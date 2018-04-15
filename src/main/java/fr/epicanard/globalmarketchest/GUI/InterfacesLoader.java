package fr.epicanard.globalmarketchest.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.Utils;

public class InterfacesLoader {
	private Map<String, Object> buttons;

	public InterfacesLoader() {
		this.buttons = GlobalMarketChest.plugin.getConfigLoader().getConfig().getConfigurationSection("Interfaces.Buttons").getValues(false);
	}
	
	public Map<String, ItemStack[]> loadInterfaces(YamlConfiguration interfaceConfig) {
		Map<String, ItemStack[]> interfaces = new HashMap<String, ItemStack[]>();
		Set<String> interfacesName = interfaceConfig.getKeys(false);

		for (String name: interfacesName) {
			ItemStack[] itemsStack = new ItemStack[54];
			Map<Integer, String> items = this.parseItems(interfaceConfig.getConfigurationSection(name + ".Items").getValues(false));

			for(int i = 0; i < 54; i++)
				itemsStack[i] = Utils.getButton(items.get(i));
			interfaces.put(name, itemsStack);
		}
		return interfaces;
	}
	
	private Map<Integer, String> parseItems(Map<String, Object> its) {
		Map<Integer, String> items = new HashMap<Integer, String>();
		for (Map.Entry<String, Object> entry : its.entrySet()) {
			try {
				items.put(Integer.parseInt(entry.getKey()), (String)entry.getValue());
			} catch (NumberFormatException e) {
				String[] nums = entry.getKey().split("-");
				for(Integer i = Integer.parseInt(nums[0]); i <= Integer.parseInt(nums[1]); i++) {
					items.put(i, (String)entry.getValue());
				}
			}
		}
		return items;
	}

}
