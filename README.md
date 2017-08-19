# Bookmarks

### Bookmarks is a Minecraft Spigot plugin that provides players an easy way to create, share, and manage warp locations.
Tested with Minecraft version 1.12.1 and _should_ work from 1.9+

Once installed, use the **/bookmarks** or **/bm** command to summon a Bookmark book.  The first page of the book reads: 


	Name a Diamond block in an Anvil then place 
	that block of Diamond somewhere to create 
	a marker to warp to.
	
	Punch other markers with this book in your 
	hand to get it added it to your list.

 
The Bookmarks book is unique for each player.  Its contents are generated when the book is opened.  If a player gets another player's book, they will still only see their own bookmarks when the book is opened. The Bookmarks book can be summoned at any time and will appear once in a player's inventory.

Players that place a named diamond block create a Marker from that block of diamond.  The top of the Marker is a warp target which will appear in player's Bookmarks book.  The player that created the Marker and any players that use their Bookmarks book to punch the Marker will see the entry in their Bookmarks book.  The player that created the Marker can see and remove any other players individually who have punched their Marker.  The Marker creator can also destroy the Marker by breaking the block of diamond.  This will remove the Marker from all other player's books.

Markers require 4 blocks of air (with some exceptions) above the top of the Marker for a player teleport to work.  Tripwire, pressure plates, and still water are all valid blocks that can still be "on top" of the Marker and will not prevent a player teleport.      


### v0.1.0

Initial commit/pre-release

To-do list:
* more performance testing / further code-path optimizations and cleanup
* add admin book that will allow staff to inspect all bookmarks
* finalize permission nodes and the way the librarian issues/registers books

#### Compiled Dependencies
This Bookmarks project packages and utilizes upperlevel's excellent [spigot-book-api](https://github.com/upperlevel/spigot-book-api)

#### Pull Requests welcome!

