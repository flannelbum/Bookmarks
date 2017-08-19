package org.eirinncraft.Bookmarks.Books.BookmarksBook;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.BookCommand;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;
import org.eirinncraft.Bookmarks.SupportingObjects.Marker;

import xyz.upperlevel.spigot.book.BookUtil;

public class DeleteMarkerFromPlayerCommand extends BookCommand {

	private Bookmarks plugin;
	private UUID uuid;
	private Integer bookmarksid;
	private UUID removeforuuid;
	private Marker marker;
	
	public DeleteMarkerFromPlayerCommand(Bookmarks plugin, UUID uuid, Marker marker, Integer bookmarksid, UUID removeforuuid) {
		super(plugin, uuid, "DeleteMarkerFromPlayerCommand");
		this.plugin = plugin;
		this.uuid = uuid;
		this.marker = marker;
		this.bookmarksid = bookmarksid;
		this.removeforuuid = removeforuuid;
	}

	@Override
	public void execute() {
		plugin.debug(DebugType.BOOK_COMMAND, "Executing delete marker from player command");
		
		plugin.getMarkerManager().deleteMarkerFromPlayer(bookmarksid, removeforuuid);
		
		Player player = plugin.getServer().getPlayer( uuid );
		
		plugin.getLogger().info( player.getName() + " removed " 
							+ plugin.getMarkerManager().lookupPlayernameFromUUID( removeforuuid ) 
							+ " from \"" + marker.getMarkername() + "\" (" + marker.getWorldName() + ") "
							+ marker.getX() + " " + marker.getY() + " " + marker.getZ() );
		
		plugin.getServer().getPlayer( uuid ).sendMessage(ChatColor.DARK_AQUA + "Removed " 
				+ plugin.getMarkerManager().lookupPlayernameFromUUID( removeforuuid ) + " from: "
				+ ChatColor.AQUA + marker.getMarkername() );
		
		
		HashMap<UUID,Integer> playerswithmap = plugin.getDB().getPlayersAndBookmarkIDsforMarker( marker.getMarkerid() );
		
		
		// only reopen if there are players to show
		if( playerswithmap.size() > 1 ){
			//reopen book in 1 tick as we get a null pointer when we give it immediately back to the player
			// ... this feels like a hack ...
			new BukkitRunnable() {
	            @Override
	            public void run() {
	            	plugin.getLibrarian().unregisterCommands(uuid);
	            	BookUtil.openPlayer( plugin.getServer().getPlayer(uuid), new ViewOthersBook(plugin, uuid, marker, playerswithmap).getBook() );
	            }
	        }.runTaskLater(this.plugin, 1);
	        // no more runnable
		}
	}

}
