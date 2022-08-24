package furkan.studio.casestudy.bukkit.Listeners;

import furkan.studio.casestudy.bukkit.CaseStudy;
import furkan.studio.casestudy.bukkit.Teleportation;
import furkan.studio.casestudy.bukkit.Utils.RedisUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Onwexrys
 * This class is used to handle the accepted teleport requests through redis
 * Reads the data from redis and sends the player to the destination
 */
public class TPAcceptListener {
    /**
     * This method is used to handle the accepted teleport requests through redis
     * Reads the data from redis and sends the player to the destination
     */
    public TPAcceptListener() {
        runTPAReceiver();
    }

    /**
     * @see Teleportation#processTeleportation(Teleportation)
     * @see Jedis
     * @see BukkitRunnable#runTaskTimer(Plugin, long, long)
     */
    private void runTPAReceiver() {
        new BukkitRunnable(){
            @Override
            public void run() {
                Jedis read = RedisUtils.getJedis();
                Jedis write = RedisUtils.getJedis();

                Set<String> teleportLines = read.smembers(RedisUtils.acceptedTeleportsKey);
                teleportLines.forEach(line -> {
                    final String[] split = line.split(", ");
                    final UUID targetUUID = UUID.fromString(split[0]);
                    final Player targetPlayer = Bukkit.getPlayer(targetUUID);
                    if (targetPlayer == null ||(!targetPlayer.isOnline())) return;

                    final String playerStrUUID = split[1];
                    final Player player = Bukkit.getPlayer(UUID.fromString(playerStrUUID));
                    if (player == null || (!player.isOnline())) return;
                    write.srem(RedisUtils.acceptedTeleportsKey, line);

                    Teleportation.processTeleportation(new Teleportation(player, targetPlayer));
                });
                read.close();
                write.close();
            }
        }.runTaskTimer(CaseStudy.getInstance(), 0, 1);
    }
}
