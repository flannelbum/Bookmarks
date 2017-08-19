package org.eirinncraft.Bookmarks.Books;

import java.util.UUID;

import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;

import lombok.Getter;

public abstract class BookCommand {
	@Getter
	private String token;
	@Getter
	private String commandName = "";
	
	public BookCommand(Bookmarks plugin, UUID uuid, String commandname) {
		this.token = plugin.getLibrarian().registerCommand(uuid, this);
		this.commandName = commandname;
		plugin.debug(DebugType.BOOK_COMMAND,
				"registered token: " + token + " for: " + plugin.getServer().getPlayer( uuid ).getName() + " command: " + commandname);
	}
	public String getCommand() { return "/" + Bookmarks.MAIN_COMMAND + " " + token;	}
	public abstract void execute();
}