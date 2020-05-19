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

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity {

    Button activate_button;
    Button quit_button;
    Button stop_fan_button;
    Button stop_motor_button;
    Button device_state_button;
    static TextView msgTextView;

    final static int CONNECT_SUCCESS = 1000;//连接成功
    final static int CONNECT_ERROR = 1001;//未连接
    final static int POST_DEVICE_PROPERTIES_SUCCESS = 1002;//发送数据成功
    final static int POST_DEVICE_PROPERTIES_ERROR = 1003;//发送数据失败

    private static class MHandler extends Handler {
        private WeakReference<Activity> mActivity;

        MHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case CONNECT_SUCCESS:
                        showToast("连接成功");
                        break;
                    case CONNECT_ERROR:
                        showToast("未连接");
                        break;
                    case POST_DEVICE_PROPERTIES_SUCCESS:
                        showToast("发送数据成功");
                        break;
                    case POST_DEVICE_PROPERTIES_ERROR:
                        showToast("发送数据失败");
                        break;
                }

            }
        }
    }

    private final Handler mHandler = new MainActivity.MHandler(this);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activate_button = findViewById(R.id.activate_button);
        quit_button = findViewById(R.id.quit_button);
        stop_fan_button = findViewById(R.id.stop_fan);
        stop_motor_button = findViewById(R.id.stop_motor);
        msgTextView = findViewById(R.id.msgTextView);
        device_state_button = findViewById(R.id.device_state);

        final MHandler mHandler = new MHandler(MainActivity.this);

        //点击连接
        activate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InitAliyun.startInit();
                        if (InitAliyun.isConnected()) {
                            Message msg = new Message();
                            msg.what = CONNECT_SUCCESS;
                            mHandler.sendMessage(msg);
                            System.out.println("连接成功");
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        InitAliyun.mqttClient.setCallback(new PushCallback());
                    }
                }).start();
            }
        });

        //跳转到 state
        device_state_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DeviceStateActivity.class);
                startActivity(intent);
            }
        });

        //断开连接
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (InitAliyun.isConnected()) {
                            InitAliyun.disconnect();
                            Message msg = new Message();
                            msg.what = CONNECT_ERROR;
                            mHandler.sendMessage(msg);
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "断开连接成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "未连接", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
            }
        });


        //点击停止吹风
        stop_fan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (InitAliyun.isConnected()){
                            sub_pub stopFan = new sub_pub();
                            stopFan.pub_closeFan();
                            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_ERROR);
                        }else{
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "未连接", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                }).start();
            }
        });

        //点击停止震动
        stop_motor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (InitAliyun.mqttClient != null && InitAliyun.isConnected()) {
                            sub_pub stopMotor = new sub_pub();
                            stopMotor.pub_closeMotor();
                            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_SUCCESS);
                        }else{
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "未连接", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
            }
        });
    }




    private static void showToast(String msg) {
        msgTextView.setText(" "+"\n"+" ");
        msgTextView.setText(msg + "\n" + sub_pub.getResponseBody());
    }

    @Override
    protected void onStop() {
        super.onStop();
        InitAliyun.disconnect();
    }
}
