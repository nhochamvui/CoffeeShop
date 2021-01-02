package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.MessageQueue;
import android.renderscript.Sampler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.Calendar;

public class SewerDetailActivity extends AppCompatActivity implements MqttCallbackExtended{

    private Socket mySocket;
    int i = 0;
    private ImageView videoContainer, imageViewMyMqttStatusColor, imageViewSewerDetailBackButton, imageViewSewerMqttStatusColor, imageViewSocketStatusColor;
    private ImageButton imageViewButtonUp, imageViewButtonDown, imageViewButtonStop, imageButtonIncreaseDistance, imageButtonDecreaseDistance;
    private TextView textViewSewerDistance, textViewSewerMqttStatus, textViewName, textViewSewerStatus, textViewMyMqttStatus, textViewSocketStatus, textViewControlMode;
    private EditText editTextNumberControlDistance;
    private com.google.android.material.slider.Slider sliderRangeControl;
    private Sewer sewer;
    private MqttClientHelper mqttClientHelper;
    private Queue<byte[]> frameQueue = new ArrayDeque<>();
    private final Bitmap bitmapTemp = Bitmap.createBitmap(768, 432, Bitmap.Config.ARGB_4444);;
    private final String OFFLINE_COLOR = "#828282";
    private final String ONLINE_COLOR = "#40B85C";
    private final String SOCKET_ONLINE_COLOR = "#F6E550";
    private long start = 0;
    private int flagToStartWorker = 0;
    private int TIME_OUT = 60000;// 6 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewer_detail);
        sewer = (Sewer) getIntent().getSerializableExtra("Sewer");
        setUpMqtt();
        initialComponent();
        if(setUpSocket()){
            mySocket.on("imageSend", onNewMessage);
            mySocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mySocket.on(Socket.EVENT_CONNECT, onConnect);
            mySocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mySocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
            mySocket.on(Socket.EVENT_RECONNECTING, onReconnecting);
            mySocket.on(Socket.EVENT_RECONNECT_FAILED, onReconnectFailed);
            boolean isConnected = mySocket.connect().connected();
        }
        else{
            Log.e("Socket", "connection failed");
        }
        playVideo();
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
    private void initialComponent() {
        fetchSewer();
        editTextNumberControlDistance = findViewById(R.id.editTextNumberControlDistance);
        sliderRangeControl = findViewById(R.id.sliderRangeControl);
        videoContainer = findViewById(R.id.imageView6);
        imageViewMyMqttStatusColor = findViewById(R.id.imageViewMyMqttStatusColor);
        imageViewButtonUp = findViewById(R.id.imageViewButtonUp);
        imageViewButtonDown = findViewById(R.id.imageViewButtonDown);
        imageViewButtonStop = findViewById(R.id.imageViewButtonStop);
        imageViewSewerDetailBackButton = findViewById(R.id.imageViewSewerDetailBackButton);
        imageViewSewerMqttStatusColor = findViewById(R.id.imageViewSewerMqttStatusColor);
        imageButtonIncreaseDistance = findViewById(R.id.imageButtonIncreaseDistance);
        imageButtonDecreaseDistance = findViewById(R.id.imageButtonDecreaseDistance);
        imageViewSocketStatusColor = findViewById(R.id.imageViewSocketStatusColor);
        textViewControlMode = findViewById(R.id.textViewControlMode);
        textViewSewerMqttStatus = findViewById(R.id.textViewSewerMqttStatus);
        textViewName = findViewById(R.id.textViewName);
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
    }

    private int getcontrolDistance() {
        int controlDistance = Integer.parseInt(editTextNumberControlDistance.getText().toString());
        return controlDistance*10;
    }

    private void setUpMqtt() {
        final String clientId = MqttClient.generateClientId();
        final String mqttServerUri = "tcp://104.155.233.176:1883";
        mqttClientHelper = new MqttClientHelper(this.getApplicationContext(), mqttServerUri, "hoangtho");
        mqttClientHelper.getMqttClient().setCallback(this);
        mqttClientHelper.doConnect();
    }

    private boolean setUpSocket(){
        try{
            IO.Options opts = new IO.Options();
            opts.transports = new String[] { WebSocket.NAME };
            this.mySocket = IO.socket("http://104.155.233.176:3000/", opts);
            return true;
        }
        catch (URISyntaxException e){
            Log.e("Socket","URI Syntax Exception: "+e.getMessage());
            return false;
        }
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
                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
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
                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(SOCKET_ONLINE_COLOR));
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
                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
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
                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
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
                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
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
                    imageViewSocketStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
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
        imageViewMyMqttStatusColor.getDrawable().setTint(Color.parseColor(ONLINE_COLOR));
        textViewMyMqttStatus.setText("Connected");
        listenToMqttStatus();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void connectionLost(Throwable cause) {
        Log.e("MQTT", "connection lost");
        if(textViewMyMqttStatus.getText().toString().equals("Connected") || textViewMyMqttStatus.getText().toString().equals("Mqtt Status")){
            imageViewMyMqttStatusColor.getDrawable().setTint(Color.parseColor(OFFLINE_COLOR));
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

    public void fetchSewer() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("sewer");
        ValueEventListener valueEventListenerFetchUser = mDatabase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("ON CHANGING SEWER","detected changed!");
                for (DataSnapshot sewerObject : dataSnapshot.getChildren()) {
                    Log.e("Fetch Sewer","Sewer has been fetched");
                    sewer = sewerObject.getValue(Sewer.class);
//                    textViewStatus.setText(sewer.getStatus());
                    textViewName.setText(sewer.getName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}