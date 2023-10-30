### Note
Currently there is a bug where crimson swipe causes kills to be double counted.
It seems to double combat xp and rolls for loot. The amount of kills shown by the mod
includes these "virtual" kills, despite the fact that they don't count toward bestiary.
Combat xp, drops, and all the calculations are still meaningfully accurate but kills
will not be. If I have the time, I may try to implement a fix.

# GhostTracker
### Preview
![preview](./ghost%20tracker%20preview.png)

### Features
- HUD that tracks total (non-timed) stats like drops and kills
- HUD that tracks stats of a timed session like kills/hr and combat xp/hr
- Note: Both HUDs are displayed and tracked separately
### Command usage
- /ghost - Opens the config
- /ghost <start/pause/reset> - Starts/pauses/resets the timed tracker
- /ghost stats reset - Resets stats from the non-timed tracker
### Credits
- [FurfSky Reborn](https://furfsky.net/) for the sword and clock texture
- [Official Skyblock Wiki](https://wiki.hypixel.net/) for most of the other textures
- [DulkirMod](https://github.com/inglettronald/DulkirMod) for ScoreboardUtils
- [GhostCounterV3](https://www.chattriggers.com/modules/v/GhostCounterV3) for some of the kill tracking logic
