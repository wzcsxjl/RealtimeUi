package cn.itcast.processdata

import java.util.Properties

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.JedisPool

/**
  * 读取配置文件中Redis参数
  */
object RedisClient {

  val prop: Properties = new Properties()
  // lazy修饰变量是为了延迟初始化
  lazy val pool: JedisPool = new JedisPool(new GenericObjectPoolConfig(), redisHost, redisPort.toInt, redisTimeout.toInt)
  // 加载配置文件
  prop.load(this.getClass.getClassLoader.getResourceAsStream("redis.properties"))
  val redisHost: String = prop.getProperty("jedis.host")
  val redisPort: String = prop.getProperty("jedis.port")
  val redisTimeout: String = prop.getProperty("jedis.max.wait.millis")
  lazy val hook: Thread = new Thread {
    override def run(): Unit = {
      println("Execute hook thread: " + this)
      pool.destroy()
    }
  }

}
