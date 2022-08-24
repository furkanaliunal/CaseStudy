package furkan.studio.casestudy.velocity.Teleports.TeleportTypes;

import com.mongodb.client.MongoCollection;
import com.velocitypowered.api.proxy.Player;
import furkan.studio.casestudy.velocity.Teleports.BaseTeleportation;
import furkan.studio.casestudy.velocity.Utils.MongoUtils;
import furkan.studio.casestudy.velocity.Utils.RedisUtils;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AcceptedTeleportation extends BaseTeleportation {

    public AcceptedTeleportation(Player player, Player target) {
        super(player, target);
    }

    public void run(){
        writeToRedis();
        writeToMongo();
        final String sourceServer = getPlayer().getCurrentServer().get().getServer().getServerInfo().getName();
        final String targetServer = getTargetServer().getServerInfo().getName();
        if (sourceServer.equals(targetServer)) return;
        getPlayer().createConnectionRequest(getTargetServer()).fireAndForget();
    }

    public void writeToRedis() {
        final Jedis jedis = RedisUtils.getJedis();
        jedis.sadd(RedisUtils.acceptedTeleportsKey, getTargetPlayer().getUniqueId().toString() + ", " + getPlayer().getUniqueId().toString());
        jedis.close();
    }
    public void writeToMongo(){
        final Map<String, Object> map = new HashMap<>();
        map.put("UUID", getPlayer().getUniqueId().toString());
        map.put("targetUUID", getTargetPlayer().getUniqueId().toString());
        map.put("targetName", getTargetPlayer().getUsername());
        String serverName = getTargetServer().getServerInfo().getName();
        serverName = serverName.substring(0, 1).toUpperCase(Locale.ROOT) + serverName.substring(1);
        map.put("server", serverName);
        MongoUtils.getCollection().insertOne(new Document(map));

    }
}
