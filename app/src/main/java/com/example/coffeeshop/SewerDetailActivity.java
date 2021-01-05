package com.example.coffeeshop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.Queue;

public class SewerDetailActivity extends AppCompatActivity implements MqttCallbackExtended{
    private SharedPreferences sharedPreferences;
    private Socket mySocket;
    int i = 0;
    private ImageView videoContainer, imageViewMyMqttStatusColor, imageViewSewerDetailBackButton, imageViewSewerMqttStatusColor, imageViewSocketStatusColor;
    private ImageButton imageViewButtonUp, imageViewButtonDown, imageViewButtonStop, imageButtonIncreaseDistance, imageButtonDecreaseDistance;
    private TextView textViewSewerDistance, textViewSewerMqttStatus, textViewName, textViewSewerStatus, textViewMyMqttStatus, textViewSocketStatus, textViewControlMode;
    private EditText editTextNumberControlDistance;
    private ImageView buttonSettingConnection;
    private com.google.android.material.slider.Slider sliderRangeControl;
    private Sewer sewer;
    private MqttClientHelper m2qttClientHelper;
    private Queue<byte[]> frameQueue = new ArrayDeque<>();
    private final Bitmap bitmapTemp = Bitmap.createBitmap(768, 432, Bitmap.Config.ARGB_4444);;
    private final String OFFLINE_COLOR = "#828282";
    private final String ONLINE_COLOR = "#40B85C";
    private final String SOCKET_ONLINE_COLOR = "#F6E550";
    private long start = 0;
    private int flagToStartWorker = 0;
    private int TIME_OUT = 60000;// 6 seconds
    private Dialog dialog;
    private final String MQTT_ADDRESS = "104.155.233.176";
    private final String MQTT_PORT = "1883";
    private final String SOCKET_ADDRESS = "104.155.233.176";
    private final String SOCKET_PORT = "3000";
    private MqttClientHelper mqttClientHelper;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewer_detail);
        sewer = (Sewer) getIntent().getSerializableExtra("Sewer");
        initialComponent();
        setUpMqtt(sharedPreferences.getString("MQTT_ADDRESS",""), sharedPreferences.getString("MQTT_PORT",""));
        setUpSocket(sharedPreferences.getString("SOCKET_ADDRESS",""), sharedPreferences.getString("SOCKET_PORT",""));
        playVideo();
    }

    private void initialComponent() {
        sharedPreferences = this.getSharedPreferences("connection setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setDefaultConnection();
        editTextNumberControlDistance = findViewById(R.id.editTextNumberControlDistance);
        sliderRangeControl = findViewById(R.id.sliderRangeControl);
        videoContainer = findViewById(R.id.imageView6);
//        imageViewMyMqttStatusColor = findViewById(R.id.imageViewMyMqttStatusColor);
        imageViewButtonUp = findViewById(R.id.imageViewButtonUp);
        imageViewButtonDown = findViewById(R.id.imageViewButtonDown);
        imageViewButtonStop = findViewById(R.id.imageViewButtonStop);
        imageViewSewerDetailBackButton = findViewById(R.id.imageViewSewerDetailBackButton);
        imageViewSewerMqttStatusColor = findViewById(R.id.imageViewSewerMqttStatusColor);
        imageButtonIncreaseDistance = findViewById(R.id.imageButtonIncreaseDistance);
        imageButtonDecreaseDistance = findViewById(R.id.imageButtonDecreaseDistance);
        buttonSettingConnection = findViewById(R.id.buttonSettingConnection);
//        imageViewSocketStatusColor = findViewById(R.id.imageViewSocketStatusColor);
        textViewControlMode = findViewById(R.id.textViewControlMode);
        textViewSewerMqttStatus = findViewById(R.id.textViewSewerMqttStatus);
        textViewName = findViewById(R.id.textViewName);
        textViewName.setText(sewer.getName());
        textViewSewerStatus = findViewById(R.id.textViewSewerStatus);
        textViewMyMqttStatus = findViewById(R.id.textViewMyMqttStatus);
        textViewSocketStatus = findViewById(R.id.textViewSocketStatus);
        textViewSewerDistance = findViewById(R.id.textViewSewerDistance);
        editTextNumberControlDistance.setFilters(new InputFilter[]{ new EditTextFilter("0", "20")});
        editTextNumberControlDistance.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    editTextNumberControlDistance.clearFocus();
                }
                return false;
            }
        });
        imageButtonIncreaseDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 0;
                String input = editTextNumberControlDistance.getText().toString();
                if(!input.equals("")){
                    num = Integer.parseInt(input);
                    if(num < 20){
                        num = num +1;
                    }
                    else{
                        num = 0;
                    }
                }
                editTextNumberControlDistance.setText(""+num);
            }
        });
        imageButtonDecreaseDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 0;
                String input = editTextNumberControlDistance.getText().toString();
                if(!input.equals("")){
                    num = Integer.parseInt(input);
                    if(num != 0){
                        num = num -1;
                    }
                    else{
                        num = 20;
                    }
                }
                editTextNumberControlDistance.setText(""+num);
            }
        });
        imageViewSewerDetailBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SewerDetailActivity.this.finish();
            }
        });
        imageViewButtonDown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mqttClientHelper.getMqttClient().isConnected()){
                    try {

                        mqttClientHelper.getMqttClient().publish("controller",
                                new MqttMessage(mqttClientHelper.getControlStatement(getcontrolDistance(), mqttClientHelper.DOWN)));
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        imageViewButtonUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mqttClientHelper.getMqttClient().isConnected()){
                    try {
                        mqttClientHelper.getMqttClient().publish("controller",
                                new MqttMessage(mqttClientHelper.getControlStatement(getcontrolDistance(), mqttClientHelper.UP)));
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        imageViewButtonStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mqttClientHelper.getMqttClient().isConnected()){
                    try {
                        mqttClientHelper.getMqttClient().publish("controller",
                                new MqttMessage(mqttClientHelper.getControlStatement(getcontrolDistance(), mqttClientHelper.STOP)));
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttonSettingConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingConnection();
            }
        });
    }

    private void setUpMqtt(String mqttAddress, String mqttPort) {
        final String clientId = MqttClient.generateClientId();
        final String mqttServerUri = "tcp://"+mqttAddress+":"+mqttPort;
        mqttClientHelper = new MqttClientHelper(this.getApplicationContext(), mqttServerUri, "hoangtho");
        mqttClientHelper.getMqttClient().setCallback(this);
        mqttClientHelper.doConnect();
    }

    private void setUpSocket(String socketAddress, String socketPort){
        try{
            IO.Options opts = new IO.Options();
            opts.transports = new String[] { WebSocket.NAME };
            this.mySocket = IO.socket("http://"+socketAddress+":"+socketPort+"/", opts);
            mySocket.on("imageSend", onNewMessage);
            mySocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mySocket.on(Socket.EVENT_CONNECT, onConnect);
            mySocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mySocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
            mySocket.on(Socket.EVENT_RECONNECTING, onReconnecting);
            mySocket.on(Socket.EVENT_RECONNECT_FAILED, onReconnectFailed);
        }
        catch (URISyntaxException e){
            Log.e("Socket","URI Syntax Exception: "+e.getMessage());
        }
    }

    private void playVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(!frameQueue.isEmpty())
                    {
                        final long start = System.currentTimeMillis();
                        // retrieves and removes the head of queue
                        byte[] data = frameQueue.poll();
                        final Bitmap image;
                        BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
                        bitmapFactoryOptions.inBitmap = bitmapTemp;
                        bitmapFactoryOptions.inSampleSize = 1;
                        image = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapFactoryOptions);
                        Log.e("DecodeByteArray", "Time to decode: "+(System.currentTimeMillis() - start));
                        videoContainer.post(new Runnable() {
                            @Override
                            public void run() {
                                videoContainer.setImageBitmap(image);
                                Log.e("Streaming", "time to changed: "+ (System.currentTimeMillis() - start) + " | size: "+ frameQueue.size());
                            }
                        });
                        /*try {
                            Thread.sleep(170);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    }

                }

            }
        }).start();
    }


    private void listenToMqttStatus() {
        try {
            mqttClientHelper.getMqttClient().subscribe("info", 0);
            mqttClientHelper.getMqttClient().subscribe("connection", 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // draw a sequence of images to make it becomes a video

    private void checkSewerConnectionTimeout() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                try {
                    while(true){
                        if(System.currentTimeMillis() - start >= 60000){
                            imageViewSewerMqttStatusColor.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageViewSewerMqttStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
                                    Log.e("sewerConnection", "changed the color of sewer connection");
                                }
                            });
                            textViewSewerMqttStatus.post(new Runnable() {
                                @Override
                                public void run() {
                                    textViewSewerMqttStatus.setText("Disconnected");
                                    Log.e("sewerConnection", "changed the text of sewer connection");
                                }
                            });
                        }
                        Thread.sleep(TIME_OUT);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void setDefaultConnection(){
        String mqtt_address = sharedPreferences.getString("MQTT_ADDRESS","");
        String mqtt_port = sharedPreferences.getString("MQTT_PORT","");
        String socket_address = sharedPreferences.getString("SOCKET_ADDRESS","");
        String socket_port = sharedPreferences.getString("SOCKET_PORT","");
        if(mqtt_address.equals("") || mqtt_port.equals("") || socket_address.equals("") || socket_port.equals("")){
            editor.putString("MQTT_ADDRESS", MQTT_ADDRESS);
            editor.putString("MQTT_PORT", MQTT_PORT);
            editor.putString("SOCKET_ADDRESS", SOCKET_ADDRESS);
            editor.putString("SOCKET_PORT", SOCKET_PORT);
            if(editor.commit()){
                Log.e("save", "saved");
            }
        }
    }
    public void settingConnection(){
        this.dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_setting_connection);
        dialog.setTitle("Setting");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText editTextMqttAddress,editTextMqttPort, editTextSocketAddress, editTextSocketPort;
        Button buttonSaveSettingConnection;
        buttonSaveSettingConnection = dialog.findViewById(R.id.buttonSaveSettingConnection);
        editTextMqttAddress = dialog.findViewById(R.id.editTextMqttAddress);
        editTextMqttPort = dialog.findViewById(R.id.editTextMqttPort);
        editTextSocketAddress = dialog.findViewById(R.id.editTextSocketAddress);
        editTextSocketPort = dialog.findViewById(R.id.editTextSocketPort);
        editTextMqttAddress.setText(sharedPreferences.getString("MQTT_ADDRESS",""));
        editTextMqttPort.setText(sharedPreferences.getString("MQTT_PORT",""));
        editTextSocketAddress.setText(sharedPreferences.getString("SOCKET_ADDRESS",""));
        editTextSocketPort.setText(sharedPreferences.getString("SOCKET_PORT",""));
        dialog.show();
        buttonSaveSettingConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextMqttAddress.getText().toString().equals("")
                        || editTextMqttPort.getText().toString().equals("")
                        || editTextSocketAddress.getText().toString().equals("")
                        || editTextSocketPort.getText().toString().equals(""))
                {
                    dialog.dismiss();
                    setUpMqtt(editTextMqttAddress.getText().toString(), editTextMqttPort.getText().toString());
                    setUpSocket(editTextSocketAddress.getText().toString(), editTextSocketPort.getText().toString());
                }
            }
        });

    }

    private int getcontrolDistance() {
        int controlDistance = Integer.parseInt(editTextNumberControlDistance.getText().toString());
        return controlDistance*10;
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("SocketIO", "connected: "+mySocket.connected());
                    byte[] data = Base64.decode((byte[]) args[0], Base64.DEFAULT | Base64.NO_WRAP);
                    long start = System.currentTimeMillis();
                    try{
                        frameQueue.add(data);
                    }
                    catch(Exception e){
                        Log.e("Queue", "Add queue faled: "+e.getMessage());
                    }
                    Log.e("Time to add", "time slapped: "+ (System.currentTimeMillis() - start) );
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    Log.e("onDisconnect", "onDisconnect");
                    textViewSocketStatus.setText("Disconnected");
//                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
                }
            });
        }
    };
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    Log.e("onConnect", "onConnect");
                    textViewSocketStatus.setText("Connected");
//                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(SOCKET_ONLINE_COLOR));
                }
            });
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    Log.e("onConnectError", "onConnectError");
                    textViewSocketStatus.setText("Connection Error");
//                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
                }
            });
        }
    };
    private Emitter.Listener onConnectTimeout = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    Log.e("onConnectTimeout", "onConnectTimeout");
                    textViewSocketStatus.setText("Connection Timeout");
//                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
                }
            });
        }
    };
    private Emitter.Listener onReconnecting = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    Log.e("onReconnecting", "onReconnecting");
                    textViewSocketStatus.setText("Reconnecting");
//                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
                }
            });
        }
    };
    private Emitter.Listener onReconnectFailed = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    Log.e("onReconnectFailed", "onReconnectFailed");
                    textViewSocketStatus.setText("Reconnect Failed");
//                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySocket.disconnect();
        mySocket.off("imageSend", onNewMessage);
        mySocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mySocket.off(Socket.EVENT_CONNECT, onConnect);
        mySocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mySocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
        mySocket.off(Socket.EVENT_RECONNECTING, onReconnecting);
        mySocket.off(Socket.EVENT_RECONNECT_FAILED, onReconnectFailed);
        mqttClientHelper.doDisconnect();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.e("MQTT", "connection established!");
//        imageViewMyMqttStatusColor.getDrawable().setTint(Color.parseColor(ONLINE_COLOR));
        textViewMyMqttStatus.setText("Connected");
        listenToMqttStatus();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void connectionLost(Throwable cause) {
        Log.e("MQTT", "connection lost");
        if(textViewMyMqttStatus.getText().toString().equals("Connected") || textViewMyMqttStatus.getText().toString().equals("Mqtt Status")){
//            imageViewMyMqttStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
            textViewMyMqttStatus.setText("Reconnecting");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        switch (topic){
            case "info":
                String info = new String(message.getPayload());
                info = info.substring(1, info.length()-1);
                String[] infoExtract = info.split(",");
                String distance = infoExtract[0];
                String controlMode = infoExtract[1];
                String sewerMqttStatus = infoExtract[2];
                setSewerInfo(Integer.valueOf(distance), controlMode, sewerMqttStatus);
                break;
            case "connection":
                start = System.currentTimeMillis();
                if(flagToStartWorker == 0){
                    checkSewerConnectionTimeout();
                    flagToStartWorker ++;
                }
                String sewerMqttConnection = new String(message.getPayload());
                setSewerState(sewerMqttConnection);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setSewerState(String sewerMqttConnection) {
        if(textViewSewerMqttStatus.getText().toString().equals("Disconnected") || flagToStartWorker == 0){
            Log.e("fuck", "fucked changed color");
            imageViewSewerMqttStatusColor.getDrawable().setTint(Color.parseColor(ONLINE_COLOR));
        }
        textViewSewerMqttStatus.setText(sewerMqttConnection);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setSewerInfo(int distance, String controlMode, String sewerMqttStatus) {
        textViewSewerStatus.setText(sewerMqttStatus);
        if(distance > 19){
            distance = 19;
        }
        sliderRangeControl.setValue(distance);
        textViewControlMode.setText(controlMode);
        textViewSewerDistance.setText(String.valueOf(distance));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.e("MQTT", "an message has been sent");
    }
}