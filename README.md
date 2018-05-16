# GlobalMarketChest
GUI Plugin of shop for spigot

## Dependecy
To work you will need the plugin **Vault** to handle the economy.

## Commands
    /globalmarketchest
**Open gui to select global, local or admin of your choice**

    /globalmarketchest locate [item]
**Give 5 best places that sell this item**

    
    /globalmarketchest reload
**Reload the plugin**

    /globalmarketchest purge  [local | admin | global | all] [group_name]
**Delete local, admin, global or all shops in groupe_name**
- if kind of shop is not specified it apply to all kind of shops by default
- if group_name is not specified it apply to all shop by default

## Permissions
    globalmarketchest.localshop.create
    globalmarketchest.localshop.use_command
    globalmarketchest.localshop.use_chest
    globalmarketchest.localshop.
    globalmarketchest.globalshop.create
    globalmarketchest.globalshop.use
    globalmarketchest.adminshop.create
    globalmarketchest.adminshop.use
