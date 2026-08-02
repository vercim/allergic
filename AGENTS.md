# Repository Guidelines

## Project Structure & Module Organization

This is a Fabric mod for Minecraft 1.21.1, built with Gradle and Java 21. Production code lives in `src/main/java/dev/vercim/allergic/`. Keep features grouped by responsibility: `allergy/` contains generation and persistent player data, `command/` contains Brigadier commands, `config/` owns configuration, `registry/` registers game content, and `mixin/` contains mixins.

Runtime metadata and assets are in `src/main/resources/`: `fabric.mod.json`, mixin configuration, translations under `assets/allergic/lang/`, and textures/sounds under `assets/allergic/`. Source artwork belongs in `artwork/`; generated output in `build/` and local run data in `run/` are not committed.

## Build, Test, and Development Commands

Use the Gradle wrapper from PowerShell:

- `./gradlew.bat build` compiles the mod, processes resources, and creates the distributable JAR in `build/libs/`.
- `./gradlew.bat runClient` starts a configured Minecraft client for manual testing.
- `./gradlew.bat test` runs unit tests when they are added.
- `./gradlew.bat clean build` performs a clean verification build before release-oriented changes.

## Coding Style & Naming Conventions

Follow the existing Java style: four-space indentation, braces on the same line, one import per line, and no wildcard imports. Use `PascalCase` for classes, `camelCase` for fields and methods, and `UPPER_SNAKE_CASE` for constants such as `MOD_ID`. Keep classes focused, make utility-only classes `final` with private constructors, and use `AllergicMod.LOGGER` for mod logging. Preserve UTF-8 resource files and use lowercase snake-case locale file names (for example, `en_us.json`).

## Testing Guidelines

There is currently no committed `src/test` tree. Add focused JUnit tests under `src/test/java` for logic that does not require a live Minecraft client; name them `*Test` and run `./gradlew.bat test`. Exercise gameplay changes manually with `runClient`, including fresh-player allergy generation, commands, saved-world persistence, and affected food items.

## Commit & Pull Request Guidelines

Recent history uses short, imperative subjects without prefixes, such as `Add audible sneezing and visible allergy symptoms`. Keep each commit scoped to one change. Pull requests should explain behavior changes, link relevant issues, note Minecraft/Fabric compatibility impacts, and include screenshots or brief reproduction steps for visible or in-game behavior. Do not commit local `config/allergic.json` files or generated artifacts.
