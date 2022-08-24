package furkan.studio.casestudy.bukkit;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import furkan.studio.casestudy.bukkit.Utils.MongoUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;


public record Teleportation(Player player, Player targetPlayer) {
    public static void processTeleportation(final Teleportation teleportation) {
        if (!teleportation.player.isOnline()) return;
        teleportation.player.teleportAsync(teleportation.targetPlayer.getLocation());
        logToMongo(teleportation);
    }

    public static void logToMongo(final Teleportation teleportation) {
        final Location location = teleportation.targetPlayer.getLocation();
        final String strLocation =  "§e" + capitalize(location.getWorld().getName().toUpperCase()) + " §7 | §c" + (int)location.getX() + "§7, §a" + (int)location.getY() + "§7, §9" + (int)location.getZ() + "§7|";
        MongoUtils.getCollection().updateMany(Filters.not(Filters.exists("location")), Arrays.asList(Updates.set("location", strLocation), Updates.set("date", System.currentTimeMillis())));


    }
    private static String capitalize(String message){
        return message.substring(0, 1).toUpperCase() + message.substring(1);
    }
}
