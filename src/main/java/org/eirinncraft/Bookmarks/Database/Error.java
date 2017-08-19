package org.eirinncraft.Bookmarks.Database;

import java.util.logging.Level;

import org.eirinncraft.Bookmarks.Bookmarks;

public class Error {
    public static void execute(Bookmarks plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);     
    }
    public static void close(Bookmarks plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}