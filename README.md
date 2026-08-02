# Allergic

Allergic is a Minecraft mod that adds food allergies. When a player first joins a world, they receive their own randomly generated set of food allergens.

Eating an allergenic food applies the Allergy effect, which causes Weakness, Slowness, Mining Fatigue, periodic sneezing that deals damage and briefly overlays the screen, and occasional nausea. Eating another allergen refreshes the effect duration. The mod supports both vanilla food and food added by other mods.

## Commands

- `/allergy list [targets]` — shows the allergens for you or the selected player(s).
- `/allergy regenerate [targets]` — generates a new random allergy list for you or the selected player(s).
- `/allergy sneeze [targets]` — makes you or the selected player(s) sneeze, including the configured sneeze damage and screen overlay.
- `/allergy add <item> [targets]` — adds an item to your or the selected player(s)' allergy list.
- `/allergy remove <item> [targets]` — removes an item from your or the selected player(s)' allergy list.

`targets` accepts a player name or selector such as `@p`, `@a`, or `@a[distance=..10]`. Omitting it keeps the original behavior and applies the command to yourself. Commands that modify allergy data or trigger sneezing still require operator permission level 2.
