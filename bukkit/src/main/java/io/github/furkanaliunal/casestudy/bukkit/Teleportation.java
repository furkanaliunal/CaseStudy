package io.github.furkanaliunal.casestudy.bukkit;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.github.furkanaliunal.casestudy.bukkit.Utils.MongoUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author OnwexryS
 * Teleportation record
 * Object to keep teleportation data
 */
public record Teleportation(Player player, Player targetPlayer) {
    /**
     * Static method to teleport player to another player
     * @param teleportation - Teleportation object to process
     */
    public static void processTeleportation(final Teleportation teleportation) {
        if (!teleportation.player.isOnline()) return;
        teleportation.player.teleportAsync(teleportation.targetPlayer.getLocation());
        logToMongo(teleportation);
    }

    /**
     * Static method to log teleportation to mongo
     * @see Filters
     * @see com.mongodb.client.MongoCollection
     * @param teleportation - Teleportation object to log
     */
    public static void logToMongo(final Teleportation teleportation) {
        final Location location = teleportation.targetPlayer.getLocation();
        final String strLocation =  "§e" + capitalize(location.getWorld().getName().toUpperCase()) + " §7 | §c" + (int)location.getX() + "§7, §a" + (int)location.getY() + "§7, §9" + (int)location.getZ() + "§7|";
        MongoUtils.getCollection().updateMany(Filters.not(Filters.exists("location")), Arrays.asList(Updates.set("location", strLocation), Updates.set("date", System.currentTimeMillis())));
    }

    /**
     * Static method to capitalize first letter of string
     * @param message - String to capitalize
     * @return capitalized string
     */
    private static String capitalize(String message){
        return message.substring(0, 1).toUpperCase() + message.substring(1);
    }
}
