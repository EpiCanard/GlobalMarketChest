# GlobalMarketChest
GUI Plugin of shop for spigot

## Dependecy
To work you will need the plugin **Vault** to handle the economy.

## Commands

**Help**

Show help

`/globalmarketchest [help]`


**List**

List all globalshop

`/globalmarketchest list`


**Detail**

List all physical globalshop à distance

`/globalmarketchest list detail <group_name>`


**Open**

Allow to open a globalshop

`/globalmarketchest open <group_name>`


**TP**

Allow to teleport the player a specific shop position

`/globalmarketchest list tp <group_name> <coordinates>`


#TODO

**Reload the plugin**

`/globalmarketchest reload`

## Permissions

```YAML
# Can use all commands and shops
globalmarketchest.*

# ==== Global Shop ====

# Can create or use a globalshop
globalmarketchest.globalshop.*

# Can create a globalshop
globalmarketchest.globalshop.createshop

# Can open shop
globalmarketchest.globalshop.openshop

# Can create an auction
globalmarketchest.globalshop.createauction

# Can create buy an auction
globalmarketchest.globalshop.buyauction

# ==== COMMANDS ===

# Can use all commands and shops
globalmarketchest.commands.*

# Can reload the plugin
globalmarketchest.commands.reload

# Can open a shop with command
globalmarketchest.commands.open

# Can list all shops with command
globalmarketchest.commands.list.*

# Can see all shop position for a group of shop
globalmarketchest.commands.detail.*

# Can be teleport to a specific shop
globalmarketchest.commands.detail.tp

```