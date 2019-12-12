package com.tuya.localcontroldemo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.sdk.lancontrol.api.ITuyaSocketManager;
import com.tuya.sdk.lancontrol.api.ResultCallback;
import com.tuya.sdk.lancontrol.api.TuyaSocketListener;
import com.tuya.sdk.lancontrol.manager.TuyaSocketManager;
import com.tuya.smart.android.common.utils.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ITuyaSocketManager tuyaSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        L.setLogSwitcher(true);
        tuyaSocketManager = TuyaSocketManager.getInstance();
        tuyaSocketManager.init(this, new TuyaSocketListener() {
            @Override
            public void onDisconnected(String deviceId, int errorCode) {
                LogUtils.i("onDisconnected : deviceId = " + deviceId + "\n errorCode = " + errorCode);
            }

            @Override
            public void onConnected(String deviceId) {
                LogUtils.i("onConnected : deviceId = " + deviceId);
            }

            @Override
            public void onCommandsReceived(String deviceId, Map<String, Object> commands) {
                LogUtils.i("onCommandsReceived : deviceId = " + deviceId + "\n commands = " + commands);
            }
        });
    }

    /**
     * Send commands to control your device
     */
    public void sendControlCommands(View view) {
        HashMap<String, Object> commands = new HashMap<>();
        boolean value = new Random().nextBoolean();
        commands.put("switch_led", value);
        tuyaSocketManager.publishCommands("05606704bcddc23b90fb", commands, new ResultCallback() {
            @Override
            public void onError(int errorCode, String errorMessage) {
                LogUtils.i("publish onError " + errorMessage);
            }

            @Override
            public void onSuccess() {
                LogUtils.i("publish onSuccess ");
            }
        });
    }

    /**
     * Send data point to control your device
     */
    public void sendDps(View view) {
        HashMap<String, Object> commands = new HashMap<>();
        boolean value = new Random().nextBoolean();
        commands.put("20", value);
        tuyaSocketManager.publishDps("05606704bcddc23b90fb", commands, new ResultCallback() {
            @Override
            public void onError(int errorCode, String errorMessage) {
                LogUtils.i("publish onError " + errorMessage);
            }

            @Override
            public void onSuccess() {
                LogUtils.i("publish onSuccess ");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close all devices connect
        tuyaSocketManager.destroy(this);
    }

    public void close(View view) {
        // Close all devices connect
        tuyaSocketManager.destroy(this);
    }

    public void setDeviceInfo(View view) {
        String json = readJson();
        // Add devices info, there will auto connect
        tuyaSocketManager.addDeviceInfo(json);
    }

    /**
     * mock some data
     */
    private String readJson() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("deviceInfo.json")));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine())!= null){
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
