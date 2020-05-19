import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.ref.WeakReference;


public class DeviceStateActivity extends AppCompatActivity {

    Button back_button;
    Button refresh_button;
    static TextView messageArrivedTextView;

    static class DSHandler extends Handler {
        private WeakReference<Activity> mActivity;

        DSHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null) {
            if (msg.what == 1){
                    String string = (String) msg.obj;
                    System.out.println("界面更新成功");
                    messageArrivedTextView.setText(string+"\n");
            }else {
                System.out.println("无数据");
            }
//                if(msg !=null) {
//                    String string = (String) msg.obj;
//                    messageArrived_textView.setText(string+"\n");
//                }else{
//                    System.out.println("无数据");
//                }
        }
        }
    }

    private Handler dsHandler = new DeviceStateActivity.DSHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_state);

        refresh_button = findViewById(R.id.Refresh);
        back_button = findViewById(R.id.Back);
        messageArrivedTextView = findViewById(R.id.messageArrivedTextView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    sub_pub sub = new sub_pub();
                    if (InitAliyun.isConnected()) {
                        System.out.println("连接成功");
                        Looper.prepare();
                        Toast.makeText(DeviceStateActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
//                    sub.subscribe();
//                    InitAliyun.mqttClient.setCallback(new PushCallback());
                }catch (Exception e){
                    System.out.println("wrong");
                }

            }
        }).start();



        //刷新 持续订阅
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sub_pub sub = new sub_pub();
                        sub.subscribe();
                        PushCallback pushCallback = new PushCallback(){
                            @Override
                            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                                super.messageArrived(topic, mqttMessage);
                                Message msg1 = new Message();
                                msg1.what = 1;
                                msg1.obj = mqttMessage.toString();
                                dsHandler.sendMessage(msg1);
                                System.out.println("发送更新界面");
                            }
                        };
                        InitAliyun.mqttClient.setCallback(pushCallback);
//                        Message msg1 = new Message();
////                        Message msg2 = new Message();
//                        msg1.what = 1;
//                        msg1.obj = pushCallback.getMessage();
//                        msg2.obj = pushCallback.topic;
//                        dsHandler.sendMessage(pushCallback.msg1);

////                        sub.subscribeListener();
//                        Message msg = new Message();
//                        msg.obj = sub.msgArrived;
//                        dsHandler.sendMessage(msg);
//                        dsHandler.postDelayed( new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//
//
//                            }
//                        }), 10 * 1000);
                    }
                }).start();
            }
        });

        //返回
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceStateActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
    private static void showToast(String msg) {
        messageArrivedTextView.setText(msg + "\n" );
    }

}
