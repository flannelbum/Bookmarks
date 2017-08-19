package org.eirinncraft.Bookmarks.SupportingObjects;

import org.eirinncraft.Bookmarks.Bookmarks;

import lombok.Data;

@Data
public class Bookmark {

	private Bookmarks plugin;
	private int bookmarksid;
	private Marker marker;
	private int markerid;
	private int playerid;
	private int playersort;
	private boolean isDirty;

	public Bookmark(Bookmarks plugin, int bookmarksid, int markerid, int playerid, int playersort) {
		this.plugin = plugin;
		this.marker = plugin.getMarkerManager().getMarker(markerid);
		this.bookmarksid = bookmarksid;
		this.markerid = markerid;
		this.playerid = playerid;
		this.playersort = playersort;
		this.isDirty = false;
	}

}
