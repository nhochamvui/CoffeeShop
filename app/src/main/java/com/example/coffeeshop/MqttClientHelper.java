package com.example.coffeeshop;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class MqttClientHelper {
    private MqttAndroidClient client;
    private IMqttToken iMqttToken;
    private MqttConnectOptions options;
    public final static int DOWN = 0;
    public final static int UP = 1;
    public final static int STOP = 2;
    public MqttClientHelper(Context context, String url, String id){
        this.client = new MqttAndroidClient(context, url, id);
        this.options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
    }
    public void doConnect(){
        try{
            this.client.connect(this.options);
        }
        catch (Exception e){
            Log.e("MQTT Connect","Failed: "+e.getMessage());
        }
    }
    public void doDisconnect(){
        this.client.close();
    }
    public IMqttToken getiMqttToken(){
        return this.iMqttToken;
    }
    public MqttAndroidClient getMqttClient(){
        return this.client;
    }
    public byte[] getControlStatement(int distance, int command) {
        return ("{ " + distance + "," + command + "}").getBytes();
    }

}
