# TuyaSmartSocketSdk

It is mainly aimed at Android developers in Tuya cloud products. The project aims to provide a local area network connection between an Android phone and a hardware device, and send dpCode in the local area network for device control communication.

Tuya devices connection and control process is as follows:

![https://cdn.nlark.com/yuque/__puml/1de1d74497bdbb14a4debde42f3f3f34.svg](https://cdn.nlark.com/yuque/__puml/1de1d74497bdbb14a4debde42f3f3f34.svg)

## Preparation

You can refer to the demo[localcontroldemo](localcontroldemo)

Root directory `build.gradle` add tuya maven url

	allprojects {
	    repositories {
	        maven {
	            url "https://maven-other.tuya.com/repository/maven-releases/"
	        }
	        google()
	        jcenter()
	    }
	}
module level `build.gradle` add dependency: 

	implementation 'com.tuya.smart:socket-sdk:0.1.1'

## 一、Initialization

The initialization interface is called when the application or Activity starts.

    /**
     * init and register listener
     * @param context context
     * @param socketListener 
     */
    void init(Context context, TuyaSocketListener socketListener);
   

The methods in TuyaSocketListener are as follows:
	
    /**
     * Called when connect disconnected
     * @param deviceId 
     * @param errorCode {@link com.tuya.sdk.lancontrol.api.ErrorCode}
     */
    void onDisconnected(String deviceId, int errorCode);

    /**
     * Called when connect success
     *
     * @param deviceId 
     */
    void onConnected(String deviceId);

    /**
     * Device control succeeded, and hardware reports the control success commands
     * 
     * @param deviceId
     * @param commands the control success commands
     */
    void onCommandsReceived(String deviceId, Map<String, Object> commands);


example：

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


## 二、Add device information

Add device information to the SDK, and the SDK will automatically establish a LAN connection with the device.

This parameter can be obtained through the cloud-docking interface `/ v1.0 / devices / schema`.

    /**
     * 
     * @param deviceInfoJsonString 接口/v1.0/devices/schema的返回数据
     */
    void addDeviceInfo(String deviceInfoJsonString);

example：

	TuyaSocketManager.getInstance().addDeviceInfo(json);


## 三、device control

    /**
     * send control commands
     * 
     * @param deviceId deviceId
     * @param commands control commands
     * @param resultCallback
     */
	TuyaSocketManager.getInstance().publishCommands(String deviceId, Map<String, Object> commands, ResultCallback resultCallback);


example：

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

## 四、关闭连接

When your app exits, you can close all LAN connections:

    // Close all devices connect
    TuyaSocketManager.getInstance().destroy(this);

## Additional information

`dpCodeValue` composition rules:

To learn more about dp points, you can refer to [Update device information](https://tuyainc.github.io/tuyasmart_home_ios_sdk_doc/en/resource/Device.html#update-device-information).
`dpCode` is a function point that describes the device, that is, which function controls a device supports. The schema returned in the `/v1.0/devices/schema` interface will return the feature points supported by the device. The typical function points are described below.
`dpCodeDict` is performed in the format `dpCode` : `dpValue` . `dpCode`  can be obtained from the code field in the schema. `dpValue`  needs to be sent according to the format supported by the `dp point`.
The following will take Demo given in the interface document as an example to explain how dpCode  is structured.

1. Switch

	"type": "bool"

	e.g.：`{"switch_led" : true}` 或者 `{"switch_led" : true}`
		
		{
		    "mode": "rw",
		    "code": "switch_led",
		    "name": "开关 ",
		    "property": {
		        "type": "bool"
		    },
		    "iconname": "icon- dp_power2",
		    "id": 20,
		    "type": "obj",
		    "desc": ""
		}
		
2. Mode option (signal choose)
	
	"type": "enum"
	
	e.g. `{"work_mode" : "white"}` `{"work_mode" : "colour"} `
		
		{
		    "mode": "rw",
		    "code": "work_mode",
		    "name": "模式",
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
	
	e.g. `{"bright_value": 400}`
	
	注意：这里的数值有最大值、最小值、步进值限制。
		
		{
		    "mode": "rw",
		    "code": "bright_value",
		    "name": "亮度值",
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
	
	e.g. `{"colour_data":"000100010001"} `
	
	以上方式只是其中一种传递方式，具体的deValue值需要针对具体情况进行分析
	
		{
		    "mode": "rw",
		    "code": "colour_data",
		    "name": "彩光",
		    "property": {
		        "type": "string",
		        "maxlen": 255
		    },
		    "iconname": "icon- dp_light",
		    "id": 24,
		    "type": "obj",
		    "desc": ""
		}
