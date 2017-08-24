package org.eirinncraft.Bookmarks.SupportingObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;


public class MarkerManager {

	private Bookmarks plugin;
	private HashMap<Integer, Marker> markerMap; // all markers by their table id
	private HashMap<Integer, UUID> playerMap; // all players by their table id
	private HashMap<Integer, String> playernamelookupbyid; // stores playernames by their databse playerid
	private HashMap<UUID, String> playernamelookupbyUUID; // stores player names by their UUID
	private HashMap<UUID, Integer> playeridlookupbyUUID; // stores playerids by their UUID
	private HashMap<UUID, ArrayList<Bookmark>> playerbookmarkMap; // Dynamic map containing players and their bookmarks

	public MarkerManager(Bookmarks plugin) {
		this.plugin = plugin;
		markerMap = new HashMap<Integer, Marker>();
		playerMap = new HashMap<Integer, UUID>();
		playerbookmarkMap = new HashMap<UUID, ArrayList<Bookmark>>();
		playernamelookupbyid = new HashMap<Integer, String>();
		playernamelookupbyUUID = new HashMap<UUID, String>();
		playeridlookupbyUUID = new HashMap<UUID, Integer>();
	}
	
	

	/**
	 * Clears local cache in markerMap triggers reload from db
	 */
	public void refreshMarkers() {
		this.markerMap = new HashMap<Integer, Marker>();
		plugin.getDB().loadMarkers();
	}

	/**
	 * Clears local cache for playermaps and triggers reload from db
	 */
	public void refreshPlayers() {
		playerMap = null;
		playernamelookupbyid = null;
		playeridlookupbyUUID = null;
		playerMap = new HashMap<Integer, UUID>();
		playernamelookupbyid = new HashMap<Integer, String>();
		playeridlookupbyUUID = new HashMap<UUID, Integer>();
		
		plugin.getDB().loadPlayerMaps();
	}

	/**
	 * Creates and registers a Marker object from the markers table
	 * 
	 * @param markerid
	 * @param name
	 * @param playerOwnerid
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void loadMarker(int markerid, String markername, int playerOwnerid, String world, float x, float y, float z) {
		UUID worlduuid = UUID.fromString(world);
		Marker m = new Marker(plugin);
		m.setMarkerid(markerid);
		m.setMarkername(markername);
		m.setPlayerOwnerid(playerOwnerid);
		m.setWorlduuid(worlduuid);
		m.setX(x);
		m.setY(y);
		m.setZ(z);
		
		plugin.debug(DebugType.MARKERMANAGER, 
				"loadMarker(markerid=" + m.getMarkerid() +
				" , markername= " + m.getMarkername() +
				", playerOwnerid= " + m.getPlayerOwnerid() +
				", world= " + m.getWorlduuid() +
				", " + m.getX() + "/" + m.getY() + "/" +m.getZ() + ")");
		

		this.markerMap.put(markerid, m);
	}

	/**
	 * Updates Player Maps used in the MarkerManager
	 * 
	 * @param playerid
	 * @param uuidstring
	 * @param name
	 */
	public void loadPlayer(int playerid, String uuidstring, String name) {
		UUID uuid = UUID.fromString(uuidstring);
		playerMap.put(playerid, uuid);
		playernamelookupbyid.put(playerid, name);
		playernamelookupbyUUID.put(uuid, name);
		playeridlookupbyUUID.put(uuid, playerid);
	}

	/**
	 * Just gets the playername from the lookup table.
	 * 
	 * @param playerid
	 * @return
	 */
	public String getPlayernamefromID(int playerid) {
		return playernamelookupbyid.get(playerid);
	}
	
	/**
	 * Just gets the playername from the lookup table from UUID
	 * @param key
	 * @return
	 */
	public String lookupPlayernameFromUUID(UUID uuid) {
		return playernamelookupbyUUID.get(uuid);
	}


	/**
	 * Just get the Marker object by its table id
	 * 
	 * @param markerid
	 * @return
	 */
	public Marker getMarker(int markerid) {
		return markerMap.get(markerid);
	}
	
	/**
	 * Loops through markers and returns markerid if passed location
	 * matches for a location.
	 * @param location
	 * @return
	 */
	public int getMarkerIdFromLocation(Location location){
		try{
			
			plugin.debug(DebugType.MARKERMANAGER, "getMarkerIdFromLocation");
			plugin.debug(DebugType.MARKERMANAGER, "COMPARE I: " + location.getX() + "/" + location.getY() + "/" + location.getZ() );
				
			for( Entry<Integer, Marker> marker : markerMap.entrySet() ){
				
				plugin.debug(DebugType.MARKERMANAGER, 
						"COMPARE o: " + marker.getValue().getLocation().getX() + 
						"/" + marker.getValue().getLocation().getY() + 
						"/" + marker.getValue().getLocation().getZ() );
				
				
				if( marker.getValue().getLocation().equals( location ) ){
					plugin.debug(DebugType.MARKERMANAGER, "id found");
					return marker.getKey();
				} 
			}
	
		} catch ( NullPointerException e ) {
			// This will happen when there are no markers in the markerMap that match
			plugin.debug(DebugType.MARKERMANAGER, "getMarkerIdFromLocation NULLPOINTEREXCEPTION -- marker or getlocation is null.  Maybe normal.");
			return 0;
		}
		return 0;
	}
	
	/**
	 * Gets the Player's UUID from their bookmarks db id
	 * 
	 * @param playerOwnerid
	 * @return
	 */
	public UUID getPlayerUUIDfromID( int playerid ){
		return playerMap.get( playerid );
	}
	
	/**
	 * First pulls from playerbookmarkMap. If nothing there, pull from database
	 * and add to map.  Returns the Bookmark ArrayList for a player.
	 * 
	 * @param uuid
	 * @return
	 */
	public ArrayList<Bookmark> getPlayerBookmarks(UUID uuid, boolean forceRefresh) {
		if (!playerbookmarkMap.containsKey(uuid) || forceRefresh) {
			ArrayList<Bookmark> marks = plugin.getDB().getPlayerBookmarks(uuid);
			playerbookmarkMap.put(uuid, marks);
		}
		
		if( plugin.debugTypeActive(DebugType.MARKERMANAGER) ){
			plugin.debug(DebugType.MARKERMANAGER, "forceRefresh = " + forceRefresh );
			plugin.debug(DebugType.MARKERMANAGER, "getPlayerBookmarks returning:");
			for( Bookmark bm : playerbookmarkMap.get(uuid) )
				plugin.debug(DebugType.MARKERMANAGER, 
						"Bookmarksid: " + bm.getBookmarksid()
						+ " bmGetMarker.getMarkerid(): " + bm.getMarker().getMarkerid()
						+ " bmMarkerid: " + bm.getMarkerid() 
						+ " bmPlayerid: " + bm.getPlayerid()
						+ " bmPlayersort: " + bm.getPlayersort()
						);
		}
		
		
		return playerbookmarkMap.get(uuid);
	}
	
	/**
	 * Wrapper to give playerbookmarks a refresh from cache by default
	 * 
	 * @param uuid
	 * @return
	 */
	public ArrayList<Bookmark> getPlayerBookmarks(UUID uuid){
		return getPlayerBookmarks(uuid,false);
	}
	
	
	/**
	 * Iterate over player's bookmarks and save all the dirty ones
	 * 
	 * @param uuid
	 */
	public void savePlayerBookmarks(UUID uuid) {
		plugin.debug(DebugType.MARKERMANAGER, "savePlayerBookmarks( " + uuid.toString() + " )");
		ArrayList<Bookmark> bookmarks = getPlayerBookmarks( uuid );
		
		ArrayList<Bookmark> dirtybookmarks = new ArrayList<Bookmark>();
		
		for (Bookmark bookmark : bookmarks) {
			plugin.debug(DebugType.MARKERMANAGER, "bookmark: " + bookmark.getBookmarksid() + "  dirty? " + bookmark.isDirty());
			if( bookmark.isDirty() )
				dirtybookmarks.add(bookmark);		
		}
		
		plugin.debug(DebugType.MARKERMANAGER, "dirtybookmarks.size() = " + dirtybookmarks.size());
		if( dirtybookmarks.size() > 0 )
			plugin.getDB().addOrSavePlayerBookmark(dirtybookmarks);
	}
	
	
	
	
	
	
	/**
	 * Deletes the marker from database and removes from all players
	 * also rewarms cache for all online player's bookmarks 
	 * 
	 * @param markerid
	 */
	public void deleteMarker(int markerid) {
		plugin.debug(DebugType.MARKERMANAGER, "deleteMarker(" + markerid + ")" );		
		
		// remove any holograms we can find for the marker
		Marker marker = markerMap.get( markerid );
		Collection<Entity> lostandfound = marker.getLocation().getWorld().getNearbyEntities( marker.getLocation() , 2, 3, 2);
		for( Entity entityfound : lostandfound ) 
			if( entityfound.getType().equals(EntityType.ARMOR_STAND) && entityfound.isCustomNameVisible() ) 
				entityfound.remove();

		// Get all players that have this marker and their current bookmarks id for it.
		HashMap<UUID,Integer> playersort = plugin.getDB().getPlayersAndBookmarkIDsforMarker( markerid );
		
		// loop through the list and loop player bookmarks to remove markerid 
		for( Entry<UUID, Integer> entry : playersort.entrySet() )
			deleteMarkerFromPlayer(entry.getValue(), entry.getKey());
		
		// remove the marker object and remove it from the db
		markerMap.remove( markerid );
		plugin.getDB().removeMarker( markerid );
		
		
		// flush the internal map and rebuild it only with current online players
		
		playerbookmarkMap = new HashMap<UUID, ArrayList<Bookmark>>();
		
		plugin.debug(DebugType.MARKERMANAGER, "Refreshing all Online players..." ); 
		for( Player player : plugin.getServer().getOnlinePlayers() ){
			getPlayerBookmarks( player.getUniqueId(), true);
		}		
	}

	/**
	 * Deletes a Bookmarks object and database 
	 * entry by bookmarks.id from the player
	 * 
	 * @param targetbookmarksid
	 * @param uuid
	 */
	public void deleteMarkerFromPlayer(int targetbookmarksid, UUID uuid){
		
		plugin.debug(DebugType.MARKERMANAGER, "IN deleteMarkerFromPlayer -- targetbookmarksid = " + targetbookmarksid 
											+ " uuuid = " + uuid.toString() );
		
		ArrayList<Bookmark> playerbookmarks = getPlayerBookmarks( uuid );

		plugin.debug(DebugType.MARKERMANAGER, "-- INITIAL playerbookmarks.size() = " + playerbookmarks.size() );
		
		//obtain current target bookmark 
		Bookmark targetbookmark = null;
		for( Bookmark bookmark : playerbookmarks ){
			if( bookmark.getBookmarksid() == targetbookmarksid )
				targetbookmark = bookmark;
		}
		
		plugin.debug(DebugType.MARKERMANAGER, "-- targetbookmark.marker.name: \"" + targetbookmark.getMarker().getMarkername() +"\"" );
		
		int targetsort = targetbookmark.getPlayersort();
		
		// remove the player Bookmark entry as an object
		// and remove it from the database
		playerbookmarks.remove( targetsort );
		
		plugin.getDB().removeBookmarksid( targetbookmark.getBookmarksid() );

		plugin.debug(DebugType.MARKERMANAGER, "-- UPDATED playerbookmarks.size() = " + playerbookmarks.size() );
		
		// close the gap left by the marker
		// set the new sort for the remaining bookmarks for the player
		// and mark the bookmark dirty so it is saved back to the database
		for( int i = targetsort; i < playerbookmarks.size(); i++ ){
			Bookmark bookmark = playerbookmarks.get(i);
			bookmark.setPlayersort(i);
			bookmark.setDirty(true);
			
			plugin.debug(DebugType.MARKERMANAGER, "-- Resort Loop i/sort[ " + i + " ] boomakrsid: " + bookmark.getBookmarksid() + " isDirty? " + bookmark.isDirty() );
		}
		
		plugin.debug(DebugType.MARKERMANAGER, "-- SANITY playerbookmarks.size() = " + playerbookmarks.size() );
		
		// refresh the internal map
		playerbookmarkMap.put(uuid, playerbookmarks);
		
		// finally trigger save of player Bookmarks which should
		// write all dirty bookmarks to the database
		savePlayerBookmarks(uuid);
	}
	
	
	/**
	 * Event Listener handlers below
	 * 
	 */
	
	public void playerQuit(UUID uuid) {
		// attempt saving bookmarks to database in case any are dirty
		savePlayerBookmarks( uuid );

		// remove them from the cache
		playerbookmarkMap.remove( uuid );
	}

	public void playerJoined(Player player) {
		// update database for any player name changes
		plugin.getDB().updatePlayer( player );

		// warm up cache with player bookmark info
		getPlayerBookmarks( player.getUniqueId() );

	}

	public void placedNewBookmark(BlockPlaceEvent e) {
		// Add new bookmark to database
		// load the marker object
		// update player's marker
		
		Player player = e.getPlayer();
		int playerownerid = playeridlookupbyUUID.get( player.getUniqueId() );
		String markername = e.getItemInHand().getItemMeta().getDisplayName();
		Location location = e.getBlockPlaced().getLocation();
		String worlduuidstring = location.getWorld().getUID().toString();
		String worldname = location.getWorld().getName();
		float x = (float) location.getX();
		float y = (float) location.getY();
		float z = (float) location.getZ();
		
		int markerid = getMarkerIdFromLocation( location ); 
		
		if(  markerid == 0 ){
			// add a new marker and save its id
			int newmarkerid = plugin.getDB().addNewMarker(markername, location, player);
			plugin.debug(DebugType.MARKERMANAGER, "placedNewBookmark -- newmarkerid= " + newmarkerid); 
			
			// load the new marker as a Marker object
			loadMarker(newmarkerid, markername, playerownerid, worlduuidstring, x, y, z);
			
			// Act as if the player punched the bookmark with
			// their bookmark book to get the mark added
			touchedBookmark(player, location);
			
			plugin.getLogger()
				.info(player.getName() + " created marker id: " + newmarkerid + " \"" + markername
						+ "\" (" + worldname + ") - " + x + " " + y + " " + z);
		}
		
	}


	/**
	 * Player touched a bookmark, add it to their bookmarks
	 * 
	 * @param player
	 * @param location
	 */
	public void touchedBookmark(Player player, Location location) {
		
		int markerid = getMarkerIdFromLocation( location );
		
		plugin.debug(DebugType.MARKERMANAGER, "touchedBookmark - markerid=" + markerid);
		
		if(  markerid > 0 ){
			Marker marker = getMarker( markerid );
			boolean hasbookmark = false;
			
			// loop through player bookmarks, if they don't have this one, add it
			for( Bookmark bm : plugin.getMarkerManager().getPlayerBookmarks( player.getUniqueId() ) )
				if( bm.getMarker().equals( marker ) )
					hasbookmark = true;
			
			if( !hasbookmark ) {
				// player does not have this bookmark so add it for them  
				plugin.getDB().addBookmarkForPlayer( markerid, playeridlookupbyUUID.get( player.getUniqueId() ));
				
				// force a refresh of cache for player.
				plugin.getMarkerManager().getPlayerBookmarks( player.getUniqueId(), true );
			}
			player.sendMessage(ChatColor.DARK_AQUA + "Bookmark: " + ChatColor.AQUA + marker.getMarkername() );
			
		}
	}




	/**
	 * Holograms are just armor stands with some stuff set to them
	 * They are Entities/LivingEntities.  Their IDs do not have persistence.
	 * 
	 * This method will first search for the existence of an entity that is an armor stand
	 * and will check if a custom name is visible.  If this is true, the entity is removed
	 * 
	 * Otherwise, a new entity is created provided the area of the world it should
	 * at is loaded.  
	 * 
	 * @param marker
	 */
	public void toggleHolo(Marker marker) {

		Entity entity = null;
		
		// let's try searching to see if there is a stand already
		Collection<Entity> lostandfound = marker.getLocation().getWorld().getNearbyEntities( marker.getLocation() , 2, 3, 2);
		for( Entity entityfound : lostandfound ) 
			if( entityfound.getType().equals(EntityType.ARMOR_STAND) && entityfound.isCustomNameVisible() ) 
				entity = entityfound;
 
		if( entity != null)
			entity.remove();
		else {
			Location location = marker.getLocation();
			String name = marker.getMarkername();
			
			// tweak location
			location.setX( location.getX() +.5);
			location.setY( location.getY() + 2);
			location.setZ( location.getZ() +.5);
			
			ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
			stand.setMarker(true);
			stand.setVisible(false);
			stand.setGravity(false);
			stand.setCustomName(ChatColor.AQUA + name);
			stand.setCustomNameVisible(true);
			stand.setRemoveWhenFarAway(false);
		}
			
	}



}






























