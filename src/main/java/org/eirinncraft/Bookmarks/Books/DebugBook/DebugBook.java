package org.eirinncraft.Bookmarks.Books.DebugBook;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.Librarian.Book;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger;
import org.eirinncraft.Bookmarks.SupportingObjects.Debugger.DebugType;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.upperlevel.spigot.book.BookUtil;
import xyz.upperlevel.spigot.book.BookUtil.PageBuilder;

public class DebugBook implements Book{
	
	private Debugger debug;
	private Bookmarks plugin;
	private UUID uuid;
	
	public DebugBook(Bookmarks plugin, UUID uuid){
		this.plugin = plugin;
		this.uuid = uuid;
		this.debug = plugin.getDebugger();
		
	}

	@Override
	public ItemStack getBook() {
		
		List<BaseComponent[]> pages = new ArrayList<BaseComponent[]>();		
		PageBuilder page = new BookUtil.PageBuilder();
		
		// display values and current setting for each debug type
		for( int i = 0; i < DebugType.values().length; i++){
			page.add(
					new TextComponent( ChatColor.BLACK + DebugType.values()[i].toString() + ":" )
					);
			page.newLine();
			if( debug.isTypeActive( DebugType.values()[i]) ){
				
				page.add(
						BookUtil.TextBuilder.of(" Enabled ")
						.color(ChatColor.DARK_GREEN)
						.onHover( BookUtil.HoverAction.showText("Click to Disable " + DebugType.values()[i].toString()))
						.onClick( BookUtil.ClickAction.runCommand( new ToggleDebugCommand(plugin, uuid, DebugType.values()[i]).getCommand() ))
						.build()
						);
			} else {
				page.add( 
						BookUtil.TextBuilder.of(" Disabled ")
						.color(ChatColor.DARK_RED)
						.onHover( BookUtil.HoverAction.showText("Click to Enable " + DebugType.values()[i].toString()))
						.onClick( BookUtil.ClickAction.runCommand( new ToggleDebugCommand(plugin, uuid, DebugType.values()[i]).getCommand() ))
						.build()
						);
			}
			page.newLine();
		}
		
		pages.add(page.build());
		
		page = new BookUtil.PageBuilder();
		page.add(
				BookUtil.TextBuilder.of("Force player bookmark refresh from DB")
				.color(ChatColor.DARK_BLUE)
				.onHover( BookUtil.HoverAction.showText("Click to force a refresh from database"))
				.onClick( BookUtil.ClickAction.runCommand( new ForceRefreshFromDBCommand(plugin, uuid).getCommand() ))
				.build()
				);
		
		
		pages.add(page.build());
		ItemStack book = setAuthorAndTitle(BookUtil.writtenBook().pages(pages).build());

		return book;
	}

	@Override
	public List<String> getLore() {
		// this is not a book that will exist as inventory
		// so OK to return null
		return null;
	}

	@Override
	public ItemStack setAuthorAndTitle(ItemStack book) {
		BookMeta book_meta = (BookMeta) book.getItemMeta();
		book_meta.setTitle("Bookmarks");
		String author = plugin.getConfig().getString("bookauthor");
		book_meta.setAuthor(author);
		book.setItemMeta(book_meta);
		plugin.debug(Debugger.DebugType.BOOK, "so it is written!  FINAL book_meta.getPageCount() = " + book_meta.getPageCount());
		return book;
	}
}
