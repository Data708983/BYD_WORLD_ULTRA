<p align="center">
    View this document in:<br> English | <a href="https://github.com/Data708983/BYD_WORLD_ULTRA/blob/master/README.zh-cn.md">简体中文</a>
</p>
<a herf="https://github.com/Data708983/BYD_WORLD_ULTRA">
  <p align="center"><img src="https://cdn.modrinth.com/data/cached_images/10648d8abc879ed5077c4a7cdead2e3b13f462d7.png" alt="BYD_ICON"></p>
</a>
<p align="center">Paper Plugin of Vanilla Survival Enhancement</p>

<h1 align="center">
BYD_WORLD_ULTRA
</h1>

<p align="center">
    Give us a star if you like this!
</p>

<p align="center">
  <a href="https://github.com/Data708983/BYD_WORLD_ULTRA/releases"><img src="https://img.shields.io/github/downloads/Data708983/BYD_WORLD_ULTRA/total?color=%239F7AEA" alt="Release Downloads"></a>
  <a href="#"><img src="https://img.shields.io/github/repo-size/Data708983/BYD_WORLD_ULTRA?color=6882C4" alt="GitHub Repo Size"></a>
  <a href="#"><img src="https://img.shields.io/github/last-commit/Data708983/BYD_WORLD_ULTRA?color=%23638e66" alt="Last Commit"></a>
  <a href="#"><img src="https://img.shields.io/github/release-date/Data708983/BYD_WORLD_ULTRA?color=%15" alt="Release Date"></a>
  <a href="https://skin.dataseven.fun/"><img src="https://img.shields.io/badge/server-BYD__WORLD-%23cf51c6" alt="Server"></a>
</p>
<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/MC_version-1.21.x-blue" alt="MC Version"></a>
</p>



## Introduction

**BWU** is a server-side integration plugin for Minecraft Paper servers. It is a vallina survival enhancement plugin.
> Please refer to the [Modrinth](https://modrinth.com/plugin/byd_world_ultra) version of this document as the official version; updates may not be timely.
## Features
> All features can be configured to be enabled or disabled.

### Player Teleportation

- Use `/tpa to (player name)` to teleport to a player
- Use `/tpa come (player name)` to invite a player to assist (the invited player will teleport to YOU)
- Configure whether to enable player confirmation/teleport cooldown/teleport method (including absolute position and safe position) in `tpa.yml`
- The aliases for this command are `/t` and `/tp`

### Sethome

- Use `/sethome` to set the current location as home
- Use `/tpa home` or `/tpa to[come] (your player name)` to teleport back home
- All Tpa commands share the same cooldown timer (the timeout is configurable)
- The aliases for this command is `/h`

### Broadcast Announcements

- This feature is used to display announcements when players join the game
- Supports multi-line configuration, player-specific announcements, announcement display blacklist, etc.
- Integrates PlaceholderAPI, allowing the use of placeholders in announcements

### Suicide

- Use the `/suicide` command to commit a suicide
- Permissions can be configured using a permissions plugin
- The aliases for this command are `/s` and `/kill`
### Death Backtrace

- When a player dies, the coordinates of the death location are echoed in the chat
- There will be a clickable link to teleport to the death location in the chat
> When you teleport back to your death location, you gain 3 seconds of extreme resistance

### i18n Suppot

- This plugin supports international language files, allowing you to edit or create relevant language files to customize almost all prompts! in the lang folder

## Configuration

- All configuration files (except locale files) support hot-reloading, no need to reload the server

- Remember to disable debug mode in actual use

### config.yml

```yml
# (No items yet)
```

### tpa.yml

```yml
# TPA feature toggle
enable: true

# Debug mode (independent of enable)
debug: true

# Cooldown time
cooldown: 60

# Confirmation timeout
reply: 60

# Whether to enable confirmation
confirm: true

# Teleportation method
# near (safe location within 5 blocks, default), absolute (player's absolute coordinates)
type: "near"

# Number of records
record: 50

# Cooldown of changing home location
homecooldown: 86400
```

### broadcast.yml

```yml
# Broadcast feature toggle
enable: true

# Debug mode (independent of enable)
debug: true

# List of players who will not receive announcements
blacklist:
  - "dadsad"
  - "adsddd"

# Default announcement
default:
  title: "Default notice: %player_name%"
  content:
    - "══════════════════════════════"
    - "Content Ln. 1"
    - "Content Ln. 2"
    - "Content Ln. 3  %img_neitherdoor%"
    - "Content Ln. 4 %img_heart%"
    - "══════════════════════════════"

# Player-specific announcements
player:
  - name: "example name1"
    title: "name1: Welcome to 1.21.8 !"
    content:
      - "══════════════════════════════"
      - "Content Ln. 1"
      - "Content Ln. 2"
      - "Content Ln. 3"
      - "Content Ln. 4"
      - "══════════════════════════════"
  - name: "example name2"
    title: "name2: Welcome to 1.21.8 !"
    content:
      - "══════════════════════════════"
      - "Content Ln. 1"
      - "Content Ln. 2"
      - "Content Ln. 3"
      - "Content Ln. 4"
      - "══════════════════════════════"
  - name: "testplayer"
    title: "Test Player Announcement"
    content:
      - "Content Ln. 1"
      - "Content Ln. 2"
      - "Content Ln. 3"
      - "Content Ln. 4"
```

## Dependencies

- Soft dependencies: [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)

- Recommended: [Emoji+ Resource Pack](https://modrinth.com/resourcepack/emoji-plus) for the best experience

## Building

Feel free to build it yourself when you'd like to try the latest version.

Gradle is required to build the project.

To integrate the CommandAPI, you should bulid the shadowJar:

```sh
gradle clean shadowJar
```

---

Check out the [Changelog](https://github.com/Data708983/BYD_WORLD_ULTRA/blob/master/Doc/Update/2.0.X.md).

Please report bugs and feature requests. PRs are welcome! 🙏🏻
