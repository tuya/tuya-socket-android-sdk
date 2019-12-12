
--------------------[English documentation](README_en.md)----------------------

----------------------------------------------------

TuyaSmartSocketKit 主要针对涂鸦云云对接的产品中Android端开发者。该项目旨在提供Android手机（以下简称手机）与硬件设备（以下简称设备）的局域网连接，并在局域网中发送dpCode进行设备控制通信。

涂鸦设备连接与控制流程如下：

![https://cdn.nlark.com/yuque/__puml/1de1d74497bdbb14a4debde42f3f3f34.svg](https://cdn.nlark.com/yuque/__puml/1de1d74497bdbb14a4debde42f3f3f34.svg)

## 接入准备

接入可以参考demo [localcontroldemo](localcontroldemo)

根目录的`build.gradle`中添加 tuya maven url

	allprojects {
	    repositories {
	        maven {
	            url "https://maven-other.tuya.com/repository/maven-releases/"
	        }
	        google()
	        jcenter()
	    }
	}
module中的`build.gradle`中添加依赖：

	implementation 'com.tuya.smart:socket-sdk:0.1.0'

## 一、局域网初始化


    /**
     * 初始化监听
     * @param context context
     * @param socketListener socket监听
     */
    void init(Context context, TuyaSocketListener socketListener);
    
应用或Activity启动时调用初始化接口。

其中，TuyaSocketListener中的方法如下：

	
    /**
     * 连接断开
     * @param deviceId 设备id
     * @param errorCode 错误码 {@link com.tuya.sdk.lancontrol.api.ErrorCode}
     */
    void onDisconnected(String deviceId, int errorCode);

    /**
     * 设备连接
     * @param deviceId 设备id
     */
    void onConnected(String deviceId);

    /**
     * 设备控制成功
     * @param deviceId 设备id
     * @param commands 修改成功的设备功能点
     */
    void onCommandsReceived(String deviceId, Map<String, Object> commands);


调用示例：

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


## 二、添加设备信息

添加设备信息到SDK中，SDK内部将自动建立与设备的局域网连接。

该参数可以通过云云对接的接口 `/v1.0/devices/schema` 获取到。

    /**
     * 
     * @param deviceInfoJsonString 接口/v1.0/devices/schema的返回数据
     */
    void addDeviceInfo(String deviceInfoJsonString);

调用示例：

	TuyaSocketManager.getInstance().addDeviceInfo(json);


## 三、设备控制

    /**
     * 发送控制命令
     * @param deviceId 设备id
     * @param commands 控制命令
     * @param resultCallback 发送回调
     */
	TuyaSocketManager.getInstance().publishCommands(String deviceId, Map<String, Object> commands, ResultCallback resultCallback);


调用示例：

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

当您的应用退出时，您可以关闭所有局域网连接：

    // Close all devices connect
    TuyaSocketManager.getInstance().destroy(this);

## 附加说明

dpCodeValue组成规则：

要了解dp点的详细信息，可以参考 [设备功能点文档](https://tuyainc.github.io/tuyasmart_home_ios_sdk_doc/zh-hans/resource/Device.html#%E8%AE%BE%E5%A4%87%E5%8A%9F%E8%83%BD%E7%82%B9)

dpCode是描述设备的功能点，即一个设备支持哪些功能控制。在 " /v1.0/devices/schema" 接口中返回的 schema 中会返回的就是设备支持的功能点。以下分别对典型的功能点进行说明。

dpCodeDict均以 `dpCode : dpValue` 的格式进行。

dpCode 可以从 schema 中 code 字段获得。dpValue 需要根据dp点支持的格式发送。

以下将以接口文档中给出的Demo为例，说明dpCode如何构成。

1. 开关

	"type": "bool"

	例如：`{"switch_led" : true}` 或者 `{"switch_led" : true}`
		
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
		
2. 模式选择 (单选)
	
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
3. 亮度值(发送数值)
	
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
4. 彩光(发送字符串)

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
