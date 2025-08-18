# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- A config option for whether throwing an item into a vanilla Cauldron should convert it into a modded Cauldron and insert the item into its inventory.

## [1.3.0] - 2025-08-17

### Added
- Particles and heat requirements are now part of the recipe format.
- Brewing input and results no longer need to be a potion, specifying `id: "minecraft:lava_cauldron` for your `potion` or `result` will use that block as the result.

### Fixed
- Duplicated item list in WTHIT/Jade.

## [1.2.0] - 2025-08-14

### Added
- Data-driven dipping recipes, the format of which is documented on this mod's Modded Minecraft Wiki.
- Hoppers can now extract items from Cauldrons.
- Throwing items into vanilla Cauldrons now properly converts them into Brewing Cauldrons.

### Changed
- Standing in cauldrons now gives you the effect for as long as you are standing in them, without ever draining the Cauldron.

### Fixed
- Strange behaviour from throwing items into Cauldrons.
- Items retrieved from cauldrons are now properly placed in your hand.

## [1.1.2] - 2025-08-10

### Changed
- Players in Adventure mode can no longer drain cauldrons by standing in them.
- Temporarily disabled splash sound effect.

### Fixed
- Cauldron heating works as intended.
- Custom potion brewing works as intended.
- Inserted items are no longer consumed in Creative.

## [1.1.1] - 2025-08-10

### Fixed
- Crash on startup on NeoForge.

## [1.1.0] - 2025-08-10

### Added
- Cauldron brewing recipes are now data-driven. Cauldrons prefer their own data-driven recipes, but will use recipes meant for the brewing stand if present. You can also disable this behaviour to solely have Cauldrons use their own recipes.
- Data-driven potions are integrated with [EMI](https://modrinth.com/mod/emi) and [EIV](https://modrinth.com/mod/eiv).
- Added support for [WTHIT](https://modrinth.com/mod/wthit).

### Changed
- Changed Tipped Arrow dipping to closer match Bedrock Edition.

### Fixed
- Cauldrons now drop their contents when broken.

## [1.0.0] - 2025-08-08

Initial release.