<p align="center">
    View this document in: <a href="./README.md">English</a> | 简体中文
</p>

---

<a href="https://github.com/Data708983/BYD_WORLD_ULTRA">
  <p align="center"><img src="https://github.com/Data708983/BYD_WORLD_ULTRA/blob/master/Doc/Img/neitherDoor4.png" alt="BYD_ICON"></p>
</a>
<!-- <p align="center">Vanilla Survival Enhancement Server Basic Integration Plugin</p> -->
<p align="center">原版生存加强 Paper 插件</p>
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

## 简介

**BYD_WORLD_ULTRA** 是一款服务器基础集成插件。是为了满足 [BYD_WORLD](https://skin.dataseven.fun/) 服务器的需求的一款原版生存加强 Paper 插件。

## 功能

所有功能均可配置是否启用。

### 玩家间传送

- 使用 `/Tpa to (玩家名称)` 传送至玩家
- 使用 `/Tpa come (玩家名称)` 邀请玩家协助
- 是否启用玩家确认/传送冷却/传送方式（包括绝对位置和安全位置）可在 tpa.yml 中配置

### Sethome 功能

- 使用 `/sethome` 将当前位置设置为家
- 使用 `/Tpa home` 或 `/Tpa to[come] (自己名称)` 传送回家
- 所有 Tpa 命令共享冷却
- `/sethome` 命令冷却时间可配置

### Broadcast 公告功能

- 该功能用于玩家加入游戏时展示公告
- 支持多行配置、玩家独特公告、公告显示的黑名单等功能等
- 集成 PlaceholderAPI，可在公告中使用占位符功能

### suicide 自杀指令

- 使用`/suicide`指令自杀
- 可用权限插件配置权限

### 死亡回溯功能

- 玩家死亡时，聊天框会输出玩家死亡坐标
- 玩家可点击聊天框 [点击传送] 按钮回到死亡位置，并获得3秒无敌状态

## 配置

- 所有配置文件热更新，无需 reload
- 实际使用时记得关闭 debug

### config.yml

```yml
# 暂无配置项
```

### tpa.yml

```yml
# 是否启用传送
enable: true

# debug模式（与enable无关）
debug: true

# 冷却时间
cooldown: 60

# 确认期限
reply: 60

# 是否启用确认
confirm: true

# 传送方式
# near（临近5格内安全地点，默认）、absolute（玩家绝对坐标）
type: "near"

#记录条数
record: 50

#改变家位置的冷却时间
homecooldown: 86400
```

### broadcast.yml

```yml
# 是否启用加入公告
enable: true

# 是否启用Debug（不受enable影响）
debug: true

# 不展示加入公告名单
blacklist:
  - "dadsad"
  - "adsddd"

# 默认公告
default:
  title: "Default notice: %player_name%"
  content:
    - "══════════════════════════════"
    - "第一行内容"
    - "第二行内容"
    - "第三行内容  %img_neitherdoor%"
    - "最后一行 %img_heart%"
    - "══════════════════════════════"

# 玩家特定公告
player:
  - name: "example name1"
    title: "name1: Welcome to 1.21.8 !"
    content:
      - "══════════════════════════════"
      - "第一行内容"
      - "第二行内容"
      - "第三行内容"
      - "最后一行"
      - "══════════════════════════════"
  - name: "example name2"
    title: "name2: Welcome to 1.21.8 !"
    content:
      - "══════════════════════════════"
      - "第一行内容"
      - "第二行内容"
      - "第三行内容"
      - "最后一行"
      - "══════════════════════════════"
  - name: "testplayer"
    title: "测试玩家公告"
    content:
      - "第一行内容"
      - "第二行内容"
      - "第三行内容"
      - "最后一行"
```


## 依赖

- 软依赖：[PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)

- 建议使用：[Emoji+ 资源包](https://modrinth.com/resourcepack/emoji-plus)以获得最佳使用体验

## 构建

希望及时试用最新版本可自行构建

本项目依赖 gradle 构建

```sh
gradle clean build
```

---

更新日志：https://github.com/Data708983/BYD_WORLD_ULTRA/tree/master/Doc/Update

技术能力有限，欢迎指出 bug 以及希望新增的功能，欢迎 PR 🙏🏻
