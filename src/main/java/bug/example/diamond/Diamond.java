package bug.example.diamond;

import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Sound; // Add this import

public final class Diamond extends JavaPlugin implements Listener {

    private ConfigGUI configGUI;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Initialize and register ConfigGUI
        configGUI = new ConfigGUI(this);
        getServer().getPluginManager().registerEvents(configGUI, this);

        // Register this class as a listener
        getServer().getPluginManager().registerEvents(this, this);

        // Register the onPlayerFish event listener with the plugin instance
        getServer().getPluginManager().registerEvents(new onPlayerFish(this), this);

        // Register the command executor
        if (getCommand("configgui") != null) {
            getCommand("configgui").setExecutor(new ConfigGUICommand(configGUI));
        }
    }

    @Override
    public void onDisable() {
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
                handleFailedAttempt(event, enableActionBar, enableSoundFail);
                break;

            default:
                break;
        }
    }

    private void handleCaughtFish(PlayerFishEvent event, boolean enableTitle, boolean enableSoundLevelUp) {
        if (enableTitle) {
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
        if (enableSoundFail) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), "minecraft:entity.villager.no", 0.2f, 1.0f);
        }
    }
}
