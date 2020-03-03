import cn.itcast.service.GetDataService;
import cn.itcast.util.JedisUtil;
import redis.clients.jedis.Jedis;

public class TestJedis {

    public static void main(String[] args) {

        Jedis jedis = JedisUtil.getJedis();

       /* Map<String, String> testData = jedis.hgetAll("bussiness::order::total");

        String [] produceId = new String [10];
        String [] producetSumPrice = new String [10];

        int i=0;
        for(Map.Entry<String,String> entry : testData.entrySet()){
            produceId[i]=entry.getKey();
            producetSumPrice[i] =entry.getValue();
            i++;
        }

        UiBean ub = new UiBean();
        ub.setProducetSumPrice(producetSumPrice);
        ub.setProduceId(produceId);
        System.out.println(ub);*/

        GetDataService getDataService = new GetDataService();
        String data = getDataService.getData();
        System.out.println(data);
    }

}
