package org.eirinncraft.Bookmarks.Books.DebugBook;

import java.util.UUID;

import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.BookCommand;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;

public class ForceRefreshFromDBCommand extends BookCommand{

	private Bookmarks plugin;
	private UUID uuid;
	
	public ForceRefreshFromDBCommand(Bookmarks plugin, UUID uuid){
		super(plugin, uuid);
		
		this.plugin = plugin;
		this.uuid = uuid;
	}

	@Override
	public String getCommandName() {
		return "ForceRefreshFromDB";
	}
	
	@Override
	public void execute() {
		plugin.debug(DebugType.BOOK_COMMAND, "Forced refresh of player bookmarks for: " + plugin.getServer().getPlayer( uuid ).getName() );
		plugin.getMarkerManager().getPlayerBookmarks(uuid, true);
	}

}
