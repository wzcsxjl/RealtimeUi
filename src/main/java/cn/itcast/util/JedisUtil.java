package cn.itcast.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;

/**
 * Java版本Jedis工具类
 * Redis Java API操作的工具类
 * 主要提供Java操作Redis的对象Jedis，类似数据库连接池
 */
public class JedisUtil {

    private JedisUtil() {
    }

    private static JedisPool jedisPool;

    static {
        Properties prop = new Properties();
        try {
            prop.load(JedisUtil.class.getClassLoader().getResourceAsStream("redis.properties"));
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            // Jedis连接池中最大的连接个数
            poolConfig.setMaxTotal(Integer.valueOf(prop.getProperty("jedis.max.total")));
            // Jedis连接池中最大的空闲连接个数
            poolConfig.setMaxIdle(Integer.valueOf(prop.getProperty("jedis.max.idle")));
            // Jedis连接池中最小的空闲连接个数
            poolConfig.setMinIdle(Integer.valueOf(prop.getProperty("jedis.min.idle")));
            // Jedis连接池最大的等待连接时间ms值
            poolConfig.setMaxWaitMillis(Long.valueOf(prop.getProperty("jedis.max.wait.millis")));
            // Jedis的服务器主机名
            String host = prop.getProperty("jedis.host");
            int port = Integer.valueOf(prop.getProperty("jedis.port"));
            jedisPool = new JedisPool(poolConfig, host, port, 10000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis对象
     * @return Jedis
     */
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 资源释放
     * @param jedis
     */
    public static void returnJedis(Jedis jedis) {
        jedis.close();
    }

}
