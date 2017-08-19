package org.eirinncraft.Bookmarks.Books.BookmarksBook;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.BookCommand;
import org.eirinncraft.Bookmarks.SupportingObjects.Bookmark;
import org.eirinncraft.Bookmarks.SupportingObjects.Marker;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;

import xyz.upperlevel.spigot.book.BookUtil;

public class ClickSortCommand extends BookCommand {

	public enum SortDirection { UP, DOWN };
	
	private Bookmarks plugin;
	private UUID uuid;
	private Marker marker;
	private SortDirection sortdirection;
	
	/**
	 * The ClickSort Command!
	 * 
	 * @param plugin
	 * @param uuid
	 * @param marker
	 * @param sortdirection
	 */
	public ClickSortCommand(Bookmarks plugin, UUID uuid, Marker marker, SortDirection sortdirection) {
		super(plugin, uuid, "ClickSort");
		
		this.plugin = plugin;
		this.uuid = uuid;
		this.marker = marker;
		this.sortdirection = sortdirection;
		
	}

	@Override
	public void execute() {
		
		ArrayList<Bookmark> list = plugin.getMarkerManager().getPlayerBookmarks( uuid );

		// get current bookmark for this marker 
		Bookmark currentBookmark = null;
		for( int i = 0; i < list.size(); i++ ){
			if( list.get(i).getMarker().equals( marker ) )
				currentBookmark = list.get(i);
		}
		
		
		// get the target bookmark
		
		/////////////////////////// DANGER //////////////////////////// 
		// We are trusting that this command won't be asked to move  //
		//  a bookmark where it can't go                             //
		//  (ex: up 1 when it's already at the top)                  //
		///////////////////////////////////////////////////////////////
		Bookmark targetBookmark = null;
		if( sortdirection.equals(SortDirection.UP) )
			targetBookmark = list.get( currentBookmark.getPlayersort() - 1 );
		else
			targetBookmark = list.get( currentBookmark.getPlayersort() + 1 );
		
		// mark these dirty
		currentBookmark.setDirty(true);
		targetBookmark.setDirty(true);
		
		// change their sort
		int targetsort = targetBookmark.getPlayersort();
		targetBookmark.setPlayersort( currentBookmark.getPlayersort() );
		currentBookmark.setPlayersort( targetsort );
		
		// write changes to db and force refresh
		plugin.debug(DebugType.BOOK_COMMAND, "Saving player bookmarks");
		plugin.getMarkerManager().savePlayerBookmarks( uuid );
		plugin.debug(DebugType.BOOK_COMMAND, "Forcing refresh");
		plugin.getMarkerManager().getPlayerBookmarks( uuid, true );
		
		// reopen the book for the player
		plugin.debug(DebugType.BOOK_COMMAND, "Reopening book");
		
		
//		BookUtil.openPlayer( plugin.getServer().getPlayer( uuid ), plugin.getLibrarian().getBookmarksBook( uuid ).getBook() );
		
		
		//reopen book in 1 tick as we get a null pointer when we give it immediately back to the player
		// ... this feels like a hack ...
		new BukkitRunnable() {
            @Override
            public void run() {
            	BookUtil.openPlayer( plugin.getServer().getPlayer( uuid ), plugin.getLibrarian().getBookmarksBook( uuid ).getBook() );
            }
        }.runTaskLater(this.plugin, 1);
        // no more runnable
				
	}


}

















