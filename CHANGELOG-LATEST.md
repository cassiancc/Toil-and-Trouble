### Added
- Dipping recipes have been upgraded into the more comprehensive Alchemy Recipes.
  - The new recipe ID is `toil_and_trouble:alchemy`. Existing recipes will continue to work.
  - Alchemy recipes support up to nine reagents.
  - Alchemy recipes can set `copy_components` to `true` to copy the components of the first reagent to the result.
- Clicking on a cauldron now shows its contents.
- Milk cauldrons, which clear all effects from players that step inside.

### Changed
- General cleanup for 26.1, include the removal of compatibility code for 1.21.11 and below, as these versions are now considered discontinued.

### Fixed
- Errors loading recipes on 26.1.
- Recipes that require heat now indicate so in RRV.