
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

 class sub_pub extends InitAliyun {

    static String responseBody = "";
    private static final String TAG = MainActivity.class.getSimpleName();
    private MqttClient mqttClient = InitAliyun.startInit();

    String msgArrived = "";

    void pub_closeFan(){
        //Paho Mqtt 消息发布
        try {

            String topic = "/sys/" + productKey + "/" + deviceName + "/thing/event/property/post";
            String content = "{\"id\":%s,\"params\":{\"Fan\": %s,},\"method\":\"thing.event.property.post\"}";
            String payload = String.format(content,
                    String.valueOf(System.currentTimeMillis()),
                    0);
            responseBody = payload;
            if(mqttClient.isConnected()) {
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(0);
                mqttClient.publish(topic, message);
                System.out.println("publish: " + content);
            }
        }catch (MqttException e){
            e.printStackTrace();
            responseBody = e.getMessage();
            Log.e(TAG, "postDeviceProperties error " + e.getMessage(), e);
        }

    }
    void pub_closeMotor(){

        //Paho Mqtt 消息发布
        String topic = "/sys/" + productKey + "/" + deviceName + "/thing/event/property/post";
        String content = "{\"id\":%s,\"params\":{\"Motor\": %s,},\"method\":\"thing.event.property.post\"}";
        String payload = String.format(content,
                String.valueOf(System.currentTimeMillis()),
                0);
        responseBody = payload;
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(0);
        try {
            mqttClient.publish(topic, message);
        }catch (MqttException e){
            e.printStackTrace();
            responseBody = e.getMessage();
            Log.e(TAG, "postDeviceProperties error " + e.getMessage(), e);
        }
        System.out.println("publish: " + content);
    }



    //订阅
    void subscribe(){
        //Paho Mqtt 消息发布
        String topicReply = "/sys/a104fcohulX/Smart_Mask/thing/event/property/post";
//        String topicReply = "/sys/a104fcohulX/Smart_Mask/thing/service/property/set";
//        String topicReply = "/sys/a104fcohulX/Smart_Mask/thing/event/property/post";
//        String topicReply = "/sys/" + productKey + "/" + deviceName + "/thing/event/property/post_reply";
        try{
            if( mqttClient != null && mqttClient.isConnected() ) { //未连接 为什么
                mqttClient.subscribe(topicReply,1);
                System.out.println("subscribe: " + topicReply);
            }else{
                System.out.println("订阅失败");
            }
        }catch (MqttException e){
            e.printStackTrace();
        }

    }
    //mqtt监听器
    void subscribeListener(){
        //Paho Mqtt 消息发布
        String topicReply = "/a104fcohulX/Smart_Mask/user/get";
//        String topicReply = "/sys/" + productKey + "/" + deviceName + "/thing/event/property/post_reply";
        try{
            if( mqttClient != null && mqttClient.isConnected() ) { //未连接 为什么
                System.out.println("subscribe: " + topicReply);
                mqttClient.subscribe(topicReply,1, new MqttPostPropertyMessageListener(){
                    @Override
                    public void messageArrived(String var1, MqttMessage var2) throws Exception {
                        super.messageArrived(var1, var2);
                        System.out.println("内容为"+var2.toString());
                        msgArrived = var2.toString();
                    }
                });
            }else{
                System.out.println("订阅失败");
            }
        }catch (MqttException e){
            e.printStackTrace();
        }

    }


    static String getResponseBody(){
        return responseBody;
    }
}
