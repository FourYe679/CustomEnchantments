# CustomEnchantments 自定义附魔插件

一个功能强大的 Minecraft Paper 26.1.2 自定义附魔插件，包含 45 种自定义附魔、原版附魔等级突破、GUI 商店系统和铁砧合并增强。

## 特性

- **45 种自定义附魔**：覆盖近战武器、远程武器、工具、盔甲四大类别
- **原版附魔等级突破**：最高可突破至 225 级
- **铁砧合并增强**：原版附魔书可在铁砧中合并升级（如时运三+时运三=时运四）
- **GUI 商店系统**：分类浏览、等级选择、金币购买、确认/取消流程
- **附魔台增强**：支持突破原版等级上限
- **冷却系统**：附魔技能独立冷却，防止滥用
- **多语言支持**：中文语言文件，可自由编辑
- **Vault 经济集成**：支持金币购买附魔书

## 环境要求

| 组件 | 版本 |
|------|------|
| Minecraft | 26.1.2（Paper） |
| Java | 17 |
| Paper API | 26.1.2-R0.1 |
| Vault | 1.7.1+（经济功能必需） |

## 安装

1. 将 `CustomEnchantments-1.0.0.jar` 放入服务器的 `plugins` 目录
2. 安装 [Vault](https://www.spigotmc.org/resources/vault.34315/) 插件（如需使用商店功能）
3. 启动服务器，插件会自动生成配置文件
4. 根据需要修改 `plugins/CustomEnchantments/config.yml` 等配置文件
5. 重启服务器或使用 `/ce reload` 重载配置

## 命令

主命令：`/cenchant`（别名：`/ce`、`/customenchant`）

| 命令 | 说明 | 权限 | 默认 |
|------|------|------|------|
| `/ce apply <附魔ID> [等级]` | 给手持物品应用自定义附魔 | `cenchant.apply` | OP |
| `/ce remove <附魔ID>` | 移除手持物品上的自定义附魔 | `cenchant.remove` | OP |
| `/ce list [页码]` | 列出所有自定义附魔 | `cenchant.command` | 所有玩家 |
| `/ce give <玩家> <附魔ID> [等级]` | 给予玩家附魔书 | `cenchant.give` | OP |
| `/ce gui` | 打开附魔 GUI 菜单 | `cenchant.gui` | 所有玩家 |
| `/ce vlevel <原版附魔名> <等级>` | 修改手持物品的原版附魔等级（0=移除） | `cenchant.vlevel` | OP |
| `/ce maxlevel [等级]` | 查看/设置原版附魔最大等级 | OP | OP |
| `/ce reload` | 重载配置文件 | `cenchant.reload` | OP |

## 权限

| 权限节点 | 说明 | 默认 |
|---------|------|------|
| `cenchant.command` | 使用自定义附魔命令 | 所有玩家 |
| `cenchant.gui` | 打开附魔 GUI 菜单并购买附魔书 | 所有玩家 |
| `cenchant.apply` | 给物品应用自定义附魔 | OP |
| `cenchant.remove` | 移除物品上的自定义附魔 | OP |
| `cenchant.give` | 给予自定义附魔书 | OP |
| `cenchant.vlevel` | 修改原版附魔等级 | OP |
| `cenchant.reload` | 重载配置 | OP |
| `cenchant.bypass` | 绕过限制 | OP |

## 自定义附魔列表

### 原有附魔（8 种）

| 附魔 ID | 显示名称 | 最大等级 | 适用物品 | 冷却(秒) | 说明 |
|---------|---------|---------|---------|---------|------|
| `tnt_burst` | TNT 瞬爆 | 10 | 弓/弩 | 3 | 弓射出点燃的 TNT |
| `lightning_strike` | 闪电链 | 10 | 剑/斧/弓/弩 | 5 | 击中时召唤闪电 |
| `fire_storm` | 火焰风暴 | 10 | 剑/斧/弓/弩 | 2 | 击中时范围着火 |
| `ice_freeze` | 冰冻 | 10 | 剑/斧/弓/弩 | 3 | 击中时冻结目标 |
| `life_steal` | 吸血 | 10 | 剑/斧/弓/弩 | 0 | 击中时回复生命 |
| `explosion` | 爆炸 | 10 | 剑/斧/弓/弩 | 3 | 击中时产生爆炸 |
| `teleport_strike` | 传送打击 | 5 | 剑/斧 | 5 | 击中时传送到目标附近 |
| `magnetic` | 磁力 | 5 | 剑/斧/镐 | 0 | 击杀时自动拾取掉落物 |

### 近战武器附魔（12 种）

| 附魔 ID | 显示名称 | 最大等级 | 适用物品 | 冷却(秒) | 说明 |
|---------|---------|---------|---------|---------|------|
| `poison_blade` | 剧毒之刃 | 10 | 剑/斧 | 2 | 给目标施加中毒 |
| `wither_touch` | 凋零之触 | 10 | 剑/斧 | 3 | 给目标施加凋零 |
| `vampirism` | 吸血鬼 | 10 | 剑/斧 | 0 | 击杀时大量回血 |
| `execute` | 处决 | 10 | 剑/斧 | 2 | 目标低血量时额外伤害 |
| `cleave` | 横扫 | 10 | 剑/斧 | 3 | 对周围敌人造成伤害 |
| `thunder_slash` | 雷霆斩 | 10 | 剑/斧 | 5 | 对周围敌人召唤闪电 |
| `bleed` | 流血 | 10 | 剑/斧 | 2 | 持续造成额外伤害 |
| `knockback_master` | 击退大师 | 10 | 剑/斧 | 1 | 超强击退效果 |
| `crit_chance` | 暴击 | 10 | 剑/斧/弓/弩 | 0 | 概率造成暴击伤害 |
| `parry` | 格挡 | 10 | 剑/斧 | 3 | 被攻击时概率获得抗性 |
| `berserk` | 狂暴 | 10 | 剑/斧 | 0 | 血量越低伤害越高 |
| `soul_reap` | 灵魂收割 | 10 | 剑/斧 | 0 | 击杀时获得额外经验 |

### 远程武器附魔（6 种）

| 附魔 ID | 显示名称 | 最大等级 | 适用物品 | 冷却(秒) | 说明 |
|---------|---------|---------|---------|---------|------|
| `multishot_burst` | 多重射击 | 5 | 弓/弩 | 4 | 射出多支箭矢 |
| `homing_arrow` | 追踪箭 | 5 | 弓/弩 | 3 | 箭矢自动追踪敌人 |
| `poison_arrow` | 毒箭 | 10 | 弓/弩 | 2 | 箭矢带毒 |
| `explosive_arrow` | 爆炸箭 | 10 | 弓/弩 | 3 | 箭矢击中时爆炸 |
| `web_shot` | 蛛网射击 | 5 | 弓/弩 | 4 | 箭矢落地生成蜘蛛网 |
| `gravity_arrow` | 重力箭 | 5 | 弓/弩 | 3 | 将目标拉向自己 |

### 工具附魔（6 种）

| 附魔 ID | 显示名称 | 最大等级 | 适用物品 | 冷却(秒) | 说明 |
|---------|---------|---------|---------|---------|------|
| `vein_miner` | 连锁挖矿 | 5 | 镐 | 1 | 破坏相连的同种矿石 |
| `timber` | 砍树 | 5 | 斧 | 1 | 连锁破坏树木 |
| `auto_smelt` | 自动冶炼 | 5 | 镐 | 0 | 挖掘矿石自动熔炼 |
| `fortune_boost` | 时运增幅 | 5 | 镐 | 0 | 增加矿物掉落 |
| `excavator` | 挖掘者 | 5 | 镐/锹 | 1 | 范围挖掘 3x3 |
| `replant` | 自动补种 | 5 | 锄 | 0 | 收割后自动种植（消耗背包种子） |

### 盔甲附魔（11 种）

| 附魔 ID | 显示名称 | 最大等级 | 适用物品 | 冷却(秒) | 说明 |
|---------|---------|---------|---------|---------|------|
| `molten_armor` | 熔岩装甲 | 5 | 胸甲 | 2 | 攻击者着火 |
| `thorns_spike` | 荆棘尖刺 | 10 | 胸甲 | 1 | 反伤攻击者 |
| `speed_boost` | 速度提升 | 5 | 护腿 | 0 | 穿戴时移速提升 |
| `jump_boost` | 跳跃强化 | 5 | 靴子 | 0 | 穿戴时跳跃提升 |
| `night_vision` | 夜视 | 5 | 头盔 | 0 | 穿戴时获得夜视 |
| `regeneration` | 再生 | 5 | 头盔/胸甲 | 0 | 穿戴时缓慢回血 |
| `resistance` | 抗性 | 5 | 胸甲 | 0 | 穿戴时减少伤害 |
| `aqua_affinity` | 深海亲和 | 5 | 头盔 | 0 | 水下呼吸和快速挖掘 |
| `feather_fall` | 羽落 | 5 | 靴子 | 5 | 触发时获得缓降 |
| `invisibility` | 隐身 | 5 | 头盔 | 10 | 触发时获得隐身 |
| `experience_boost` | 经验增幅 | 5 | 剑/斧/镐 | 0 | 获得更多经验 |

### 通用附魔（2 种）

| 附魔 ID | 显示名称 | 最大等级 | 适用物品 | 冷却(秒) | 说明 |
|---------|---------|---------|---------|---------|------|
| `looting_master` | 掠夺大师 | 5 | 剑/斧 | 0 | 击杀时额外掉落 |
| `durability_blessing` | 耐久祝福 | 5 | 全部工具/武器 | 0 | 概率不消耗耐久 |

## 使用示例

### 应用自定义附魔

```
/ce apply tnt_burst 5      # 给手持弓应用 5 级 TNT 瞬爆
/ce apply life_steal 10    # 给手持剑应用 10 级吸血
```

### 修改原版附魔等级

```
/ce vlevel sharpness 100   # 手持物品锋利设为 100 级
/ce vlevel fortune 50      # 手持镐时运设为 50 级
/ce vlevel sharpness 0     # 移除锋利附魔
```

### 铁砧合并升级

原版附魔书可在铁砧中合并升级，突破原版等级上限：

| 合并组合 | 结果 |
|---------|------|
| 时运三 + 时运三 | 时运四 |     
| 时运四 + 时运四 | 时运五 |
| 时运二 + 时运三 | 时运三（取较高） |
| 锋利100 + 锋利100 | 锋利200 |
| 锋利200 + 锋利100 | 锋利225（达上限） |

合并规则：
- 相同等级原版附魔合并 → 等级 +1
- 不同等级原版附魔合并 → 取较高等级
- 超等级附魔合并 → 等级相加（上限 225）

### 给予附魔书

```
/ce give Steve tnt_burst 5   # 给 Steve 一本 5 级 TNT 瞬爆附魔书
```

### GUI 商店

所有玩家可执行 `/ce gui` 打开附魔商店，选择类别 → 选择附魔 → 选择等级 → 确认购买（消耗金币）。

## 配置文件

### config.yml

```yaml
# 是否启用插件
enabled: true

# 原版附魔最大等级 (默认225)
max-enchant-level: 225

# 是否启用铁砧修改等级上限
enable-anvil-max-level: true

# 是否启用附魔台修改等级上限
enable-enchanting-table-max-level: true

# 是否在铁砧中允许合并超出原版等级的附魔书
allow-combine-overlevel: true

# 附魔书物品设置
enchant-book-item:
  material: ENCHANTED_BOOK
  glow: true
  custom-model-data: 0

# 商店设置
shop:
  default-price: 1000  # 每个附魔每级基础价格
```

### enchantments.yml

管理所有自定义附魔的配置，可修改：
- `enabled`：启用/禁用附魔
- `display-name`：显示名称（支持颜色代码）
- `max-level`：最大等级
- `description`：描述文本
- `apply-to`：适用物品类型
- `cooldown`：冷却时间（秒）
- `price`：商店价格（可选，覆盖默认价格）

### 语言文件

- `language/zh_cn.yml`：通用消息文本
- `gui/categories.yml`：GUI 分类配置
- `gui/shop_menu.yml`：商店菜单文本
- `gui/confirm_menu.yml`：确认菜单文本

## 项目结构

```
CustomEnchantments/
├── src/main/java/com/XiaoR/customenchantments/
│   ├── CustomEnchantments.java       # 主类
│   ├── enchantments/
│   │   ├── CustomEnchantment.java    # 附魔基类
│   │   └── impl/                     # 45 个附魔实现
│   ├── gui/                          # GUI 系统
│   │   ├── EnchantGUI.java           # 主 GUI
│   │   ├── EnchantCategoryGUI.java   # 分类 GUI
│   │   ├── EnchantShopGUI.java       # 商店 GUI
│   │   └── GUIListener.java          # GUI 事件监听
│   ├── listener/                     # 事件监听器
│   │   ├── AnvilListener.java        # 铁砧合并
│   │   ├── ArmorEffectListener.java  # 盔甲附魔效果
│   │   ├── BlockBreakListener.java   # 方块破坏（连锁/砍树/补种）
│   │   ├── DurabilityListener.java   # 耐久祝福
│   │   ├── EnchantListener.java      # 附魔触发主监听
│   │   └── EnchantingTableListener.java # 附魔台
│   ├── manager/                      # 管理器
│   │   ├── EnchantManager.java       # 附魔管理
│   │   ├── EnchantCommand.java       # 命令处理
│   │   ├── CooldownManager.java      # 冷却管理
│   │   └── LanguageManager.java      # 语言管理
│   └── util/EnchantUtil.java         # 工具类
├── src/main/resources/
│   ├── plugin.yml                    # 插件描述
│   ├── config.yml                    # 主配置
│   ├── enchantments.yml              # 附魔配置
│   └── language/                     # 语言文件
└── pom.xml                           # Maven 配置
```

## 编译

```bash
# 需要 JDK 17-25
mvn clean package -DskipTests
```

生成的 JAR 包位于 `target/CustomEnchantments-1.0.0.jar`。

## 技术说明

- **数据存储**：使用 PersistentDataContainer 存储自定义附魔数据，无需外部数据库
- **事件驱动**：所有附魔效果通过 Bukkit 事件触发，性能高效
- **内存管理**：玩家退出时自动清理冷却数据，防止内存泄漏
- **任务调度**：所有延时任务正确注册和取消，避免内存泄漏
- **兼容性**：基于 Paper API 26.1.2 开发，使用 ProtocolLib 进行协议级操作

## 版本历史

- **1.0.0**：初始版本
  - 45 种自定义附魔
  - 原版附魔等级突破（最高 225）
  - 铁砧合并增强
  - GUI 商店系统
  - Vault 经济集成
  - 多语言支持

## 作者

XiaoR

## 许可

本项目仅供学习和个人使用。
