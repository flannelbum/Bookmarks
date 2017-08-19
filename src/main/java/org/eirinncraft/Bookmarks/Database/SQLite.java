package org.eirinncraft.Bookmarks.Database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Database.Database;

/***************
 * This util database is a slightly modified form of:
 * https://www.spigotmc.org/threads/how-to-sqlite.56847/
 */
public class SQLite extends Database {

	private String dbname = "Bookmarks";
	private List<String> tables;

	public SQLite(Bookmarks plugin) {
		super(plugin);

		tables = new ArrayList<String>();
		tables.add("CREATE TABLE IF NOT EXISTS `settings` (`name` TEXT NOT NULL UNIQUE,`txtvalue` TEXT,`intvalue` INTEGER);");
		tables.add("REPLACE INTO settings (name, txtvalue, intvalue) VALUES ('DBVersion', '0.1a', null);");
		tables.add("CREATE TABLE IF NOT EXISTS `players` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,`uuid` INTEGER NOT NULL UNIQUE,`name` INTEGER NOT NULL);");
		tables.add("CREATE TABLE IF NOT EXISTS `markers` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,`name` TEXT NOT NULL,`playerid` INTEGER NOT NULL,`world` TEXT NOT NULL,`x` REAL,`y` REAL,`z` REAL,FOREIGN KEY(`playerid`) REFERENCES player ( id ));");
		tables.add("CREATE TABLE IF NOT EXISTS `bookmarks` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,`markerid` INTEGER NOT NULL,`playerid` INTEGER NOT NULL,`sort` INTEGER NOT NULL,FOREIGN KEY(`markerid`) REFERENCES markers ( id ),FOREIGN KEY(`playerid`) REFERENCES players ( id ));");
	}

	public Connection getSQLConnection() {

		File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");

		if (!dataFolder.exists()) {
			try {
				plugin.getLogger().info("initializing database");
				plugin.saveConfig();
				dataFolder.createNewFile();

			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db");
			}
		}
		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
			return connection;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
		} catch (ClassNotFoundException ex) {
			plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
		}
		return null;
	}

	public void load() {
		connection = getSQLConnection();

		try {
			for (String table : tables) {
				Statement s = connection.createStatement();
				s.executeUpdate(table);
				s.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initialize();
	}
}