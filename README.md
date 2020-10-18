![banner](https://user-images.githubusercontent.com/29657498/52167675-07369d80-271f-11e9-86ff-362db527f854.png)

GlobalMarketChest is a spigot plugin that allows to easily create global shops to sell and buy auctions. You interact with shops through graphical interfaces. It can be easily configured and customized.

![dependencies](https://user-images.githubusercontent.com/29657498/52743417-45af4080-2fda-11e9-8be7-4b645e77e898.png)

To work you will need :
- **Vault**
- An **economy plugin** compatible with Vault to manage your economy


*Soft Dependencies* :
- If you want to use MySQL instead of default SQLite you will need to install a MySQL Server by yourself.

![features](https://user-images.githubusercontent.com/29657498/52743424-4ba52180-2fda-11e9-8fe1-dded69d0dd85.png)

 - User friendly shop graphical interfaces
 - Fluid graphical interfaces
 - Simple and instinctive shop creation process
 - A unique place to sell/buy items and improve interaction between players
 - Smart sorting system to see first cheapest auctions and than oldest
 - Auction search (By similar items, item name or player name)
 - Paginator to see auctions 27 by 27
 - Easy process to create / buy auction
 - Easy price selector
 - Advanced feature to repeat the auction that you are creating as many as possible or one by one
 - Advanced feature to put all similar items of your inventory  in the auction that you are creating (you can go higher than 64 items)
 - Advanced categories system by default inspired by creative tabs but can be totally customized
 - List of the last 24 hours of auctions created
 - Detailed permissions system
 - Complete commands to list, teleport to, open and close shops (with autocompletion)
 - Ranks system to configure some properties to specific players
 - High configurability
   - All items composing interfaces can be changed (item used, title and lore)
   - Language can easily be configured
   - Categories totally configurable (display item, title, position, items contains, GroupLevels)
   - Many features can be configured/disabled
 - Multi database support. The plugin actually support SQLite database (local) and MySQL database (remote)
 - Admin features to remove player auctions

**Technical features :**

- Highly configurable interfaces through config file
- Interface loader that auto adapt to available interfaces
- Database schema is automatically updated if needed

**In coming features**

- Multi database support (yml, postgresql, mongodb)
- AuctionShop (maybe another name) - Here roles are reversed, the player ask for an item and a quantity he need and players come to sell to him the wanted item
- AdminShop - Here players can find all item they want infinitely.
- LocalShop - With this shop only the owner can sell auctions in it.
- Maybe the split of this plugin in multiple little plugins (core, globalshop,adminshop,...)  to improve scalability of this plugin

List of next features : [https://trello.com/b/VsJRVqVn/globalmarketchest](https://trello.com/b/VsJRVqVn/globalmarketchest)

![installation](https://user-images.githubusercontent.com/29657498/52743428-4ea01200-2fda-11e9-94eb-3f9bba72e926.png)

1. Download the file GlobalMarketChest.jar
2. Add the file in the folder plugins of your server.
3. Launch your server, it will generate all necessary configuration files in plugins/GlobalMarketChest. The plugin is now enable.

If you choose to change Storage.type to mysql extra more steps will follow :

1. The plugin will disable from itself because your database is not configured.
2. In file plugins/GlobalMarketChest/config.yml change the database informations in variable Storage.Connection.See https://github.com/EpiCanard/GlobalMarketChest/wiki/resources-:-config.yml#storage
3. Reload the plugin or server
4. If your database informations are correct, the plugin should now work :)

If you have any issue don't hesitate to come on Discord (https://discord.gg/UuGAcCa) to ask for help.

![documentation](https://user-images.githubusercontent.com/29657498/52743420-48119a80-2fda-11e9-8076-582a7617be7c.png)

You can find all information that you need to use and configure this plugin in the [wiki](https://github.com/EpiCanard/GlobalMarketChest/wiki).

![support](https://user-images.githubusercontent.com/29657498/52744544-439ab100-2fdd-11e9-8ec8-b18edd602689.png)

If you find a bug or want to make a suggestion to improve the plugin please open an [issue](https://github.com/EpiCanard/GlobalMarketChest/issues) on GitHub.

If you want to participate to development of this plugin, fork the GitHub, make your modifications and open a Pull Request.

If you have any question, you can contact me on discord.

You will find all development build on discord, so don't hesitate to come.

Discord: [https://discord.gg/TndZcuy](https://discord.gg/TndZcuy)

Source GitHub: [https://github.com/EpiCanard/GlobalMarketChest](https://github.com/EpiCanard/GlobalMarketChest)

Spigot : [https://www.spigotmc.org/resources/globalmarketchest-gui-shop-plugin.64921/](https://www.spigotmc.org/resources/globalmarketchest-gui-shop-plugin.64921/)

Metrics : [bstats.org](https://bstats.org/plugin/bukkit/GlobalMarketChest/7557)

If you want to test work in progress features you will find all development builds on Jenkins.
Jenkins: [https://www.globalmarketchest-jenkins.fr](https://www.globalmarketchest-jenkins.fr)

![screenshots](https://user-images.githubusercontent.com/29657498/52745804-4ea31080-2fe0-11e9-8604-8e5081c75605.png)

![buy_auction](https://user-images.githubusercontent.com/29657498/52537867-c1578600-2d6b-11e9-8657-7f4efb43ac27.gif)

![create_auction](https://user-images.githubusercontent.com/29657498/52538701-fcf74d80-2d75-11e9-972b-de74812f337b.gif)

![edit_auction_overview](https://user-images.githubusercontent.com/29657498/52537956-e993b480-2d6c-11e9-9c5c-1316c9908de7.gif)

![using](https://user-images.githubusercontent.com/29657498/52744655-970cff00-2fdd-11e9-9dbe-697f46eafd12.png)

![search](https://user-images.githubusercontent.com/29657498/54498025-ef4b5f80-4901-11e9-8a8e-f29b76815d7e.gif)

- Freebuild.fr - play.freebuild.fr [website](https://freebuild.fr)
- Politicraft - play.politicraft.fr

If you are using GlobalMarketChest on your server and your server is public you can leave a review with your server name and ip.

![thanks](https://user-images.githubusercontent.com/29657498/52743433-5069d580-2fda-11e9-8d34-ae14557c1311.png)

I would like to thanks :

- The server Freebuild.fr that helps me during development of this plugin.
- The player @Icodak that make the logo of this plugin.
- @Coby_Cola for the Chinese translation.
- @Drynael for the German translation.
- @Ranqstrail for the Polish translation.
- @cvary61 for the Turkish translation.
- @VStitch for the Russian translation.


### Donation
If you want to donate for work done, it will be a pleasure. Thank you.

[www.paypal.me/globalmarketchest42](https://www.paypal.me/globalmarketchest42)
