# Tuya Smart Socket SDK

[English](README.md) | [中文版](README_cn.md)

## Introduction

Tuya Smart Socket SDK is used for Android development of Tuya's cloud products. The project provides a local area network (LAN) connection between an Android phone and a device, and sends dpCode in the LAN for device control communication.

The following figure shows device connection and control process:

![https://cdn.nlark.com/yuque/__puml/1de1d74497bdbb14a4debde42f3f3f34.svg](https://cdn.nlark.com/yuque/__puml/1de1d74497bdbb14a4debde42f3f3f34.svg)

## Preparation

You can refer to the demo [localcontroldemo](localcontroldemo).

Add tuya maven url to `build.gradle` in root directory

	allprojects {
	    repositories {
	        maven {
	            url "https://maven-other.tuya.com/repository/maven-releases/"
	        }
	        google()
	        jcenter()
	    }
	}

Add dependency to `build.gradle` in module.

	implementation 'com.tuya.smart:socket-sdk:0.1.1'

## 1. Initialization

The initialization interface is called when the application or Activity starts.

    /**
     * Initialize and register listener
     * @param context context
     * @param socketListener 
     */
    void init(Context context, TuyaSocketListener socketListener);
   

The methods in TuyaSocketListener is as follows:
	
    /**
     * Called on disconnection
     * @param deviceId 
     * @param errorCode {@link com.tuya.sdk.lancontrol.api.ErrorCode}
     */
    void onDisconnected(String deviceId, int errorCode);

    /**
     * Called when connection succeeds
     *
     * @param deviceId 
     */
    void onConnected(String deviceId);

    /**
     * Device control succeeded, and hardware reports the control success commands
     * 
     * @param deviceId
     * @param commands The control success commands
     */
    void onCommandsReceived(String deviceId, Map<String, Object> commands);


Example:

    TuyaSocketManager.getInstance().init(this, new TuyaSocketListener() {
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


## 2. Add device information

Add device information to the SDK, and the SDK will automatically establish a LAN connection with the device.

This parameter can be obtained through the cloud-connection interface `/ v1.0 / devices / schema`.

    /**
     * 
     * @param deviceInfoJsonString Data returned from interface /v1.0/devices/schema
     */
    void addDeviceInfo(String deviceInfoJsonString);

Example:

	TuyaSocketManager.getInstance().addDeviceInfo(json);


## 3. Device control

    /**
     * Send control commands
     * 
     * @param deviceId Device ID
     * @param commands Control commands
     * @param resultCallback
     */
	TuyaSocketManager.getInstance().publishCommands(String deviceId, Map<String, Object> commands, ResultCallback resultCallback);


Example:

    HashMap<String, Object> commands = new HashMap<>();
    boolean value = new Random().nextBoolean();
    commands.put("switch_1", value);
    TuyaSocketManager.getInstance().publishCommands("6ce35593d91e3d9aa2tlo4", commands, new ResultCallback() {
        @Override
        public void onError(int errorCode, String errorMessage) {
            LogUtils.i("publish onError " + errorMessage);
        }

        @Override
        public void onSuccess() {
            LogUtils.i("publish onSuccess ");
        }
    });

## 4. Disable connection

When your app exits, you can disable all LAN connections:

    // Close all device connections
    TuyaSocketManager.getInstance().destroy(this);

## Additional information

`dpCodeValue` composition rules:

To learn more about data point (DP), you can refer to [Update device information](https://developer.tuya.com/en/docs/app-development/android-app-sdk/device-management/devicemanage?id=Ka6ki8r2rfiuu).
`dpCode` is a DP that describes which functions can be controlled for a device. The schema returned in the `/v1.0/devices/schema` interface will return the DPs supported by a device. The typical DPs are described below.
`dpCodeDict` is performed in the format `dpCode: dpValue`. `dpCode` can be obtained from the code field in the schema. `dpValue` needs to be sent according to the format supported by the `dp point`.
The following will take the demo in the interface document as an example to explain how dpCode is structured.

1. Switch

	"type": "bool"

	For example, `{"switch_led" : true}` or `{"switch_led" : true}`
		
		{
		    "mode": "rw",
		    "code": "switch_led",
		    "name": "Switch ",
		    "property": {
		        "type": "bool"
		    },
		    "iconname": "icon- dp_power2",
		    "id": 20,
		    "type": "obj",
		    "desc": ""
		}
		
2. Mode option (signal choice)
	
	"type": "enum"
	
	For example, `{"work_mode" : "white"}` `{"work_mode" : "colour"} `
		
		{
		    "mode": "rw",
		    "code": "work_mode",
		    "name": "Mode",
		    "property": {
		        "range": ["white", "colour", "scene", "music"],
		        "type": "enum"
		    },
		    "iconname": "i con-dp_list",
		    "id": 21,
		    "type": "obj",
		    "desc": ""
		}
3. Brightness value (send number)
	
	"type": "value"
	
	For example, `{"bright_value": 400}`
	
	Note: The value has maximum, minimum, and step limit.
		
		{
		    "mode": "rw",
		    "code": "bright_value",
		    "name": "Brightness",
		    "property": {
		        "min": 10,
		        "max": 1000,
		        "scale": 0,
		        "step": 1,
		        "type": "value"
		    },
		    "iconname ": "icon-dp_sun",
		    "id": 22,
		    "type": "obj",
		    "desc": ""
		}
4. Color data (send string)

	"type": "string"
	
	For example, `{"colour_data":"000100010001"} `
	
	The above method is only one of the transfer methods, and the specific deValue varies by application scenarios 
	
		{
		    "mode": "rw",
		    "code": "colour_data",
		    "name": "Color",
		    "property": {
		        "type": "string",
		        "maxlen": 255
		    },
		    "iconname": "icon- dp_light",
		    "id": 24,
		    "type": "obj",
		    "desc": ""
		}
