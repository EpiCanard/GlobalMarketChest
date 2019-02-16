![banner](https://user-images.githubusercontent.com/29657498/52167675-07369d80-271f-11e9-86ff-362db527f854.png)

GlobalMarketChest is a spigot plugin that allows to easily create global shops to sell and buy auctions. You interact with shops through graphical interfaces. It can be easily configured and customized.

![dependencies](https://user-images.githubusercontent.com/29657498/52743417-45af4080-2fda-11e9-8be7-4b645e77e898.png)

To work you will need the plugin **Vault** to handle the economy.

While there is no other storage system you will also need **MySQL**.

![features](https://user-images.githubusercontent.com/29657498/52743424-4ba52180-2fda-11e9-8fe1-dded69d0dd85.png)

 - User friendly shop graphical interfaces
 - Fluid graphical interfaces
 - Simple and instinctive shop creation process
 - A unique place to sell/buy items and improve interaction between players
 - Smart sorting system to see first cheapest auctions and than oldest
 - Paginator to see auctions 27 by 27
 - Easy process to create / buy auction
 - Easy price selector
 - Advanced feature to repeat the auction that you are creating as many as possible
 - Advanced feature to put all similar items of your inventory  in the auction that you are creating (you can go higher than 64 items)
 - Advanced categories system by default inspired by creative tabs but can be totally customized
 - Detailed permissions system
 - Complete commands to list, teleport to and open shops (with autocompletion)
 - High configurability
   - All items composing interfaces can be changed (item used, title and lore)
   - Language can easily be configured
   - Categories totally configurable (display item, title, position, items contains, GroupLevels)
    - Many features can be configured/disabled

**Technical features :**

- Highly configurable interfaces through config file
- Interface loader that auto adapt to available interfaces

**In coming features**

- Multi database support (yml, postgresql, SQLite, mongodb)
- AuctionShop (maybe another name) - Here roles are reversed, the player ask for an item and a quantity he need and players come to sell to him the wanted item
- AdminShop - Here players can find all item they want infinitely.
- LocalShop - With this shop only the owner can sell auctions in it.
- Maybe the split of this plugin in multiple little plugins (core, globalshop,adminshop,...)  to improve scalability of this plugin

![installation](https://user-images.githubusercontent.com/29657498/52743428-4ea01200-2fda-11e9-94eb-3f9bba72e926.png)

1. Download the file **GlobalMarketChest.jar**
2. Add the file in the folder **plugins** of your server.
3. Launch your server, it will generate all necessary configuration files in **plugins/GlobalMarketChest**. The plugin will disable from itself because your database is not configured.
4. In file **plugins/GlobalMarketChest/config.yml** change the database informations in variable **Database**. See [wiki](https://github.com/EpiCanard/GlobalMarketChest/wiki/resources-:-config.yml#database).
5. Reload the plugin or server
6. If your database informations are correct, the plugin should now work :) If it's not the case don't hesitate to come on Discord ([https://discord.gg/UuGAcCa](https://discord.gg/UuGAcCa)) to ask for help.

![documentation](https://user-images.githubusercontent.com/29657498/52743420-48119a80-2fda-11e9-8076-582a7617be7c.png)

You can find all information that you need to use and configure this plugin in the [wiki](https://github.com/EpiCanard/GlobalMarketChest/wiki).

![support](https://user-images.githubusercontent.com/29657498/52744544-439ab100-2fdd-11e9-8ec8-b18edd602689.png)

If you find a bug or want to make a suggestion to improve the plugin please open an [issue](https://github.com/EpiCanard/GlobalMarketChest/issues) on GitHub.

If you want to participate to development of this plugin, fork the GitHub, make your modifications and open a Pull Request.

If you have questions, you can contact me on discord.
Discord: [https://discord.gg/UuGAcCa](https://discord.gg/UuGAcCa)

Source GitHub: [https://github.com/EpiCanard/GlobalMarketChest](https://github.com/EpiCanard/GlobalMarketChest)

![screenshots](https://user-images.githubusercontent.com/29657498/52745804-4ea31080-2fe0-11e9-8604-8e5081c75605.png)

![buy_auction](https://user-images.githubusercontent.com/29657498/52537867-c1578600-2d6b-11e9-8657-7f4efb43ac27.gif)

![create_auction](https://user-images.githubusercontent.com/29657498/52538701-fcf74d80-2d75-11e9-972b-de74812f337b.gif)

![edit_auction_overview](https://user-images.githubusercontent.com/29657498/52537956-e993b480-2d6c-11e9-9c5c-1316c9908de7.gif)

![using](https://user-images.githubusercontent.com/29657498/52744655-970cff00-2fdd-11e9-9dbe-697f46eafd12.png)

- Freebuild.fr - play.freebuild.fr [website](https://freebuild.fr)

![thanks](https://user-images.githubusercontent.com/29657498/52743433-5069d580-2fda-11e9-8d34-ae14557c1311.png)

I would like to thanks the server Freebuild.fr that helps me during development of this plugin.

I would like to thanks the player Icodak that make the logo of this plugin.

### Donation
If you want to donate for work done, it will be a pleasure. Thank you.

[www.paypal.me/globalmarketchest42](https://www.paypal.me/globalmarketchest42)
