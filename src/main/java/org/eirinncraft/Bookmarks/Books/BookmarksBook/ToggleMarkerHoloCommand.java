package org.eirinncraft.Bookmarks.Books.BookmarksBook;

import java.util.UUID;

import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.BookCommand;
import org.eirinncraft.Bookmarks.SupportingObjects.Marker;

public class ToggleMarkerHoloCommand extends BookCommand{

	private Bookmarks plugin;
	private Marker marker;
	
	public ToggleMarkerHoloCommand(Bookmarks plugin, UUID uuid, Marker marker) {
		super(plugin, uuid);
		
		this.plugin = plugin;
		this.marker = marker;
	}

	@Override
	public String getCommandName() {
		return "ToggleMarkerHoloCommand";
	}

	@Override
	public void execute() {
		plugin.getMarkerManager().toggleHolo(marker);
		
	}

}
