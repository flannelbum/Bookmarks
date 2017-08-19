package org.eirinncraft.Bookmarks.Books.BookmarksBook;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.BookCommand;
import org.eirinncraft.Bookmarks.SupportingObjects.Marker;

import xyz.upperlevel.spigot.book.BookUtil;

public class ViewOthersOpenBookCommand extends BookCommand{

	private Bookmarks plugin;
	private UUID uuid;
	private Marker marker;
	private HashMap<UUID,Integer> playerswith;
	
	public ViewOthersOpenBookCommand(Bookmarks plugin, UUID uuid, Marker marker, HashMap<UUID,Integer> playerswith) {
		super(plugin, uuid, "ViewOthersOpenBookCommand");
		this.plugin = plugin;
		this.uuid = uuid;
		this.marker = marker;
		this.playerswith = playerswith;
		
	}

	@Override
	public void execute() {
		Player player = plugin.getServer().getPlayer( uuid );
		
		//reopen book in 1 tick as we get a null pointer when we give it immediately back to the player
		// ... this feels like a hack ...
		new BukkitRunnable() {
            @Override
            public void run() {
            	plugin.getLibrarian().unregisterCommands(uuid);
            	BookUtil.openPlayer(player, new ViewOthersBook(plugin, uuid, marker, playerswith).getBook());
            }
        }.runTaskLater(this.plugin, 1);
        // no more runnable
		
	}

}
