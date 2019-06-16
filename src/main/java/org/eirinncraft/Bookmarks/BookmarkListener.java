package org.eirinncraft.Bookmarks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;
import org.eirinncraft.Bookmarks.SupportingObjects.Marker;


import net.md_5.bungee.api.ChatColor;
import xyz.upperlevel.spigot.book.BookUtil;


public class BookmarkListener implements Listener {

	Bookmarks plugin;
	
	public BookmarkListener(Bookmarks plugin) {
		this.plugin = plugin;
	}

	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		plugin.getMarkerManager().playerJoined( e.getPlayer() );
	}
	
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		plugin.getMarkerManager().playerQuit( e.getPlayer().getUniqueId() );
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		// Handler priority is set MONITOR as we don't want to create a bookmark if the block placement was canceled.
		// Otherwise, player placed a named diamond block.  Register it as a marker and add it to this player's bookmarks
		if ( !e.isCancelled() )
			if ( e.getBlockPlaced().getType().equals(Material.DIAMOND_BLOCK) && e.getItemInHand().hasItemMeta() )
				plugin.getMarkerManager().placedNewBookmark(e);		
	}

	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		// if this is a diamond block, it might be a bookmark. 
		if ( e.getBlock().getType().equals(Material.DIAMOND_BLOCK) ){			
			 
			plugin.debug(DebugType.LISTENER, 
						"BlockBreakEvent -- block Location: " + e.getBlock().getLocation().getX() + 
						"/" + e.getBlock().getLocation().getY() +
						"/" + e.getBlock().getLocation().getZ() );
			
			int markerid = plugin.getMarkerManager().getMarkerIdFromLocation( e.getBlock().getLocation() );			
			Marker marker = plugin.getMarkerManager().getMarker(markerid);
			
			if( marker != null )
				if( marker.getOwnerUUID().equals( e.getPlayer().getUniqueId() )){
					// Player broke their own bookmark
					plugin.getLogger()
							.info(e.getPlayer().getName() + " removed marker id: " + marker.getMarkerid() + " \""
									+ marker.getMarkername() + "\" (" + marker.getWorldName() + ") - " + marker.getX()
									+ " " + marker.getY() + " " + marker.getZ());
					
					e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Marker " + ChatColor.AQUA + marker.getMarkername() + ChatColor.DARK_AQUA + " has been removed");
					marker.delete();
					
				} else {
					// Player doesn't own the marker so cancel the break and let em know they're a dingus
					e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Can't break Marker " + ChatColor.AQUA + marker.getMarkername() + ChatColor.DARK_AQUA + " owned by: " + ChatColor.AQUA + "" + ChatColor.ITALIC + marker.getOwnerPlayername());
					e.setCancelled(true);
				}
		}
	}
	
	
	
	
	/**
	 * Much opportunity for other books managed by the Librarian here
	 *
	 */
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		
		Player player = e.getPlayer();
		ItemStack mainHandItem = player.getInventory().getItemInMainHand();

		// Handle if a player touches a diamond block with a Written Book in their hand
		if ( e.getAction().equals(Action.LEFT_CLICK_BLOCK) && 
				mainHandItem != null )
			
			if ( mainHandItem.getType().equals(Material.WRITTEN_BOOK) && 
					mainHandItem.hasItemMeta() )
				
				if ( mainHandItem.getItemMeta().hasLore() && 
						mainHandItem.getItemMeta().getLore().equals( plugin.getLibrarian().getBookmarksBook(null).getLore() )){
					
					plugin.getMarkerManager().touchedBookmark(player, e.getClickedBlock().getLocation());
				
				}
					
		
		
		// Handle players that are opening their Bookmark book.  
		if ( e.getAction().equals(Action.RIGHT_CLICK_AIR) ||
				e.getAction().equals(Action.RIGHT_CLICK_BLOCK) )
			
			if ( mainHandItem.getType().equals(Material.WRITTEN_BOOK) && mainHandItem.hasItemMeta() )
				
				if ( mainHandItem.getItemMeta().hasLore() && mainHandItem.getItemMeta().getLore().equals( plugin.getLibrarian().getBookmarksBook(null).getLore() )){
										
					e.setCancelled(true); // we cancel the event as, otherwise, the player would see the wrong book.
					BookUtil.openPlayer(e.getPlayer(), plugin.getLibrarian().getBookmarksBook( e.getPlayer().getUniqueId() ).getBook() );
					
				} 
		
	}
}