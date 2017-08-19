package org.eirinncraft.Bookmarks.Books;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.BookmarksBook.BookmarksBook;
import org.eirinncraft.Bookmarks.Books.DebugBook.DebugBook;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;


public class Librarian {

	private Bookmarks plugin;
	private HashMap<UUID, HashMap<String, BookCommand>> commandRegister;

	public Librarian(Bookmarks plugin) {
		this.plugin = plugin;
		this.commandRegister = new HashMap<UUID, HashMap<String, BookCommand>>();
	}

	
	/**
	 * Returns the BookmarksBook for a given uuid
	 * 
	 * Need a more elegant way to get a book to a player
	 * and handle checks for lore that cover multiple books
	 * 
	 * @param uuid
	 * @return
	 */
	public Book getBookmarksBook(UUID uuid) {
		unregisterCommands( uuid );
		return new BookmarksBook(plugin, uuid);
	}
	public Book getDebugBook(UUID uuid) {
		unregisterCommands( uuid );
		return new DebugBook(plugin, uuid);
	}

	
	
	
	
	/**
	 * Interface for Book objects the Librarian manages.
	 * 
	 * Also see the abstract class BookCommands.  All commands
	 * a book should run via user-interaction extend that class.
	 *
	 */
	public interface Book {
		public abstract ItemStack getBook();
		public abstract List<String> getLore();
	}

	
	
	
	/**
	 * Generates a token and adds a token and BookCommand to a player in the
	 * commandRegister The BookCommand implementations trigger this method
	 * (via the BookCommand abstract class constructor) to get their token
	 * and register as a valid command.
	 * 
	 * @param uuid
	 * @param command
	 * @return token as String
	 ***/
	public String registerCommand(UUID uuid, BookCommand command) {
		
		// generate the token
		String token = Long.toHexString(Double.doubleToLongBits(Math.random()));

		// create a new map that we add to.  This will replace
		// what's already in the commandRegister map for the player
		HashMap<String, BookCommand> map = new HashMap<String, BookCommand>();
		map.put(token, command);
		
		// loop through existing register for this player and finish building
		// the map that we will add back into the register for the player.
		if (commandRegister.containsKey( uuid ))
			for (Entry<String, BookCommand> entry : commandRegister.get(uuid).entrySet())
				map.put(entry.getKey(), entry.getValue());

		// finally, write back the new command map for the player and return our token
		commandRegister.put(uuid, map);
		
		if(plugin.debugTypeActive(DebugType.LIBRARIAN)){
			plugin.debug(DebugType.LIBRARIAN, "Player has these: " );
			for( Entry<String,BookCommand> entry : map.entrySet())
				plugin.debug(DebugType.LIBRARIAN, "Key: " + entry.getKey() + " value: " + entry.getValue().getCommandName() );
		}
		
		return token;
	}

	/**
	 * Checks if we have a command for a player or not.
	 * 
	 * @param uuid
	 * @return
	 */
	public boolean playerHasCommands(UUID uuid) {
		if( commandRegister.containsKey( uuid ) )
			return true;
		return false;
	}
	
	/**
	 * Gets all commands for a specific player UUID
	 * 
	 * @return
	 */
	public HashMap<String, BookCommand> getPlayerCommands(UUID uuid) {
		HashMap<String,BookCommand> commandmap = null;
		if( playerHasCommands( uuid ) ){
			commandmap = commandRegister.get( uuid );
			plugin.debug(DebugType.LIBRARIAN, "getPlayerCommands returning for: " + uuid.toString() + " commandmap.size = " + commandmap.size() );
		}
		return commandmap;
	}

	/*****************************
	 * Removes all tokens and BookCommands for a given uuid
	 * 
	 * @param uuid
	 ***/
	public void unregisterCommands(UUID uuid) {
		if (playerHasCommands( uuid )){
			plugin.debug(DebugType.LIBRARIAN, "unregister - " + uuid.toString() + " .. removing commands from CommandRegister");
			commandRegister.remove( uuid );
		}
	}

	/*****************************
	 * Wrapper for unregisterCommand(String uuid).  Sometimes it's easier/quicker to just pass the player object.
	 * 
	 * @param uuid
	 ***/
	public void unregisterCommands(Player player) {
		unregisterCommands( player.getUniqueId() );
	}

	
}
