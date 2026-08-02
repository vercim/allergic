# Allergic

Allergic is a server-compatible Fabric mod for Minecraft 1.21.1. Each player receives an individual, world-specific random set of food allergens on first join. Finishing an allergenic food applies a harmful custom Allergy effect together with hidden nausea, blindness, and weakness symptoms.

## Requirements

- Java 21 or newer
- Minecraft 1.21.1
- Fabric Loader 0.16.0 or newer
- Fabric API for Minecraft 1.21.1

## Build

On Windows:

```powershell
.\gradlew.bat build
```

On Linux or macOS:

```bash
./gradlew build
```

The distributable JAR is written to `build/libs/allergic-1.0.0.jar`.

## Development environment

Generate IDE sources and import the Gradle project, or start directly with:

```powershell
.\gradlew.bat runClient
```

For a dedicated development server:

```powershell
.\gradlew.bat runServer
```

The development server requires accepting Mojang's EULA in `run/eula.txt` before the second launch.

## Configuration

The first launch creates `config/allergic.json`:

```json
{
  "allergyChance": 0.3,
  "allergyDurationSeconds": 20,
  "damageIntervalTicks": 40,
  "damageAmount": 1.0
}
```

Invalid values are replaced with safe defaults and the sanitized file is written back. Configuration changes affect automatic generation for players who do not yet have saved data and explicit `/allergy regenerate` operations. Existing saved sets are never regenerated automatically.

When both [Mod Menu](https://modrinth.com/mod/modmenu) and [Cloth Config API](https://modrinth.com/mod/cloth-config) are installed on the client, the Mod Menu entry exposes every configuration value in an in-game settings screen. Neither dependency is required for servers or for normal mod operation.

## Operator commands

- `/allergy list`
- `/allergy regenerate`
- `/allergy check <item>`
- `/allergy add <item>`
- `/allergy remove <item>`

All commands require permission level 2 and must be run by a player because they operate on the executing player's allergy set.

## Persistence

Allergy sets are stored in the overworld persistent-state file under the world's `data` directory. The state maps player UUIDs to sorted string identifiers such as `minecraft:apple`. It contains no raw registry IDs or serialized `Item` instances. Missing or malformed identifiers are ignored when the state loads.

## Food discovery

The catalog scans the complete item registry. An item is considered edible when its default stack has the Minecraft 1.21.1 `FOOD` data component or reports the `EAT` use action. This covers vanilla foods and ordinary modded foods without a hand-maintained list.

## Implementation note

Fabric API does not expose a general event for every completed item use in 1.21.1. A single mixin injects after `ItemStack.finishUsing`, the common completion point used after the full use duration. Starting and cancelling consumption never reaches this hook.
