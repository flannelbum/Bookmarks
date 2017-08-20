package org.eirinncraft.Bookmarks;

import org.bukkit.plugin.java.JavaPlugin;
import org.eirinncraft.Bookmarks.Books.Librarian;
import org.eirinncraft.Bookmarks.Database.Database;
import org.eirinncraft.Bookmarks.Database.SQLite;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;
import org.eirinncraft.Bookmarks.SupportingObjects.MarkerManager;

import lombok.Getter;

public class Bookmarks extends JavaPlugin {

	public static final String MAIN_COMMAND = "bookmarks";

	@Getter 
	private MarkerManager MarkerManager;
	@Getter
	private Librarian Librarian;
	@Getter
	private Debugger debugger;
	
	private Database db;

	@Override
	public void onEnable() {
		if(debugger == null)
			debugger = new Debugger(this);
		
		if(MarkerManager == null)
			MarkerManager = new MarkerManager(this);
		
		if(Librarian == null)
			Librarian = new Librarian(this);

		if(db == null)
			db = getDB();
		

		getCommand(MAIN_COMMAND).setExecutor(new MainCommand(this));
		
		getServer().getPluginManager().registerEvents(new BookmarkListener(this),this);

	}
	
	public Debugger debug(DebugType type, String message){
		if(type != null && message != null)
			debugger.log(type, message);	
		return debugger;
	}
	
	public boolean debugTypeActive(DebugType type){
		if( debugger.isTypeActive(type))
			return true;
		return false;
	}
		
	public Database getDB() {
		if (db == null) {
			db = new SQLite(this);
			db.load();
		}
		return db;
	}

}
