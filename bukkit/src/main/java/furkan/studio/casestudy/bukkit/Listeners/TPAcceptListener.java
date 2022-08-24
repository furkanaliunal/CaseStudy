package furkan.studio.casestudy.bukkit.Listeners;

import furkan.studio.casestudy.bukkit.CaseStudy;
import furkan.studio.casestudy.bukkit.Teleportation;
import furkan.studio.casestudy.bukkit.Utils.RedisUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.UUID;

public class TPAcceptListener {
    public TPAcceptListener() {
        runTPAReceiver();
    }

    private void runTPAReceiver() {
        new BukkitRunnable(){
            @Override
            public void run() {
                Jedis read = RedisUtils.getJedis();
                Jedis write = RedisUtils.getJedis();
                Set<String> teleportTargetUUIDs =  read.hkeys(RedisUtils.acceptedTeleportsKey);
                teleportTargetUUIDs.forEach(strUUID -> {
                    final UUID targetUUID = UUID.fromString(strUUID);
                    final Player targetPlayer = Bukkit.getPlayer(targetUUID);
                    if (targetPlayer == null ||(!targetPlayer.isOnline())) return;

                    final String playerUUID = read.hget(RedisUtils.acceptedTeleportsKey, strUUID);
                    final Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));
                    if (player == null || (!player.isOnline())) return;

                    write.hdel(RedisUtils.acceptedTeleportsKey, strUUID);
                    Teleportation.processTeleportation(new Teleportation(player, targetPlayer));
                });
                read.close();
                write.close();
            }
        }.runTaskTimer(CaseStudy.getInstance(), 0, 1);
    }
}
