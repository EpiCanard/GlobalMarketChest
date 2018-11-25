# GlobalMarketChest
GUI Plugin of shop for spigot

## Dependecy
To work you will need the plugin **Vault** to handle the economy.

## Commands
    /globalmarketchest
**Open gui to select global, local or admin of your choice**

    /globalmarketchest open [group_name]
**Open global shop with the groupe_name**

    /globalmarketchest locate [item] #TODO
**Give 5 best places that sell this item**

    /globalmarketchest reload #TODO
**Reload the plugin**



## Permissions

```YAML
 # Allow to create a globalshop
globalmarketchest.globalshop.createshop

# Allow to destroy a globalshop
globalmarketchest.globalshop.destroyshop

# Allow to open a globalshop
globalmarketchest.globalshop.openshop

# Allow to create an auction inside globalshop
globalmarketchest.globalshop.createauction

# Allow to buy an auction inside globalshop
globalmarketchest.globalshop.buyauction
```