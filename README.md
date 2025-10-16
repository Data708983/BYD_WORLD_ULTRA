<p align="center">
    View this document in:<br> English | <a href="./README.zh-cn.md">简体中文</a>
</p>

---

<a href="https://github.com/Data708983/BYD_WORLD_ULTRA">
  <p align="center"><img src="https://github.com/Data708983/BYD_WORLD_ULTRA/blob/master/Doc/Img/neitherDoor4.png" alt="BYD_ICON"></p>
</a>
<!-- <p align="center">Vanilla Survival Enhancement Server Basic Integration Plugin</p> -->
<p align="center">Paper Plugin of Vanilla Survival Enhancement</p>
<h1 align="center">BYD_WORLD_ULTRA</h1>

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

**BYD_WORLD_ULTRA** is a server-side integration plugin for Minecraft Paper servers. It is an original survival enhancement designed to meet the needs of the [BYD_WORLD](https://skin.dataseven.fun) server.

## Features

All features can be configured to be enabled or disabled.

### Player Teleportation

- Use `/Tpa to (player name)` to teleport to a player
- Use `/Tpa come (player name)` to invite a player to assist (the invited player will teleport to YOU)
- Configure whether to enable player confirmation/teleport cooldown/teleport method (including absolute position and safe position) in `tpa.yml`.

### Sethome

- Use `/sethome` to set the current location as home
- Use `/Tpa home` or `/Tpa to[come] (your player name)` to teleport back home
- All Tpa commands share the same cooldown timer (the timeout is configurable)

### Broadcast Announcements

- This feature is used to display announcements when players join the game
- Supports multi-line configuration, player-specific announcements, announcement display blacklist, etc.
- Integrates PlaceholderAPI, allowing the use of placeholders in announcements

### Suicide

- Use the `/suicide` command to commit a suicide
- Permissions can be configured using a permissions plugin

### Death Backtrace

- When a player dies, the coordinates of the death location are echoed in the chat
- There will be a clickable link to teleport to the death location in the chat

### i18n Suppot

- This plugin supports international language files, allowing you to edit or create relevant language files to customize almost all prompts! in the lang folder

## Configuration

- All configuration files support hot-reloading, no need to reload the server

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

- Soft dependency: [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)

- Recommended: [Emoji+ Resource Pack](https://modrinth.com/resourcepack/emoji-plus) for the best experience

## Building

Feel free to build it yourself when you'd like to try the latest version.

Gradle is required to build the project.

```sh
gradle clean build
```

---

Check out the [Changelog](https://github.com/Data708983/BYD_WORLD_ULTRA/tree/master/Doc/Update).

My technical skills are limited, so please report bugs and feature requests. PRs are welcome! 🙏🏻
