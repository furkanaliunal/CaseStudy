package io.github.furkanaliunal.casestudy.velocity.Teleports.TeleportTypes;

import com.mongodb.client.MongoCollection;
import com.velocitypowered.api.proxy.Player;
import io.github.furkanaliunal.casestudy.velocity.Teleports.BaseTeleportation;
import io.github.furkanaliunal.casestudy.velocity.Utils.MongoUtils;
import io.github.furkanaliunal.casestudy.velocity.Utils.RedisUtils;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by OnwexryS
 * This class is used store accepted teleportation request.
 * @see BaseTeleportation - BaseTeleportation class is used to store any teleportation request.
 */
public class AcceptedTeleportation extends BaseTeleportation {
    /**
     * Constructor for AcceptedTeleportation.
     * @see BaseTeleportation - superclass
     * @param player - player who accepted the teleportation request
     * @param target - player who sent the teleportation request
     */
    public AcceptedTeleportation(Player player, Player target) {
        super(player, target);
    }

    /**
     * This method is used to process the teleportation request.
     * @see BaseTeleportation - superclass
     * @see AcceptedTeleportation#writeToRedis() - writes to redis
     * @see AcceptedTeleportation#writeToMongo() - writes to mongo
     */
    public void run(){
        writeToRedis();
        writeToMongo();
        final String sourceServer = getPlayer().getCurrentServer().get().getServer().getServerInfo().getName();
        final String targetServer = getTargetServer().getServerInfo().getName();
        if (sourceServer.equals(targetServer)) return;
        getPlayer().createConnectionRequest(getTargetServer()).fireAndForget();
    }

    /**
     * This method is used to write the teleportation request to redis.
     * Gets a resource from redis and writes the request to it.
     * @see RedisUtils#getJedis() - Gets a resource from redis.
     * @see Jedis#sadd(String, String...) - Writes the request to redis as an element of Set.
     * @see Jedis#close() - Closes the resource.
     */
    public void writeToRedis() {
        final Jedis jedis = RedisUtils.getJedis();
        jedis.sadd(RedisUtils.acceptedTeleportsKey, getTargetPlayer().getUniqueId().toString() + ", " + getPlayer().getUniqueId().toString());
        jedis.close();
    }

    /**
     * This method is used to write the teleportation data to mongo.
     * @see MongoCollection#insertOne(Object) - inserts the data to mongo.
     */
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
