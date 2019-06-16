package org.eirinncraft.Bookmarks.Books.BookmarksBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.Librarian.Book;
import org.eirinncraft.Bookmarks.Books.BookmarksBook.ClickSortCommand.SortDirection;
import org.eirinncraft.Bookmarks.SupportingObjects.Bookmark;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;
import org.eirinncraft.Bookmarks.SupportingObjects.Marker;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.upperlevel.spigot.book.BookUtil;
import xyz.upperlevel.spigot.book.BookUtil.PageBuilder;

public class BookmarksBook implements Book {

	private Bookmarks plugin;
	private UUID uuid;
	
	
	/**
	 * The Constructor here has a plugin and uuid param
	 * other books can instantiate with or without a constructor
	 * as our Librarian.Book object is just an interface.
	 * 
	 * However, if the book should execute commands, plugin and uuid
	 * are required.
	 * 
	 */
	public BookmarksBook(Bookmarks plugin, UUID uuid) {
		this.plugin = plugin;
		this.uuid = uuid;
	}

	
	/**
	 * Any number of method(s) can be used to generate the book but 
	 * we MUST return one as a concrete ItemStack item. 
	 */
	@Override
	public ItemStack getBook() {

		ArrayList<Bookmark> marks = plugin.getMarkerManager().getPlayerBookmarks(uuid);
		List<BaseComponent[]> pages = new ArrayList<BaseComponent[]>();		
		PageBuilder page = null;//new BookUtil.PageBuilder();
		
		plugin.debug(DebugType.BOOK,"Entered: getBook(" + uuid.toString() + ").  Has  " + marks.size() + " bookmarks");
		
		if (marks.size() > 0) {

			Bookmark bookmark;
			Marker marker;
	
			for (int i = 0; i < marks.size(); i++) {
				bookmark = marks.get(i);
				marker = bookmark.getMarker();
				
				// this is used more than once in the text onClick events so only generate it once.
				String tpcommandstring = new TeleportCommand(plugin, uuid, marker).getCommand();

				
				// There are 14 lines in a book page.  
				// remember, list is 0-based. bookmark 7 is 
				// actually the 8th item.
				if ((i % 7) == 0) { // 7 bookmarks to a page.
					plugin.debug(DebugType.BOOK, i + " % 7 == 0 !!" );
					if (i > 0) {// handle when we're bookmark 7, 14, 21, etc...
						pages.add(page.build());
						plugin.debug(DebugType.BOOK, "page added to pages.  New pages.size() = " + pages.size());
					}
					plugin.debug(DebugType.BOOK, "Generating new PageBuilder for page");
					page = new BookUtil.PageBuilder(); // new page or first
														// bookmark
				}

				
				
				// Actual code that comprises a bookmark for display
				
				
				
				// First line - "Markername"
				
				// space out the up arrow on the first bookmark
				if( i == 0 ){
					plugin.debug(DebugType.BOOK, "adding content to page: blankarrowup");
					page.add(new TextComponent("  "));
				} else {
					plugin.debug(DebugType.BOOK, "adding content to page: up arrow");
					page.add(
							// Up arrow element
							BookUtil.TextBuilder.of( "\u21E7 ") // utf up-arrow
							.color(ChatColor.BLACK)
							.onHover(BookUtil.HoverAction.showText("Move " + marker.getMarkername() + " up"))
							.onClick(BookUtil.ClickAction.runCommand( new ClickSortCommand(plugin, uuid, marker, SortDirection.UP).getCommand() ))
							.build()
							);
				}
				plugin.debug(DebugType.BOOK, "adding content to page: markername: " + marker.getMarkername());
				page.add(
						
						BookUtil.TextBuilder.of( 
								// Some light string manipulation to prevent wordwrapping in the book
								//  "%-50s" basically means "widen field to 50 chars and left align it"
								// we then only use the first 18 chars.
								// This should give the player's mouse more to click on for short names
								String.format("%-50s", marker.getMarkername()).substring(0, 18)
								)
    					.onHover(BookUtil.HoverAction.showText(
    							"Teleport to: " + ChatColor.AQUA + marker.getMarkername() + ChatColor.RESET
    							+ "\n(" + marker.getWorldName() +") X:" + marker.getX() + " Y:" + marker.getY() + " Z:" + marker.getZ()
    							))
    					.onClick(BookUtil.ClickAction.runCommand( tpcommandstring ))
    					.color(ChatColor.DARK_AQUA)
    					.build()
    					);
				plugin.debug(DebugType.BOOK, "adding newLine() to page");
				page.newLine();
				
				
				// Second line - " ~You/ownername"
				
				// space out the down arrow if we're the last bookmark
				if( i == marks.size() - 1 ){
					plugin.debug(DebugType.BOOK, "adding content to page: blankdownarrow");
					page.add(new TextComponent("   "));
				} else {
					plugin.debug(DebugType.BOOK, "adding content to page: downarrow");
					page.add(
							// Down arrow element
							BookUtil.TextBuilder.of( "\u21E9  ") // utf down-arrow
							.color(ChatColor.BLACK)
							.onHover(BookUtil.HoverAction.showText("Move " + marker.getMarkername() + " down"))
							.onClick(BookUtil.ClickAction.runCommand( new ClickSortCommand(plugin, uuid, marker, SortDirection.DOWN).getCommand() ))
							.build()
							);
				}

				if( marker.getOwnerUUID().equals( uuid )){
					plugin.debug(DebugType.BOOK, "adding content to page: markerownerinfo");
					
					String others = "";
					HashMap<UUID,Integer> playerswithmap = plugin.getDB().getPlayersAndBookmarkIDsforMarker( marker.getMarkerid() );
					
					if( playerswithmap.size() > 1 ){
						others = "+" + (playerswithmap.size() - 1);
						if( playerswithmap.size() == 2 ){
							others = others + " other";
						} else {
							others = others + " others";
						}
					}
					
					String viewotherscommand = new ViewOthersOpenBookCommand(plugin, uuid, marker, playerswithmap).getCommand();
					if( others == "" ){
					page.add( 
							
							BookUtil.TextBuilder.of(
									"~You "
									)
							.onHover(BookUtil.HoverAction.showText(
	    							"Click to manage: " + ChatColor.AQUA + marker.getMarkername()	))
	    					.onClick(BookUtil.ClickAction.runCommand( viewotherscommand ))
	    					.color(ChatColor.DARK_GREEN)
	    					.style(ChatColor.ITALIC)
	    					.build()
							);
					page.add(
							BookUtil.TextBuilder.of( 
									"            " // space added to allow for larger mouse "target" in book
									)
	    					.onHover(BookUtil.HoverAction.showText(
	    							"Teleport to: " + ChatColor.AQUA + marker.getMarkername() + ChatColor.RESET
	    							+ "\n(" + marker.getWorldName() +") X:" + marker.getX() + " Y:" + marker.getY() + " Z:" + marker.getZ()
	    							))
	    					.onClick(BookUtil.ClickAction.runCommand( tpcommandstring ))
	    					.color(ChatColor.DARK_GREEN)
	    					.style(ChatColor.ITALIC)
	    					.build()
							
							);
					} else {
						viewotherscommand = new ViewOthersOpenBookCommand(plugin, uuid, marker, playerswithmap).getCommand();
						
						page.add(
								BookUtil.TextBuilder.of(
										"~You "
										)
								.onHover(BookUtil.HoverAction.showText(
		    							"Click to manage: " + ChatColor.AQUA + marker.getMarkername()	))
		    					.onClick(BookUtil.ClickAction.runCommand( viewotherscommand ))
		    					.color(ChatColor.DARK_GREEN)
		    					.style(ChatColor.ITALIC)
		    					.build()
								);
						page.add(
								BookUtil.TextBuilder.of(
										others
										)
								.onHover(BookUtil.HoverAction.showText(
		    							"Teleport to: " + ChatColor.AQUA + marker.getMarkername() + ChatColor.RESET
		    							+ "\n(" + marker.getWorldName() +") X:" + marker.getX() + " Y:" + marker.getY() + " Z:" + marker.getZ()
		    							))
		    					.onClick(BookUtil.ClickAction.runCommand( tpcommandstring ))
		    					.color(ChatColor.DARK_GREEN)
		    					.style(ChatColor.ITALIC)
		    					.build()
								);
					}
					
					
					
				} else {
					plugin.debug(DebugType.BOOK, "adding content to page: markerownerinfo");
					page.add( 
							BookUtil.TextBuilder.of( 
									// Again, some light string manipulation to prevent wordwrapping
									"~" + String.format("%-50s", marker.getOwnerPlayername() ).substring(0,17) 
									)
	    					.onHover(BookUtil.HoverAction.showText(
	    							"Teleport to: " + ChatColor.AQUA + marker.getMarkername() + ChatColor.RESET
	    							+ "\n(" + marker.getWorldName() +") X:" + marker.getX() + " Y:" + marker.getY() + " Z:" + marker.getZ()
	    							))
	    					.onClick(BookUtil.ClickAction.runCommand( tpcommandstring ))
	    					.color(ChatColor.DARK_RED)
	    					.style(ChatColor.ITALIC)
	    					.build()
							
							);
				}
				// finalize with a new line
				plugin.debug(DebugType.BOOK, "adding newLine() to page");
				page.newLine();
				
			}
		} else {
			// Player has no stored bookmarks. Give them something to read
			// anyway.
			plugin.debug(DebugType.BOOK, "Generating new PageBuilder() for page and generating content (default)");
			page = new BookUtil.PageBuilder().add(
					new TextComponent("Name a Diamond block in an Anvil then place that block of Diamond somewhere "
							+ "to create a marker to warp to.\n\nPunch other markers with this book in "
							+ "your hand to get it added it to your list."));
		}
		
		

		pages.add(page.build());
		plugin.debug(DebugType.BOOK, "FINAL page added to pages.  New pages.size() = " + pages.size());

		
		plugin.debug(DebugType.BOOK, "BookmarksBook.GetBook() FINAL page size: " + pages.size());
		
		ItemStack book = BookUtil.writtenBook().pages(pages).build();
		
		
		
		// Still set some meta manually. Wanted colors in the item display name
		// and needed to generate lore without generating the full book
		// as lore is used in event listeners and handlers
		BookMeta book_meta = (BookMeta) book.getItemMeta();				
		book_meta.setDisplayName(ChatColor.AQUA + "Bookmarks");
		
		plugin.getConfig().addDefault("bookauthor", "The Server Librarian");
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
		String author = plugin.getConfig().getString("bookauthor");
		plugin.saveConfig();
		
		book_meta.setAuthor(author);
		
		book_meta.setLore(getLore());
		
		book.setItemMeta(book_meta);

		plugin.debug(DebugType.BOOK, "so it is written!  FINAL book_meta.getPageCount() = " + book_meta.getPageCount());
		return book;

	}

	/**
	 * This is used in event listeners to determine if a player holding the book
	 * in-world is this book.  Changes to lore will cause "old" versions to no
	 * longer work.
	 * 
	 * If a book doesn't need lore (it's a transient menu or selection GUI) this
	 * can return null as those books shouldn't exist in-world to be handled.
	 */
	@Override
	public List<String> getLore() {
		List<String> lore = new ArrayList<String>();
//		lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "All punched or created");
//		lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "warp points.");
//		lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Reader only sees their");
//		lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "bookmarks. Bookmarks are");
//		lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "removed from all books");
//		lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "when warp is broken.");
		lore.add("All punched or created");
		lore.add("warp points.");
		lore.add("Reader only sees their");
		lore.add("bookmarks. Bookmarks are");
		lore.add("removed from all books");
		lore.add("when warp is broken.");

		return lore;
	}

}
