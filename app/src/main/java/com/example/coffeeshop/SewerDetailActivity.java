package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.MessageQueue;
import android.renderscript.Sampler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
    private ImageView videoContainer, imageViewMqttStatusColor, imageViewSewerDetailBackButton;
    private ImageButton imageViewButtonUp, imageViewButtonDown, imageViewButtonStop;
    private TextView textViewSewerMqttStatus, textViewName, textViewStatus, textViewMyMqttStatus, textViewSocketStatus, textViewControlMode;
    private EditText editTextNumberControlDistance;
    private com.google.android.material.slider.Slider sliderRangeControl;
    private Sewer sewer;
    private MqttClientHelper mqttClientHelper;
    private Queue<byte[]> frameQueue = new ArrayDeque<>();
    private final Bitmap bitmapTemp = Bitmap.createBitmap(768, 432, Bitmap.Config.ARGB_4444);;
    private final String OFFLINE_COLOR = "#828282";
    private final String ONLINE_COLOR = "#40B85C";
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

    private void initialComponent() {
        fetchSewer();
        editTextNumberControlDistance = findViewById(R.id.editTextNumberControlDistance);
        sliderRangeControl = findViewById(R.id.sliderRangeControl);
        videoContainer = findViewById(R.id.imageView6);
        imageViewMqttStatusColor = findViewById(R.id.imageViewSewerMqttStatusColor);
        imageViewButtonUp = findViewById(R.id.imageViewButtonUp);
        imageViewButtonDown = findViewById(R.id.imageViewButtonDown);
        imageViewButtonStop = findViewById(R.id.imageViewButtonStop);
        imageViewSewerDetailBackButton = findViewById(R.id.imageViewSewerDetailBackButton);
        textViewControlMode = findViewById(R.id.textViewControlMode);
        textViewSewerMqttStatus = findViewById(R.id.textViewSewerMqttStatus);
        textViewName = findViewById(R.id.textViewName);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewMyMqttStatus = findViewById(R.id.textViewMyMqttStatus);
        textViewSocketStatus = findViewById(R.id.textViewSocketStatus);
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
                        int controlDistance = Integer.parseInt(editTextNumberControlDistance.getText().toString());
                        mqttClientHelper.getMqttClient().publish(sewer.getChannel(),
                                new MqttMessage(mqttClientHelper.getControlStatement(controlDistance, mqttClientHelper.DOWN)));
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
                        mqttClientHelper.getMqttClient().publish(sewer.getChannel(),
                                new MqttMessage(mqttClientHelper.getControlStatement(0, mqttClientHelper.UP)));
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
                        mqttClientHelper.getMqttClient().publish(sewer.getChannel(),
                                new MqttMessage(mqttClientHelper.getControlStatement(0, mqttClientHelper.STOP)));
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
//                        final Bitmap image;
//                        image = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        videoContainer.setImageBitmap(image);
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
                @Override
                public void run() {
                    Log.e("onDisconnect", "onDisconnect");
                    textViewSocketStatus.setText("Disconnected");
                }
            });
        }
    };
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onConnect", "onConnect");
                    textViewSocketStatus.setText("Connected");
                }
            });
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onConnectError", "onConnectError");
                    textViewSocketStatus.setText("Connection Error");
                }
            });
        }
    };
    private Emitter.Listener onConnectTimeout = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onConnectTimeout", "onConnectTimeout");
                    textViewSocketStatus.setText("Connection Timeout");
                }
            });
        }
    };
    private Emitter.Listener onReconnecting = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onReconnecting", "onReconnecting");
                    textViewSocketStatus.setText("Reconnecting");
                }
            });
        }
    };
    private Emitter.Listener onReconnectFailed = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onReconnectFailed", "onReconnectFailed");
                    textViewSocketStatus.setText("Reconnect Failed");
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

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.e("MQTT", "connection established!");
        textViewMyMqttStatus.setText("Connected");
        listenToMqttStatus();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e("MQTT", "connection lost");
        if(textViewSewerMqttStatus.getText().toString().equals("Connected"))
        textViewMyMqttStatus.setText("Reconnecting");
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
                setSewerInfo(distance, controlMode, sewerMqttStatus);
                break;
            case "connection":
                String sewerMqttConnection = new String(message.getPayload());
                setSewerState(sewerMqttConnection);
        }
    }

    private void setSewerState(String sewerMqttConnection) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setSewerInfo(String distance, String controlMode, String sewerMqttStatus) {
        textViewSewerMqttStatus.setText(sewerMqttStatus);
        imageViewMqttStatusColor.getDrawable().setTint(Color.parseColor(sewerMqttStatus.equals("Connected") ? ONLINE_COLOR : OFFLINE_COLOR));
        sliderRangeControl.setValue(Integer.valueOf(distance));
        textViewControlMode.setText(controlMode);
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