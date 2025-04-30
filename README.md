🎣 Plugin Configuration Overview
1. Configuration Management
The plugin uses a config.yml file to store settings that control various features. These include:

Displaying titles and action bar messages during fishing events.

Playing specific sounds (e.g., level-up, splash, fail).

Enabling bobber movement effects.

Teleporting caught fish directly to the player's inventory.

2. Config GUI
GUI Overview
The ConfigGUI class provides a graphical user interface (GUI) for players to toggle settings in-game.

Opening the GUI
The openConfigGUI method creates an inventory with toggle buttons for each setting.

Each button uses green/red concrete to indicate whether the setting is enabled or disabled.

An "Exit Config" button (represented by a barrier item) lets players close the GUI.

Toggle Buttons
Clicking a button toggles the corresponding setting in the config.yml.

The GUI refreshes after each toggle to show updated states.

3. Event Handling
InventoryClickEvent
Prevents players from moving items inside the GUI.

Detects which button was clicked and updates the setting accordingly.

Plays feedback sounds (e.g., button click, amethyst block, villager "no").

InventoryCloseEvent
Ensures no items are left behind when the GUI is closed.

4. Sounds
The plugin provides audio feedback to enhance interaction:

Button click sounds during GUI interaction.

Special sounds for toggling:

Amethyst block for “Caught Sound”.

Villager “no” for “Fail Sound”.

5. Commands
Defined in plugin.yml:
/configgui — Opens the configuration GUI for the player.

6. Dependencies
Requires the Paper API (paper-api) for Minecraft server development.

7. Gameplay Features
The plugin enhances the fishing experience by allowing players to:

Customize visual and sound effects.

Enable or disable bobber movement.

Automatically teleport caught fish to their inventory.

✅ Summary
This plugin provides a user-friendly, in-game GUI to customize fishing-related features, making the experience more interactive and immersive.

