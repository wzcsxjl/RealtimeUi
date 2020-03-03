package cn.itcast.createorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Random;
import java.util.UUID;

/**
 * 定义订单字段以及生成订单数据
 */
public class PaymentInfo {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;
    /**
     * 订单编号
     */
    private String orderId;
    /**
     * 商品编号
     */
    private String productId;
    /**
     * 商品价格
     */
    private long productPrice;

    /**
     * 无参构造方法
     */
    public PaymentInfo() {
    }

    public static long getSerialVersionUid() {
        return serialVersionUID;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", productPrice=" + productPrice +
                '}';
    }

    /**
     * 模拟订单数据
     */
    public String random() {
        Random r = new Random();
        // 采用UUID模拟生成订单编号（UUID是由一组32位数的十六进制数字随机构成的字符串数据）
        this.orderId = UUID.randomUUID().toString().replaceAll("-", "");
        // 商品编号是由0~9这10个数字组成，代表特定商品
        this.productId = r.nextInt(10) + "";
        this.productPrice = r.nextInt(1000);
        /*
        在数据传输过程中，需要将对象转换成Json格式的字符串
        这里采用了Fastjson数据转换工具，调用JSONObject类的toJSONString()方法将PaymentInfo订单对象转换为Json格式的字符串
         */
        JSONObject obj = new JSONObject();
        String jsonString = JSON.toJSONString(this);
        return jsonString;
    }

}
