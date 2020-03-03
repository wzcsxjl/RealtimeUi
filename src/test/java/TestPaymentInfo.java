import cn.itcast.createorder.PaymentInfo;

public class TestPaymentInfo {

    public static void main(String[] args) {
        PaymentInfo p = new PaymentInfo();
        String message = p.random();
        System.out.println(message);
    }

}
