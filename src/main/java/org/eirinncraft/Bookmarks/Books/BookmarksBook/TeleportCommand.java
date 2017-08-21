package org.eirinncraft.Bookmarks.Books.BookmarksBook;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.BookCommand;
import org.eirinncraft.Bookmarks.SupportingObjects.Marker;

public class TeleportCommand extends BookCommand {

	private Bookmarks plugin;
	private UUID uuid;
	private Marker marker;
	
	public TeleportCommand(Bookmarks plugin, UUID uuid, Marker marker) {
		super(plugin, uuid);
		
		this.plugin = plugin;
		this.uuid = uuid;
		this.marker = marker;
	}
	
	@Override
	public String getCommandName() {
		return "TeleportCommand";
	}

	@Override
	public void execute() {
		Player player = plugin.getServer().getPlayer( uuid );
		
		Location tplocation = marker.getLocation().clone();
		// first check if location isn't world spawn as Markers
		// default there if the worlduuid can't resolve to a world on the server
		
		if( tplocation.equals( plugin.getServer().getWorlds().get(0).getSpawnLocation() )){
			player.sendMessage(ChatColor.DARK_AQUA + "Warp Canceled.  World not found.");
			return;
		}
		
		//fine-tune the location
		
		// x+.5, y+1, z+.5 - so tp ends up in middle of marker
		tplocation.setX( tplocation.getX() + .5 );
		tplocation.setY( tplocation.getY() + 1 );
		tplocation.setZ( tplocation.getZ() + .5 );
		
		// stay looking in player's current heading
		tplocation.setYaw( player.getLocation().getYaw() );
		tplocation.setPitch( player.getLocation().getPitch() );
			
		if ( safeToWarp( tplocation )){
			try	{
				player.teleport(tplocation, TeleportCause.COMMAND);
				player.sendMessage(ChatColor.DARK_AQUA + "Arrived at: " + ChatColor.AQUA + marker.getMarkername());
				plugin.getLogger().info("Teleported " + player.getName() 
						+ " to \"" + marker.getMarkername() + "\" (" + marker.getWorldName() + ") " 
						+ (int) marker.getX() + " " + (int) marker.getY() + " " + (int) marker.getZ());

			} catch (Exception e1) { 
				player.sendMessage(ChatColor.RED + "Unable to warp!");
				plugin.getLogger().severe("SEVERE error attempting to teleport " + player.getPlayerListName() + " to location: " + marker.getMarkername() + " loctostring: " + marker.getLocation().toString() );
				plugin.getLogger().severe(e1.getMessage());
			}

		} else
			player.sendMessage(ChatColor.RED + "Warp is unsafe!");
	}
	
	
	
	private boolean safeToWarp( Location location ){

		// only work on your own copy of the passed arg
		Location checkloc = location.clone();
		
		// iterate up from location to ensure player
		// won't suffocate when warping.
		int checkblocks = 3;
		for ( int i = 1; i <= checkblocks; i++){
			
			// skip over some "whitelisted" stuff
			//  but return false otherwise
			switch( checkloc.getBlock().getType() ){
				case AIR: break;
				case DIAMOND_BLOCK: break;
				case STATIONARY_WATER: break;
				case GOLD_PLATE: break;
				case IRON_PLATE: break;
				case STONE_PLATE: break;
				case WOOD_PLATE: break;
				case TRIPWIRE: break;
				default: 
					return false;
			}
			
			// made it this far, move up one block and check that
			checkloc.setY( checkloc.getY() + 1);
		}
		// made it out of the loop so we should be safe to warp to.
		return true;
	}
}
