package furkan.studio.casestudy.velocity.Utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisFactory;
import redis.clients.jedis.JedisPool;

import java.time.Duration;

public class RedisUtils {
    public static JedisPool jedisPool = null;
    public static String teleportsKey = "casestudy#pendingteleports";
    public static String onlinePlayersKey = "casestudy#onlineplayers";
    private static Integer defaultPort = 6379;
    private static String defaultIP = "localhost";
    public static boolean initalizeFromConfig(final Logger logger, final Configuration configuration){
        RedisUtils.teleportsKey = configuration.getOrDefault("Redis.PendingTeleportsKey", "casestudy#pendingteleports");
        RedisUtils.onlinePlayersKey = configuration.getOrDefault("Redis.OnlinePlayersKey", "casestudy#onlineplayers");
        return RedisUtils.initialize(logger, configuration.getOrDefault("Redis.Hostname",null), configuration.getOrDefault("Redis.Port", null), configuration.getOrDefault("Redis.Password", null));
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
            logger.warn("§6[§eERROR§6] §f §4REDIS ERROR: §c"+e.getMessage());
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

}