# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- Players in Adventure mode can no longer drain cauldrons by standing in them.

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