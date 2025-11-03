@file:Suppress("UnstableApiUsage")

plugins {
    id("fabric-loom")
    id("dev.kikugie.postprocess.jsonlang")
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

version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
base.archivesName = property("mod.id") as String

loom {
    accessWidenerPath = rootProject.file("src/main/resources/${property("mod.id")}.accesswidener")
}

jsonlang {
    languageDirectories = listOf("assets/${property("mod.id")}/lang")
    prettyPrint = true
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
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    mappings(loom.layered {
        officialMojangMappings()
        if (hasProperty("deps.parchment"))
            parchment("org.parchmentmc.data:parchment-${property("deps.parchment")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")
    // Kaleido
    implementation("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    include("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    modImplementation( "maven.modrinth:mcqoy:adCKjC4q")

    // Cloth Config
    if (hasProperty("deps.cloth_config")) {
        modImplementation("me.shedaniel.cloth:cloth-config-fabric:${property("deps.cloth_config")}")
    } else {
        modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:19.0.147")
    }
    // Mod Menu
    if (hasProperty("deps.modmenu"))
        modApi("com.terraformersmc:modmenu:${property("deps.modmenu")}")
    else {
        modCompileOnly("com.terraformersmc:modmenu:15.0.0-beta.3")
    }

    // Optional compat
    if (hasProperty("deps.jade")) {
        modCompileOnly("maven.modrinth:jade:${property("deps.jade")}")
        modLocalRuntime("maven.modrinth:jade:${property("deps.jade")}")
    } else {
        modCompileOnly("maven.modrinth:jade:19.3.2+fabric")
    }
    if (hasProperty("deps.badpackets_version")) {
        modCompileOnly("mcp.mobius.waila:wthit-api:fabric-${property("deps.wthit_version")}")
        modLocalRuntime("mcp.mobius.waila:wthit:fabric-${property("deps.wthit_version")}")
        modLocalRuntime("lol.bai:badpackets:fabric-${property("deps.badpackets_version")}")
    } else {
        modCompileOnly("mcp.mobius.waila:wthit-api:fabric-17.2.0")
    }
    // Development QOL
    modLocalRuntime("cc.cassian.item-descriptions:item-descriptions-fabric:${property("deps.item_descriptions")}") {
        isTransitive = false
    }

    // Recipe Viewers
    if (hasProperty("deps.eiv")) {
        modCompileOnly("maven.modrinth:eiv:${property("deps.eiv")}-fabric")
        modLocalRuntime("maven.modrinth:eiv:${property("deps.eiv")}-fabric")
    }
    if (hasProperty("deps.emi")) {
        modCompileOnly("dev.emi:emi-fabric:${property("deps.emi")}:api")
        modLocalRuntime("dev.emi:emi-fabric:${property("deps.emi")}")
    }
//    modCompileOnly("mezz.jei:jei-${property("deps.minecraft")}-fabric-api:${property("deps.jei")}")

    val modules = listOf("transitive-access-wideners-v1", "registry-sync-v0", "resource-loader-v0")
    for (it in modules) modImplementation(fabricApi.module("fabric-$it", property("deps.fabric-api") as String))
}

//fabricApi {
//    configureDataGeneration() {
//        outputDirectory = file("$rootDir/src/main/generated")
//        client = true
//    }
//}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/mods.toml")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

java {
    withSourcesJar()
    val javaCompat = if (stonecutter.eval(stonecutter.current.version, ">=1.21")) {
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
    file = tasks.remapJar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })

    type = BETA
    displayName = "${property("mod.name")} ${property("mod.version")} for ${stonecutter.current.version} Fabric"
    version = "${property("mod.version")}+${property("deps.minecraft")}-fabric"
    changelog = provider { rootProject.file("CHANGELOG-LATEST.md").readText() }
    modLoaders.add("fabric")

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
        optional("mcqoy")

    }

    curseforge {
        projectId = property("publish.curseforge") as String
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
    }
}