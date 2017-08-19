package org.eirinncraft.Bookmarks.Books.BookmarksBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.eirinncraft.Bookmarks.Bookmarks;
import org.eirinncraft.Bookmarks.Books.Librarian.Book;
import org.eirinncraft.Bookmarks.SupportingObjects.Marker;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.upperlevel.spigot.book.BookUtil;
import xyz.upperlevel.spigot.book.BookUtil.PageBuilder;

public class ViewOthersBook implements Book {

	private Bookmarks plugin;
	private Marker marker; 
	private UUID uuid;
	private HashMap<UUID, Integer> playerswith;
	
	public ViewOthersBook(Bookmarks plugin, UUID uuid, Marker marker, HashMap<UUID, Integer> playerswith) {
		this.plugin = plugin;
		this.marker = marker;
		this.uuid = uuid;
		this.playerswith = playerswith;
	}

	@Override
	public ItemStack getBook() {
		List<BaseComponent[]> pages = new ArrayList<BaseComponent[]>();		
	
		// Walk through players with this bookmark
		//  TODO: This needs refactoring in order to display multiple pages.
		
		int i = 0;
		PageBuilder page = new BookUtil.PageBuilder();
		page.add(new TextComponent( ChatColor.DARK_AQUA + marker.getMarkername()));
		page.newLine(); i++;

		for( Entry<UUID, Integer> entry : playerswith.entrySet() ){
			if( !entry.getKey().equals(uuid) ){
				page.add(
							BookUtil.TextBuilder.of(" " + plugin.getMarkerManager().lookupPlayernameFromUUID( entry.getKey() ))
							.color(ChatColor.DARK_RED)
							.style(ChatColor.ITALIC)
							.onHover( BookUtil.HoverAction.showText("Click to remove " + plugin.getMarkerManager().lookupPlayernameFromUUID( entry.getKey() ) 
									+ "\nfrom: " + ChatColor.AQUA + marker.getMarkername() ))
							.onClick( BookUtil.ClickAction.runCommand( new DeleteMarkerFromPlayerCommand(plugin, uuid, marker, entry.getValue(), entry.getKey()).getCommand() ))
							.build()
							);
				page.newLine(); i++;
			}
			if( i % 14 == 0 ){
				pages.add(page.build());
				page = new BookUtil.PageBuilder();
				page.add(new TextComponent( ChatColor.DARK_AQUA + marker.getMarkername()));
				page.newLine(); i++;
			}
		}
		
		pages.add(page.build());
		
		ItemStack book = BookUtil.writtenBook().pages(pages).build();
		return book;
	}

	@Override
	public List<String> getLore() {
		return null;
	}

}
