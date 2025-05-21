package bug.example.diamond;

import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import net.milkbowl.vault.economy.Economy;

import java.util.Map;
import java.util.UUID;

public final class Diamond extends JavaPlugin implements Listener {

    private ConfigGUI configGUI;
    private Economy economy;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Initialize Vault economy
        if (!setupEconomy()) {
            getLogger().severe("Vault economy not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize and register ConfigGUI
        configGUI = new ConfigGUI(this);
        getServer().getPluginManager().registerEvents(configGUI, this);

        // Register FishingRodShopGUI
        FishingRodShopGUI fishingRodShopGUI = new FishingRodShopGUI(this, economy);
        getServer().getPluginManager().registerEvents(fishingRodShopGUI, this);

        // Register commands
        if (getCommand("configgui") != null) {
            getCommand("configgui").setExecutor(new ConfigGUICommand(configGUI));
        }
        if (getCommand("openrodshop") != null) { // Register the openrodshop command
            getCommand("openrodshop").setExecutor(new RodShopCommand(fishingRodShopGUI));
            registerCustomAliases("openrodshop");
        }
        if (getCommand("bfreload") != null) { // Register the bfreload command
            getCommand("bfreload").setExecutor(new ReloadCommand(this));
            registerCustomAliases("bfreload");
        }

        // Register the onPlayerFish event listener
        getServer().getPluginManager().registerEvents(new onPlayerFish(this), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        var rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Reloads the plugin configuration and refreshes all plugin components
     */
    @Override
    public void reloadConfig() {
        // Reload the configuration file
        super.reloadConfig();

        // Log that the configuration has been reloaded
        getLogger().info("BugsBlepFishingAddon configuration reloaded");
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getPlayer() == null) {
            return;
        }

        PlayerFishEvent.State state = event.getState();
        boolean enableTitle = getConfig().getBoolean("enable-title", false);
        boolean enableActionBar = getConfig().getBoolean("enable-actionbar", false);
        boolean enableSoundLevelUp = getConfig().getBoolean("enable-sound-levelup", false);
        boolean enableSoundSplash = getConfig().getBoolean("enable-sound-splash", false);
        boolean enableSoundFail = getConfig().getBoolean("enable-sound-fail", false);

        switch (state) {
            case CAUGHT_FISH:
                handleCaughtFish(event, enableTitle, enableSoundLevelUp);
                break;

            case FISHING: // Trigger when the bobber hits the water
                if (enableSoundSplash) { // Check if splash sound is enabled
                    FishHook hook = event.getHook();
                    if (hook != null && hook.isInWater()) { // Ensure the hook is in water
                        event.getPlayer().playSound(hook.getLocation(), Sound.ENTITY_AXOLOTL_SPLASH, 1.0f, 1.0f); // Play axolotl splash sound
                    }
                }
                break;

            case FAILED_ATTEMPT:
                // Ensure handleFailedAttempt is only called once
                if (enableActionBar || enableSoundFail) {
                    handleFailedAttempt(event, enableActionBar, enableSoundFail);
                }
                break;

            default:
                break;
        }
    }

    private void handleCaughtFish(PlayerFishEvent event, boolean enableTitle, boolean enableSoundLevelUp) {
        if (enableTitle) {
            event.getPlayer().resetTitle(); // Clear any existing titles
            event.getPlayer().sendTitle("Fish Caught!", "Great job!", 10, 70, 20);
        }
        if (enableSoundLevelUp) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), "minecraft:block.amethyst_block.step", 2.0f, 1.0f);
        }
    }

    private void handleBite(PlayerFishEvent event, boolean enableActionBar, boolean enableSoundSplash) {
        if (enableActionBar) {
            event.getPlayer().sendActionBar("A fish is biting!");
        }
        // Removed splash sound logic to ensure no splash noises are played
    }

    private void handleFailedAttempt(PlayerFishEvent event, boolean enableActionBar, boolean enableSoundFail) {
        if (enableActionBar) {
            event.getPlayer().sendActionBar("The fish got away!");
        }

    }

    /**
     * Registers custom command aliases from the config.yml file
     * @param commandName The name of the command to register aliases for
     */
    private void registerCustomAliases(String commandName) {
        if (!getConfig().contains("commands." + commandName + ".aliases")) {
            return;
        }

        for (String alias : getConfig().getStringList("commands." + commandName + ".aliases")) {
            getCommand(commandName).getAliases().add(alias);
            getLogger().info("Registered custom alias '" + alias + "' for command '" + commandName + "'");
        }
    }
}
