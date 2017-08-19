package org.eirinncraft.Bookmarks.SupportingObjects;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.eirinncraft.Bookmarks.Bookmarks;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Data
public class Marker {

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Bookmarks plugin;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private String playerOwnername;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private UUID owneruuid;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Location location;
		
	private int markerid;
	private String markername;
	private int playerOwnerid;
	private UUID worlduuid;
	private double x;
	private double y;
	private double z;

	public Marker(Bookmarks plugin) {
		this.plugin = plugin;
	}

	/**
	 * Used to compare locations.  Do not molest!
	 * 
	 * @return
	 */
	public Location getLocation() {
		if( location == null ){
			World world = plugin.getServer().getWorld(worlduuid);
			if( world == null){
				// instead of fretting, set to "default" world spawn for marker
				plugin.getLogger().info("World not valid.  Looking for: " + worlduuid);
				location = plugin.getServer().getWorlds().get(0).getSpawnLocation();
				// maybe just delete marker in future?
			} else { 
				location = new Location(world, x, y, z);
			}
		}
		
		// return a defensive copy
		return location.clone();
	}
	
	public String getWorldName(){
		return getLocation().getWorld().getName();
	}
	
	public String getOwnerPlayername() {
		if( playerOwnername == null )
			playerOwnername = plugin.getMarkerManager().getPlayernamefromID( playerOwnerid );
		return playerOwnername;
	}
	
	public UUID getOwnerUUID() {
		if( owneruuid == null )
			owneruuid = plugin.getMarkerManager().getPlayerUUIDfromID( playerOwnerid );
		return owneruuid;
	}

	public void delete() {
		plugin.getMarkerManager().deleteMarker( markerid );
	}
	

}
