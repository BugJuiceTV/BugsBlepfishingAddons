package bug.example.diamond;

import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class onPlayerFish implements Listener {
    private final Plugin plugin;
    private final Map<FishHook, BukkitTask> activeTasks = new HashMap<>();

    public onPlayerFish(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FishHook hook = event.getHook();

        boolean enableBobberMovement = plugin.getConfig().getBoolean("enable-bobber-movement", true);
        boolean teleportFishToInventory = plugin.getConfig().getBoolean("teleport-fish-to-inventory", true);

        switch (event.getState()) {
            case BITE:
                if (hook != null && enableBobberMovement) {
                    simulateBobberMovement(hook, player);
                }
                break;

            case FAILED_ATTEMPT:
                if (hook != null) {
                    BukkitTask task = activeTasks.remove(hook);
                    if (task != null) {
                        task.cancel();
                    }
                    player.sendMessage("The fish got away!");
                }
                break;

            case CAUGHT_FISH:
                if (event.getCaught() instanceof Item item) {
                    handleCaughtFish(player, item, teleportFishToInventory);
                }
                break;

            default:
                break;
        }
    }

    private void handleCaughtFish(Player player, Item item, boolean teleportFishToInventory) {
        ItemStack caughtItem = item.getItemStack();

        if (teleportFishToInventory) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(caughtItem);
                item.remove();
                player.sendMessage("You caught a fish and it was added to your inventory!");
            } else {
                player.sendMessage("Your inventory is full! The fish dropped on the ground.");
            }
        } else {
            player.sendMessage("You caught a fish, but it was left on the ground!");
        }
    }

    private void simulateBobberMovement(FishHook hook, Player player) {
        int maxTicks = 60;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= maxTicks || hook.isDead()) {
                    Bukkit.getScheduler().cancelTask(this.hashCode());
                    activeTasks.remove(hook);
                    return;
                }

                double offsetX = (Math.random() - 0.5) * 0.5;
                double offsetZ = (Math.random() - 0.5) * 0.5;
                hook.setVelocity(new Vector(offsetX, 0, offsetZ));

                ticks += 10;
            }
        }, 0L, 10L);

        activeTasks.put(hook, task);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (hook.isDead() || !player.isOnline()) {
                task.cancel();
                activeTasks.remove(hook);
            }
        }, maxTicks);
    }
}
