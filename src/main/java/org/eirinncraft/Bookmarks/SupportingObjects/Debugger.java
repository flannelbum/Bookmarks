package org.eirinncraft.Bookmarks.SupportingObjects;

import java.util.logging.Logger;

import org.eirinncraft.Bookmarks.Bookmarks;

public class Debugger {
	
	public static enum DebugType { BOOK, BOOK_COMMAND, DATABASE, INPUT_COMMAND, LIBRARIAN, LISTENER, MARKERMANAGER }
	
	private boolean book_debug = false;
	private boolean bookcommand_debug = false;
	private boolean database_debug = false;
	private boolean inputcommand_debug = false;
	private boolean librarian_debug = false;
	private boolean listener_debug = false;
	private boolean markermanager_debug = false;
	
	private Bookmarks plugin;
	private Logger log;
	
	public Debugger(Bookmarks plugin){
		this.plugin = plugin;
		this.log = plugin.getLogger();
		
		book_debug = plugin.getConfig().getBoolean("debug.book_debug");
		bookcommand_debug = plugin.getConfig().getBoolean("debug.bookcommand_debug");
		database_debug = plugin.getConfig().getBoolean("debug.database_debug");
		inputcommand_debug = plugin.getConfig().getBoolean("debug.inputcommand_debug");
		librarian_debug = plugin.getConfig().getBoolean("debug.librarian_debug");
		listener_debug = plugin.getConfig().getBoolean("debug.listener_debug");
		markermanager_debug = plugin.getConfig().getBoolean("debug.markermanager_debug");
	}
	
	public void log(DebugType type, String message){
		if( isTypeActive(type) )
			log.info("DEBUG (" + type.toString() + "): " + message);
	}

	public void toggleType(DebugType type){
		boolean newtypestate = !isTypeActive(type);
		
		switch(type){
		case BOOK: book_debug = newtypestate; break;
		case BOOK_COMMAND: bookcommand_debug = newtypestate; break; 
		case DATABASE: database_debug = newtypestate; break;
		case INPUT_COMMAND: inputcommand_debug = newtypestate; break;
		case LIBRARIAN: librarian_debug = newtypestate; break;
		case LISTENER: listener_debug = newtypestate; break;
		case MARKERMANAGER: markermanager_debug = newtypestate; break;
		}
		
		plugin.getConfig().set("debug.book_debug", book_debug);
		plugin.getConfig().set("debug.bookcommand_debug", bookcommand_debug);
		plugin.getConfig().set("debug.database_debug", database_debug);
		plugin.getConfig().set("debug.inputcommand_debug", inputcommand_debug);
		plugin.getConfig().set("debug.librarian_debug", librarian_debug);
		plugin.getConfig().set("debug.listener_debug", listener_debug);
		plugin.getConfig().set("debug.markermanager_debug", markermanager_debug);
		
		plugin.saveConfig();
		
	}
	
	public boolean isTypeActive(DebugType type){
		
		switch(type){
		
		case BOOK: return book_debug;
		case BOOK_COMMAND: return bookcommand_debug; 
		case DATABASE: return database_debug;
		case INPUT_COMMAND: return inputcommand_debug;
		case LIBRARIAN: return librarian_debug;
		case LISTENER: return listener_debug;
		case MARKERMANAGER: return markermanager_debug;
		
		default: return false;
		
		}
		
	}

}
