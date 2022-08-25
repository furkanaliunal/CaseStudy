package io.github.furkanaliunal.casestudy.velocity.Utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisFactory;
import redis.clients.jedis.JedisPool;

import java.time.Duration;

/**
 * Created by Onwexrys
 * This is library class to initialize Redis connection.
 * @see JedisPool
 * @see JedisFactory
 * @see Jedis
 * @see GenericObjectPoolConfig
 */
public class RedisUtils {
    /**
     * Instance of Redis connection pool
     * This is used to get Jedis resource from pool.
     * @see JedisPool
     */
    public static JedisPool jedisPool = null;
    /**
     * Key to get accepted teleports data from Redis
     */
    public static String acceptedTeleportsKey = "casestudy#acceptedteleports";
    /**
     * Default ip for redis
     */
    private static String defaultIP = "localhost";
    /**
     * Default port for Redis
     */
    private static Integer defaultPort = 6379;

    /**
     * Reads the Redis connection data from config and uses it to initialize Redis connection.
     * Alternative way to initialize Redis {@link RedisUtils#initialize(Logger, String, Integer, String)} method.
     * @see Configuration
     * @see Logger
     * @param logger Logger instance
     * @param configuration Configuration instance
     * @return true if connection is initialized successfully, false otherwise
     */
    public static boolean initializeFromConfig(final Logger logger, final Configuration configuration){
        RedisUtils.acceptedTeleportsKey = configuration.getOrDefault("Redis.AcceptedTeleportsKey", "casestudy#accepted_teleports");
        return RedisUtils.initialize(logger, configuration.getOrDefault("Redis.Hostname",null), configuration.getOrDefault("Redis.Port", null), configuration.getOrDefault("Redis.Password", null));
    }

    /**
     * Gets the default pool configuration and initializes Redis connection.
     * {@link RedisUtils#initialize(Logger, GenericObjectPoolConfig, String, Integer, String)}
     * @param logger Logger instance
     * @param ip ip of Redis server
     * @param port port of Redis server
     * @param password password of Redis server
     * @return true if connection is established, false otherwise
     */
    public static boolean initialize(final Logger logger, final String ip, final Integer port, final String password){
        return RedisUtils.initialize(logger, buildPoolConfig(), ip, port, password);
    }
    /**
     * Initializes Redis connection.
     * @param logger Logger instance
     * @param configuration {@link GenericObjectPoolConfig} instance
     * @param ip ip of Redis server
     * @param port port of Redis server
     * @param password password of Redis server
     * @return true if connection is established, false otherwise
     */
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

    /**
     * Gets the default pool configuration.
     * @see GenericObjectPoolConfig
     * @return {@link GenericObjectPoolConfig} instance
     */
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
    /**
     * Gets the Jedis resource from pool.
     * Alternative way to get Jedis resource {@link RedisUtils#getJedis()} method.
     * @return {@link Jedis} instance
     */
    public static Jedis getRedis(){
        return RedisUtils.getJedis();
    }
    /**
     * Gets the Jedis resource from pool.
     * @return {@link Jedis} instance
     */
    public static Jedis getJedis(){
        return RedisUtils.jedisPool.getResource();
    }
    /**
     * Closes the Jedis resource.
     */
    public static void close(){
        RedisUtils.jedisPool.close();
    }

}