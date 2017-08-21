package org.eirinncraft.Bookmarks.Books;

import java.util.UUID;

import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;

import lombok.Getter;

public abstract class BookCommand {
	
	@Getter
	private String token;
	
	public BookCommand(Bookmarks plugin, UUID uuid) {
		this.token = plugin.getLibrarian().registerCommand(uuid, this);
		plugin.debug(DebugType.BOOK_COMMAND,
				"registered token: " + token + " for: " + plugin.getServer().getPlayer( uuid ).getName() + " command: " + getCommandName());
	}
	
	public String getCommand() { return "/" + Bookmarks.MAIN_COMMAND + " " + token;	}
	
	public abstract String getCommandName();
	
	public abstract void execute();
	
}