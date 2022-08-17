package furkan.studio.casestudy;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import furkan.studio.casestudy.Utils.RedisUtils;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class PlayerListSchedule {
    private final ScheduledTask scheduledTask;
    private final ProxyServer plugin;

    public PlayerListSchedule(){
        plugin = CaseStudy.server;
        final String onlinePlayersKey = RedisUtils.onlinePlayersKey;
        scheduledTask = plugin.getScheduler().buildTask(CaseStudy.instance, () -> {
            final Collection<String> players = new ArrayList<>();
            plugin.getAllPlayers().forEach(player -> players.add(player.getUniqueId().toString()));
            if (players.size() <= 0) return;
            final Jedis jedis = RedisUtils.getJedis();
            jedis.del(onlinePlayersKey);
            jedis.sadd(onlinePlayersKey, players.toArray(new String[players.size()]));
            jedis.close();
        }).repeat(1L, TimeUnit.SECONDS).schedule();
    }

    public void stopScheduler(){
        scheduledTask.cancel();
    }
}
