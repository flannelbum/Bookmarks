package org.eirinncraft.Bookmarks.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.SupportingObjects.Bookmark;
import org.eirinncraft.Bookmarks.SupportingObjects.Timer;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;



public abstract class Database {

	Bookmarks plugin;
	Connection connection;

	public Database(Bookmarks plugin){
		this.plugin = plugin;
	}

	public abstract Connection getSQLConnection();

	public abstract void load();


	/**
	 * Run once on plugin load.  Display some settings and load objects
	 */
	public void initialize(){
		
		connection = getSQLConnection();
		
		String initInfoSQL = "SELECT * FROM settings WHERE name = 'DBVersion' LIMIT 1;";
		
		try{
			PreparedStatement ps = connection.prepareStatement(initInfoSQL);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				if ( rs.getString("name") != null )
					
					plugin.getLogger().info( rs.getString("name") + ": " + rs.getString("txtvalue"));
			
			close(ps,rs);
			
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
		
		
		loadMarkers();
		loadPlayerMaps();
		
	}


	public void close(PreparedStatement ps,ResultSet rs){
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			Error.close(plugin, ex);
		}
	}



	
	/**
	 * retrieve marker information from database and create/register new marker
	 * objects
	 */
	public void loadMarkers() {
		connection = getSQLConnection();
		String markerLoadSQL = "SELECT * FROM markers;";
		try {
			plugin.getLogger().info("Loading markers");
			int count = 0;
			Timer timer = new Timer();

			PreparedStatement ps = connection.prepareStatement(markerLoadSQL);
			ResultSet rs = ps.executeQuery();

			while (rs.next())
				if (rs.getString("id") != null) {
					plugin.getMarkerManager().loadMarker(
							rs.getInt("id"), 
							rs.getString("name"), 
							rs.getInt("playerid"),
							rs.getString("world"), 
							rs.getFloat("x"), 
							rs.getFloat("y"), 
							rs.getFloat("z"));
					count++;
				}
			close(ps, rs);
			plugin.getLogger().info(count + " markers loaded in " + timer.stop() + "ms");

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}

	}

	
	/**
	 * populate player hashmaps
	 */
	public void loadPlayerMaps(){
		connection = getSQLConnection();

		String playerLoadSQL = "SELECT * FROM players;";
		
		
		try{
			PreparedStatement ps = connection.prepareStatement(playerLoadSQL);
			ResultSet rs = ps.executeQuery();
			plugin.getLogger().info("Loading players");
			Timer timer = new Timer();
			int count = 0;
			
			while (rs.next())
				if ( rs.getString("id") != null ){
					plugin.getMarkerManager().loadPlayer( 
							rs.getInt("id"),
							rs.getString("uuid"),
							rs.getString("name")
							);
					count++;
				}
	
			close(ps,rs);
			plugin.getLogger().info(count + " players loaded in " + timer.stop() + "ms" );
			
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		} finally {
		} 
		
	}


	/**
	 * Get all bookmarks for a player and give them to caller in an array
	 * by the given sort order
	 * 
	 * @param uuid
	 * @return
	 */
	public ArrayList<Bookmark> getPlayerBookmarks(UUID uuid) {
		
		
		plugin.debug(DebugType.DATABASE, "PlayerBookmarks DB pull for: " + uuid.toString());
		
		
		String sql = "SELECT id, markerid, playerid, sort FROM bookmarks "
					+"WHERE playerid = (SELECT id from players where uuid = ?) "
					+"ORDER BY sort ASC;";

		ArrayList<Bookmark> markermap = new ArrayList<Bookmark>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(sql);

			ps.setString(1, uuid.toString() );
			
			rs = ps.executeQuery();

			while(rs.next())
			{
		
				markermap.add( 
						new Bookmark(
								plugin,
								rs.getInt("id"),
								rs.getInt("markerid"),
								rs.getInt("playerid"),
								rs.getInt("sort")
								)
						);
			}
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			close(ps,rs);
		}  

		return markermap;
		
	}
	
	
	/**
	 * Runs INSERT OR REPLACE INTO bookmarks for the bookmark.
	 * 
	 * @param bookmark
	 */
	public void addOrSavePlayerBookmark(ArrayList<Bookmark> bookmarks) {
		
		plugin.debug(DebugType.DATABASE, "addOrSavePlayerBookmarks received  " + bookmarks.size() + "  bookmarks to update");
		
		// Generate some SQL
		String sql = "INSERT OR REPLACE INTO bookmarks (id,markerid,playerid,sort) VALUES ";

		for( Bookmark bookmark : bookmarks){
			int bookmarksid = bookmark.getBookmarksid();
			int markerid = bookmark.getMarkerid();
			int playerid = bookmark.getPlayerid();
			int sort = bookmark.getPlayersort();
			plugin.debug(DebugType.DATABASE, "addOrSavePlayerBookmark(BM) bookmarksid = " + bookmarksid + " sort: " + sort );
			
			sql = sql + "(" + bookmarksid + ", " + markerid + ", " + playerid + ", " + sort + "), ";
		}
		
		// cleanup sql to remove last comma
		sql = sql.substring(0,sql.lastIndexOf(","));
		
		plugin.debug(DebugType.DATABASE, "SQL= " + sql);
//      Example (formatted ) SQL:		
//		INSERT OR REPLACE INTO bookmarks (id,markerid,playerid,sort) 
//		VALUES 
//		(218, 218, 1, 3), 
//		(219, 219, 1, 4), 
//		(220, 220, 1, 5), 
//		(221, 221, 1, 6), 
//		(222, 222, 1, 7), 
//		(223, 223, 1, 8), 
//		(224, 224, 1, 9)
				

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(sql);

			ps.executeUpdate();

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		} 
		
	}
	
	
	
	
	
	


	/*******************************************************
	 * Updates players table.  Is called every time a player joins
	 * @param player
	 */
	public void updatePlayer(Player player) {

		String sql = "INSERT OR REPLACE INTO players (id,uuid,name) "
				+ "VALUES(COALESCE( (SELECT id FROM players WHERE uuid = ?), null), ?, ?);";


		String uuid = player.getUniqueId().toString();
		String name = player.getPlayerListName();

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(sql);

			ps.setString(1, uuid);
			ps.setString(2, uuid);
			ps.setString(3, name);

			ps.executeUpdate();
			
			// trigger refresh of player hashmaps
			plugin.getMarkerManager().refreshPlayers();

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);


		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
	}

	
	
	/**
	 * Adds a new marker to the database given a marker name, location, and player object
	 * Assumes object refresh/removal handled elsewhere.
	 * 
	 * @param markername
	 * @param location
	 * @param player
	 * @return
	 */
	public int addNewMarker(String markername, Location location, Player player) {

		String sql = "INSERT INTO markers (name, playerid, world, x, y ,z) "
				+ "SELECT ?, p.id, ?, ?, ?, ? "
				+ "FROM players p "
				+ "WHERE p.uuid = ? ";

		String worlduid = location.getWorld().getUID().toString();
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		
		int newmarkerid = 0;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(sql);

			ps.setString(1, markername);
			ps.setString(2, worlduid);
			ps.setDouble(3, x);
			ps.setDouble(4, y);
			ps.setDouble(5, z);
			ps.setString(6, player.getUniqueId().toString());

			ps.executeUpdate();

			rs = ps.getGeneratedKeys();

			int count = 0;
			while (rs.next()){
				newmarkerid = rs.getInt(1);
				
				plugin.debug(DebugType.DATABASE, "addNewMarker[" + count +"]: " + newmarkerid + " - " + markername );
				
			}
		} catch (SQLException ex) {

			String okerror = "[SQLITE_CONSTRAINT]  Abort due to constraint violation (column uuid is not unique)";
			if ( !ex.getLocalizedMessage().toString().equals(okerror) )
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);

// TODO:  Cleanup methods that have a bunch of try/catch nonsense.  isn't this why close() exists?
			
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		
		plugin.debug(DebugType.DATABASE, "addNewMarker() -RETURN-: " + newmarkerid );
		return newmarkerid;
	}


	
	/**
	 * Returns a map of all players and the current sort for a given markerid
	 * @param markerid
	 * @return
	 */
	public HashMap<UUID, Integer> getPlayersAndBookmarkIDsforMarker(int markerid) {
		HashMap<UUID,Integer> playerBookmarksmap = new HashMap<UUID,Integer>();
		
		String sql = "SELECT p.uuid, b.id FROM bookmarks b "
					+"JOIN players p on p.id = b.playerid "
					+"WHERE b.markerid = ? ";
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(sql);

			ps.setInt(1, markerid );
			
			rs = ps.executeQuery();

			while(rs.next()){
				playerBookmarksmap.put(UUID.fromString( rs.getString("uuid")), rs.getInt("id") );
			}
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			close(ps,rs);
		}  
		
		plugin.debug(DebugType.DATABASE, "getPlayersAndBookmarkIDsforMarker returning: " + playerBookmarksmap.size() + " results");
		
		return playerBookmarksmap;
	}
	
	
	
	/**
	 * Removes marker from database by id.  Also removes references to
	 * Marker from the Bookmarks table.  
	 * 
	 * Assumes object refresh/removal and Bookmark resort are handled elsewhere.
	 * @param markerid
	 */
	public void removeMarker(int markerid) {
		
		String sql = "DELETE FROM markers WHERE id = ?;";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(sql);

			ps.setInt(1, markerid);

			ps.executeUpdate();

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
				
				plugin.debug(DebugType.DATABASE, "removeMarker OK. Now removeMarkerFromPlayers");
				
//				removeMarkerFromPlayers( markerid );
				
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}  

	}
	
//	/**
//	 * Removes a bookmark for all players that had it.
//	 * Usually called automatically by removeMarker(int markerid)
//	 *  
//	 * @param markerid
//	 */
//	public void removeMarkerFromPlayers(int markerid) {
//
//		String sql = "DELETE FROM bookmarks WHERE markerid = ?;";
//
//		Connection conn = null;
//		PreparedStatement ps = null;
//		try {
//			conn = getSQLConnection();
//			ps = conn.prepareStatement(sql);
//
//			ps.setInt(1, markerid);
//
//			ps.executeUpdate();
//
//		} catch (SQLException ex) {
//			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
//		} finally {
//			try {
//				if (ps != null)
//					ps.close();
//				if (conn != null)
//					conn.close();
//				plugin.debug(DebugType.DATABASE, "removeMarkerFromPlayers OK"); 
//			} catch (SQLException ex) {
//				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
//			}
//		}    
//	}

	
	public void removeBookmarksid(int bookmarksid) {
		
		plugin.debug(DebugType.DATABASE, "Removing boomarks ID: " + bookmarksid);
		
		String sql = "DELETE FROM bookmarks WHERE id = ?;";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(sql);

			ps.setInt(1, bookmarksid);

			ps.executeUpdate();


		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
				
				plugin.debug(DebugType.DATABASE, "removeBookmarksid( " + bookmarksid + " ) OK"); 
				
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}    
	}

	

	
	
	
	
	/**
	 * Adds a bookmark for a player.  Assumes object refresh/removal handled elsewhere.
	 * 
	 * @param markerid
	 * @param playerid
	 */
	public void addBookmarkForPlayer(int markerid, int playerid) {

		String sql = "INSERT INTO bookmarks (markerid,playerid,sort) "
				+ "SELECT ?, ?, coalesce((SELECT max(sort) + 1 from bookmarks where playerid = ?),0); ";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement(sql);

			ps.setInt(1,markerid);
			ps.setInt(2, playerid);
			ps.setInt(3, playerid);

			ps.executeUpdate();

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}            
	}

	


	
	
}
	







	/******************************************************************************
	 * 
	 * EXAMPLE METHODS BELOW
	 * 
	 *****************************************************************************/
	// These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
	// This returns the number of people the player killed.
//	public void getSpammed(Player player) {
//
//		String sql = "SELECT * FROM settings;";
//
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try {
//			conn = getSQLConnection();
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			while(rs.next()){
//
//				// Code per-entry here
//				player.sendMessage( 
//						" name: " + rs.getString("name") + 
//						" txtvalue: " + rs.getString("txtvalue") +
//						" intvalue: " + rs.getInt("intvalue") );
//
//			}
//		} catch (SQLException ex) {
//			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
//		} finally {
//			try {
//				if (ps != null)
//					ps.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException ex) {
//				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
//			}
//		}  
//	}
//
//	//Now we need methods to save things to the database
//	public void addSpam(Player player, String txtvalue, Integer intvalue) {
//
//		String sql = "REPLACE INTO settings (name,txtvalue,intvalue) VALUES(?,?,null)";
//
//		Connection conn = null;
//		PreparedStatement ps = null;
//		try {
//			conn = getSQLConnection();
//			ps = conn.prepareStatement(sql);
//
//			ps.setString(1, txtvalue);
//			ps.setString(2, player.getName().toLowerCase());
//
//			ps.executeUpdate();
//			//			return;
//		} catch (SQLException ex) {
//			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
//		} finally {
//			try {
//				if (ps != null)
//					ps.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException ex) {
//				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
//			}
//		}
//		return;             
//	}


	//END


