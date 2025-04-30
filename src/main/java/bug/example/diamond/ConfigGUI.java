package bug.example.diamond;

import org.bukkit.Bukkit; import org.bukkit.Material; import org.bukkit.Sound; import org.bukkit.configuration.file.FileConfiguration; import org.bukkit.entity.Player; import org.bukkit.event.EventHandler; import org.bukkit.event.Listener; import org.bukkit.event.inventory.InventoryClickEvent; import org.bukkit.event.inventory.InventoryCloseEvent; import org.bukkit.inventory.Inventory; import org.bukkit.inventory.ItemStack; import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ConfigGUI implements Listener {


    private final Diamond plugin;

    public ConfigGUI(Diamond plugin) {
        this.plugin = plugin;
    }

    public void openConfigGUI(Player player) {
        FileConfiguration config = plugin.getConfig();
        Inventory gui = Bukkit.createInventory(null, 18, "Config Settings");

        // Add toggle buttons
        gui.setItem(0, createToggleItem(config.getBoolean("enable-title"), "Toggle Title", "Click to toggle title display"));
        gui.setItem(1, createToggleItem(config.getBoolean("enable-actionbar"), "Toggle ActionBar", "Click to toggle action bar display"));
        gui.setItem(2, createToggleItem(config.getBoolean("enable-sound-levelup"), "Toggle Caught Sound", "Click to toggle fish caught sound"));
        gui.setItem(3, createToggleItem(config.getBoolean("enable-sound-splash"), "Toggle Splash Sound", "Click to toggle splash sound"));
        gui.setItem(4, createToggleItem(config.getBoolean("enable-sound-fail"), "Toggle Fail Sound", "Click to toggle fail sound"));
        gui.setItem(5, createToggleItem(config.getBoolean("enable-bobber-movement"), "Toggle Bobber Movement", "Click to toggle bobber movement"));
        gui.setItem(6, createToggleItem(config.getBoolean("teleport-fish-to-inventory"), "Toggle Fish Teleport", "Click to toggle fish teleport to inventory"));

        // Exit button
        gui.setItem(17, createItem(Material.BARRIER, "Exit Config", null));

        // Play open sound
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
        player.openInventory(gui);
    }

    private ItemStack createToggleItem(boolean enabled, String name, String lore) {
        Material material = enabled ? Material.GREEN_CONCRETE : Material.RED_CONCRETE;
        return createItem(material, name, lore);
    }

    private ItemStack createItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(Collections.singletonList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Config Settings")) {
            return;
        }

        event.setCancelled(true); // Prevent item movement
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        String displayName = clickedItem.getItemMeta().getDisplayName();
        FileConfiguration config = plugin.getConfig();

        // Play click sound
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

        switch (displayName) {
            case "Toggle Title":
                toggleConfigOption(player, config, "enable-title");
                break;
            case "Toggle ActionBar":
                toggleConfigOption(player, config, "enable-actionbar");
                break;
            case "Toggle Caught Sound":
                toggleConfigOption(player, config, "enable-sound-levelup");
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, 2.0f, 1.0f); // Updated to play amethyst block sound with max volume
                break;
            //case "Toggle Splash Sound":
            //    toggleConfigOption(player, config, "enable-sound-splash");
            //    player.playSound(player.getLocation(), Sound.ENTITY_AXOLOTL_SPLASH, 0.1f, 1.0f); // Unavailable at this time
            //    break;
            case "Toggle Fail Sound":
                toggleConfigOption(player, config, "enable-sound-fail");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.2f, 1.0f); // Play fail sound
                break;
            case "Toggle Bobber Movement":
                toggleConfigOption(player, config, "enable-bobber-movement");
                break;
            case "Toggle Fish Teleport":
                toggleConfigOption(player, config, "teleport-fish-to-inventory");
                break;
            case "Exit Config":
                player.closeInventory();
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Config Settings")) {
            // Ensure no items are left in the GUI
            Inventory inventory = event.getInventory();
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    inventory.remove(item);
                }
            }
        }
    }

    private void toggleConfigOption(Player player, FileConfiguration config, String key) {
        boolean newValue = !config.getBoolean(key);
        config.set(key, newValue);
        plugin.saveConfig();
        openConfigGUI(player); // Refresh the GUI
    }
}
