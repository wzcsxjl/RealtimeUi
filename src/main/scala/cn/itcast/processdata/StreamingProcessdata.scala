package cn.itcast.processdata

import com.alibaba.fastjson.{JSON, JSONObject}
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import redis.clients.jedis.Jedis

/**
  * Spark Streaming处理Kafka集群中的数据
  */
object StreamingProcessdata {

  // 每件商品总销售额
  val orderTotalKey: String = "bussiness::order::total"
  // 总销售额
  val totalKey: String = "bussiness::order::all"
  // Redis数据库
  val dbIndex: Int = 0

  def main(args: Array[String]): Unit = {
    // 1.创建SparkConf对象
    val sparkConf: SparkConf = new SparkConf().setAppName("KafkaStreamingTest").setMaster("local[4]")
    // 2.创建SparkContext对象
    val sc: SparkContext = new SparkContext(sparkConf)
    sc.setLogLevel("WARN")
    // 3.构建StreamingContext对象
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(3))
    // 4.消息的偏移量会被写入到checkpoint中
    ssc.checkpoint("./spark-receiver")
    // 5.设置Kafka参数
    val kafkaParams: Map[String, String] = Map("bootstrap.servers" -> "node-1:9092,node-2:9092,node-3:9092", "group.id" -> "spark-receiver")
    // 6.指定Topic相关信息
    val topics: Set[String] = Set("itcast_order")
    // 7.通过KafkaUtils.createDirectStream利用低级API接收Kafka数据
    val kafkaDStream: InputDStream[(String, String)] = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
    // 8.获取Kafka中Topic数据并解析JSON格式数据
    // 接收到Kafka中每一条数据时，通过JSON.parseObject()方法，将Json字符串转换为JSONObject对象
    val events: DStream[JSONObject] = kafkaDStream.flatMap(line => Some(JSON.parseObject(line._2)))
    // 9.按照productID进行分组统计个数和总价格
    // x._1 商品编号；x._2.size 商品个数；x._2.reduceLeft(_ + _) 商品总价格（reduceLeft表示从左到右聚合计算）
    val orders: DStream[(String, Int, Long)] = events.map(x => (x.getString("productId"), x.getLong("productPrice"))).groupByKey().map(x => (x._1, x._2.size, x._2.reduceLeft(_ + _)))
    orders.foreachRDD(x =>
      x.foreachPartition(partition =>
        partition.foreach(x => {
          println("productId = " + x._1 + ", count = " + x._2 + ", productPrice = " + x._3)
          // 获取Redis连接资源
          val jedis: Jedis = RedisClient.pool.getResource()
          // 指定数据库
          jedis.select(dbIndex)
          /*
          每个商品销售额累加
          将orders对象中的productId和productPrice字段以Hash数据类型的结构保存在Redis数据库中，
          在Redis中表现为Map<orderTotalKey, Map<productId, productPrice>>的数据格式
          type bussiness::order::total hash
          hincrby <key> <field>  <increment>
          为哈希表key中的域field的值加上增量increment
          */
          jedis.hincrBy(orderTotalKey, x._1, x._3)
          /*
          总销售额累加
          type bussiness::order::all string
          incrby / decrby  <key>  <步长>
          将key中储存的数字值增减，自定义步长
           */
          jedis.incrBy(totalKey, x._3)
          RedisClient.pool.returnResource(jedis)
        })
      )
    )
    ssc.start()
    ssc.awaitTermination()
  }

}
