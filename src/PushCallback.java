
import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PushCallback implements MqttCallback {

    Message msg = new Message();

    Activity DeviceStateActivity ;
    @Override
    public void connectionLost(Throwable throwable) {//断开连接可以重连
        System.out.println("连接断开，可以做重连");
        InitAliyun.reconnect();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        System.out.println("接收消息主题:"+topic);
        System.out.println("接收消息Qos:"+mqttMessage.getQos());
        System.out.println("接收消息内容:"+new String(mqttMessage.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("deliveryComplete---------" );
    }


}
