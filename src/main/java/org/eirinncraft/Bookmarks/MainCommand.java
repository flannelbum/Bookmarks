package org.eirinncraft.Bookmarks;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.eirinncraft.Bookmarks.Books.BookCommand;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;

import xyz.upperlevel.spigot.book.BookUtil;

public class MainCommand implements CommandExecutor {

	private Bookmarks plugin;

	public MainCommand(Bookmarks plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		//DEBUG section
		if ( plugin.debugTypeActive(DebugType.INPUT_COMMAND) && sender.isOp() ) {
			sender.sendMessage(Bookmarks.MAIN_COMMAND + " values: ");
			sender.sendMessage("command name: " + command.getName());
			sender.sendMessage("label: " + label);

			for (String arg : args)
				sender.sendMessage("arg: " + arg);
		}
		//END DEBUG section
			
			
	
		if( sender instanceof Player ) {
			
			Player player = (Player) sender;
			
			if (args.length < 1) {
				
				// player typed in command only. if they don't have a book
				// in their inventory, given them one.
			
				boolean hasbook = false;
				PlayerInventory playerinventory = player.getInventory();
				
				if (playerinventory.contains(Material.WRITTEN_BOOK))
					for (ItemStack item : playerinventory.getContents())
						if (item != null)
							if (item.hasItemMeta())
								if (item.getItemMeta().hasLore())
									if (item.getItemMeta().getLore().equals(plugin.getLibrarian().getBookmarksBook(null).getLore()))
										hasbook = true;
				if (hasbook)
					player.sendMessage(ChatColor.DARK_AQUA + "You already have the Bookmark book!");
				else
					playerinventory.addItem( plugin.getLibrarian().getBookmarksBook(player.getUniqueId()).getBook() );
				
				
			} else {
				
				// Handle specific commands like debug
				if( args[0].equalsIgnoreCase("DEBUG") )
					if(player.isOp() || player.hasPermission("bookmarks.admin.debug"))
						BookUtil.openPlayer(player, plugin.getLibrarian().getDebugBook( player.getUniqueId() ).getBook() );
				
//				// And for the admin book
//				if( args[0].equalsIgnoreCase("ADMIN") )
//					if( player.isOp() || player.hasPermission("bookmarks.adminbook"))
//						BookUtil.
				
				
				// check library for command and perform action/unregister player
				// assumes commands like /bookmarks <token>
				if ( plugin.getLibrarian().playerHasCommands( player.getUniqueId() )){
					HashMap<String,BookCommand> commands = plugin.getLibrarian().getPlayerCommands( player.getUniqueId() );
					if(commands.containsKey( args[0] )){
						commands.get( args[0] ).execute();
						plugin.getLibrarian().unregisterCommands( player );
					}			
				}
			}	
		}

		return true;
	}
}
