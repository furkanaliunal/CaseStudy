package furkan.studio.casestudy.bukkit.Utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisFactory;
import redis.clients.jedis.JedisPool;

import java.time.Duration;

public class RedisUtils {
    public static JedisPool jedisPool = null;
    public static String acceptedTeleportsKey = "casestudy#acceptedteleports";
    private static Integer defaultPort = 6379;
    private static String defaultIP = "localhost";
    public static boolean initializeFromConfig(final Logger logger, final FileConfiguration config){
        RedisUtils.acceptedTeleportsKey = config.getString("Redis.AcceptedTeleportsKey", "casestudy#acceptedteleports");
        return RedisUtils.initialize(logger, config.getString("Redis.Hostname",null), config.getInt("Redis.Port", 6379), config.getString("Redis.Password", null));
    }
    public static boolean initialize(final Logger logger, final String ip, final Integer port, final String password){
        return RedisUtils.initialize(logger, buildPoolConfig(), ip, port, password);
    }
    public static boolean initialize(final Logger logger, final GenericObjectPoolConfig configuration, final String ip, final Integer port, final String password){
        RedisUtils.jedisPool = new JedisPool(configuration, ip == null ? defaultIP : ip , port == null ? defaultPort : port, 10000);
        boolean hasPassword = !(password == null || password.equals("") || password.isEmpty());
        if (hasPassword) ((JedisFactory)jedisPool.getFactory()).setPassword(password);

        try {
            RedisUtils.jedisPool.preparePool();
        } catch (Exception e) {
            Audience.audience(Bukkit.getConsoleSender()).sendMessage(Component.text("§6[§eERROR§6] §f §4REDIS ERROR: §c"+e.getMessage()));
            return false;
        }
        return true;
    }

    private static GenericObjectPoolConfig buildPoolConfig() {
        final GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(256);
        poolConfig.setMaxIdle(256);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTime(Duration.ofSeconds(60)); //Duration.ofSeconds(60).toMillis()
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));//Duration.ofSeconds(30).toMillis()
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
    public static Jedis getRedis(){
        return RedisUtils.getJedis();
    }
    public static Jedis getJedis(){
        return RedisUtils.jedisPool.getResource();
    }
    public static void deleteKey(final String key){
        final Jedis jedis = RedisUtils.getJedis();
        jedis.del(key);
        jedis.close();
    }
    public static void close(){
        if (jedisPool != null){
            jedisPool.close();
        }
    }

}

