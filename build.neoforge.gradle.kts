plugins {
    id("net.neoforged.moddev")
    id ("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
}

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["version"] = prop("mod.version") + "+" + prop("deps.minecraft")
        this["minecraft"] = prop("deps.minecraft")
    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
base.archivesName = property("mod.id") as String

jsonlang {
    languageDirectories = listOf("assets/${property("mod.id")}/lang")
    prettyPrint = true
}

neoForge {
    version = property("deps.neoforge") as String
    validateAccessTransformers = true

    if (hasProperty("deps.parchment")) parchment {
        val (mc, ver) = (property("deps.parchment") as String).split(':')
        mappingsVersion = ver
        minecraftVersion = mc
    }

    runs {
        register("client") {
            gameDirectory = file("run/")
            client()
        }
        register("server") {
            gameDirectory = file("run/")
            server()
        }
    }

    mods {
        register(property("mod.id") as String) {
            sourceSet(sourceSets["main"])
        }
    }
    sourceSets["main"].resources.srcDir("src/main/generated")
}


repositories {
    mavenLocal()
    maven("https://maven.shedaniel.me/") {
        name = "shedaniel (Cloth Config)"
    }
    maven("https://maven.terraformersmc.com/releases/") {
        name = "Terraformers (Mod Menu)"
    }
    maven("https://repo.sleeping.town/") {
        name = "Sisby Maven"
    }
    maven("https://maven.parchmentmc.org") {
        name = "Parchment Mappings"
    }
    maven("https://maven.parchmentmc.org") {
        name = "Parchment Mappings"
    }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
    }
    maven("https://maven.architectury.dev") {
        name = "REI Maven"
    }
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") {
        name = "Fuzs Mod Resources"
    }
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
    }
    maven("https://maven.blamejared.com/") {
        // location of the maven that hosts JEI files since January 2023
        name = "Jared's maven"
    }
    maven("https://modmaven.dev/") {
        // location of a maven mirror for JEI files, as a fallback
        name = "JEI"
    }
    maven ( "https://maven2.bai.lol" ) {
        name = "WTHIT"
    }
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    // Kaleido
    implementation("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    jarJar("folk.sisby:kaleido-config:${property("deps.kaleido")}")
//    "additionalRuntimeClasspath"("folk.sisby:kaleido-config:${property("deps.kaleido")}")

    // mcqoy
    implementation("maven.modrinth:mcqoy:yHGo6VsD")

    // Cloth Config
    if (hasProperty("deps.yacl")) {
        implementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")}-neoforge")
        compileOnly("thedarkcolour:kotlinforforge-neoforge:5.10.0")
    } else {
        compileOnly("dev.isxander:yet-another-config-lib:3.7.1+1.21.6-neoforge")
    }

    // Optional compat
    if (hasProperty("deps.jade")) {
        compileOnly("maven.modrinth:jade:${property("deps.jade")}")
        runtimeOnly("maven.modrinth:jade:${property("deps.jade")}")
    } else {
        compileOnly("maven.modrinth:jade:19.3.2+neoforge")
    }
    if (hasProperty("deps.badpackets_version")) {
        compileOnly("mcp.mobius.waila:wthit-api:neo-${property("deps.wthit_version")}")
        runtimeOnly("mcp.mobius.waila:wthit:neo-${property("deps.wthit_version")}")
        runtimeOnly("lol.bai:badpackets:neo-${property("deps.badpackets_version")}")
    } else {
        compileOnly("mcp.mobius.waila:wthit-api:neo-17.2.0")
    }
    // Development QOL
    runtimeOnly("cc.cassian.item-descriptions:item-descriptions-neoforge:${property("deps.item_descriptions")}")   {
        isTransitive = false
    }

    // Recipe Viewers
    compileOnly("maven.modrinth:eiv:${property("deps.eiv")}-neoforge")
    compileOnly("me.shedaniel:RoughlyEnoughItems-api-neoforge:${property("deps.rei")}")
    compileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-neoforge:${property("deps.rei")}")
    compileOnly("mezz.jei:jei-${property("deps.minecraft")}-neoforge:${property("deps.jei")}")

}

tasks {
    processResources {
        exclude("**/fabric.mod.json", "**/*.accesswidener", "**/mods.toml")
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

java {
    withSourcesJar()
    val javaCompat = if (stonecutter.eval(stonecutter.current.version, ">=1.20.5")) {
        JavaVersion.VERSION_21
    } else {
        JavaVersion.VERSION_17
    }
    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

publishMods {
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

    type = BETA
    displayName = "${property("mod.name")} ${property("mod.version")} for ${stonecutter.current.version} NeoForge"
    version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
    changelog = provider { rootProject.file("CHANGELOG-LATEST.md").readText() }
    modLoaders.add("neoforge")

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)
        optional("mcqoy")
    }

    curseforge {
        projectId = property("publish.curseforge") as String
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)
    }
}