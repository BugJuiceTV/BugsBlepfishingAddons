package bug.example.diamond;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigGUICommand implements CommandExecutor {

    private final ConfigGUI configGUI;

    public ConfigGUICommand(ConfigGUI configGUI) {
        this.configGUI = configGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            configGUI.openConfigGUI(player); // Removed the second argument to match the method signature
            return true;
        } else {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
    }
}