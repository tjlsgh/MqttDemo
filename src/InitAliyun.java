import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;



//初始化
 class InitAliyun {


     static String productKey = "********";
     static String deviceName = "**********";

     static MqttClient mqttClient = null;

     static MqttClient startInit(){
         //计算Mqtt建联参数

         String deviceSecret = "****************";

         MqttSign sign = new MqttSign();
         sign.calculate(productKey, deviceName, deviceSecret);//计算三元组信息

         System.out.println("username: " + sign.getUsername());
         System.out.println("password: " + sign.getPassword());
         System.out.println("clientid: " + sign.getClientid());

         //使用Paho连接阿里云物联网平台
         String port = "1883";

         String broker = "tcp://" + productKey + ".iot-as-mqtt.cn-shanghai.aliyuncs.com" + ":" + port;
         MemoryPersistence persistence = new MemoryPersistence();
         try {
             //Paho Mqtt 客户端
                 mqttClient = new MqttClient(broker, sign.getClientid(), persistence);

             //Paho Mqtt 连接参数
             MqttConnectOptions connOpts = new MqttConnectOptions();
             connOpts.setCleanSession(true);
             connOpts.setKeepAliveInterval(180);
             connOpts.setUserName(sign.getUsername());
             connOpts.setPassword(sign.getPassword().toCharArray());
             mqttClient.connect(connOpts);
             System.out.println("broker: " + broker + " Connected");
         } catch (
                 MqttException e) {
             System.out.println("reason " + e.getReasonCode());
             System.out.println("msg " + e.getMessage());
             System.out.println("loc " + e.getLocalizedMessage());
             System.out.println("cause " + e.getCause());
             System.out.println("excep " + e);
             e.printStackTrace();
         }
         return mqttClient;
     }

     //重连
     static void reconnect(){
         if(mqttClient !=null && !(mqttClient.isConnected())){
             try{
                 mqttClient.reconnect();
             }catch (MqttException e){
                 e.printStackTrace();
                 System.out.println("重连成功");
             }
         }
     }

     //是否连接
     static boolean isConnected(){
         if(mqttClient != null){
             return mqttClient.isConnected();
         }
         return false;
     }
     //断开连接
     static void disconnect(){
         if(mqttClient !=null && mqttClient.isConnected()){
             try {
                 mqttClient.disconnect();
             }catch (MqttException e){
                 e.printStackTrace();
             }
         }
     }


}


