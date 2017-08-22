package org.eirinncraft.Bookmarks.Books.DebugBook;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.BookCommand;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;

import xyz.upperlevel.spigot.book.BookUtil;

public class ToggleDebugCommand extends BookCommand {

	private DebugType debugType;
	private Bookmarks plugin;
	private UUID uuid;
	
	public ToggleDebugCommand(Bookmarks plugin, UUID uuid, DebugType debugType) {
		super(plugin, uuid);
		
		this.debugType = debugType;
		this.plugin = plugin;
		this.uuid = uuid;
	}
	
	@Override
	public String getCommandName() {
		return "ToggleDebug";
	}

	@Override
	public void execute() {
		plugin.getDebugger().toggleType(debugType);
		Player player = plugin.getServer().getPlayer( uuid );

		
		//reopen book in 1 tick 
		// ... this feels like a hack ...
		new BukkitRunnable() {
            @Override
            public void run() {
            	BookUtil.openPlayer(player, plugin.getLibrarian().getDebugBook( player.getUniqueId() ).getBook() );
            }
        }.runTaskLater(this.plugin, 1);
        // no more runnable
	}

}
