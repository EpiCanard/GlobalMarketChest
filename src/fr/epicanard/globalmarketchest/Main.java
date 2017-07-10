package fr.epicanard.globalmarketchest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import fr.epicanard.globalmarketchest.comands.CommandGMC;
import fr.epicanard.globalmarketchest.database.SQLConnection;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;
import fr.epicanard.globalmarketchest.listeners.GUIListeners;
import fr.epicanard.globalmarketchest.listeners.WorldListeners;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class Main extends JavaPlugin {

  private ConfigLoader loader;
  private SQLConnection sql;

  public Main() {
    // Initialisation of loader
    this.loader = new ConfigLoader(this);
  }

  @Override
  public void onEnable() {

    // Load Configurations files
    this.loader.loadFiles();
    try {
      this.sql = new SQLConnection("jdbc:mysql://", this.loader.getConfig());
      Connection con = this.sql.connect();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SHOW TABLES IN " + this.loader.getConfig().getString("Connection.database"));
      while (rs.next()) {
        System.out.println(rs.getString(1));
      }
    } catch (ConfigException | SQLException e) {
      this.getLogger().log(Level.WARNING, e.getMessage());
      this.getLogger().log(Level.WARNING, "Without a correct database config the plugin can't work");
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    MinecraftKey mk = new MinecraftKey("minecraft:log");
    ItemStack item = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(mk));
    Material mat = item.getType();
    System.out.println(mat.toString());

    getCommand("gmc").setExecutor(new CommandGMC());
    getServer().getPluginManager().registerEvents(new GUIListeners(), this);
    getServer().getPluginManager().registerEvents(new WorldListeners(), this);
  }
}
