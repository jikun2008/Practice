# Practice  
Practice  是一个使用stomp协议的webSocket连接的Android库

底层websocket是需要okhttp依赖 

内部是实现了心跳 当服务器返回Connected的时候:

例如:

```
CONNECTED
version:1.1
heart-beat:3000,6000
```

会根据heart-beat的值向服务发送心跳,同时也会检查服务器定时向client发送心跳 如果超过3次未发送 自动断开连接
    


## 如何使用  

### 首先你需要引入 okhttp库
```
implementation("com.squareup.okhttp3:okhttp:4.0.1")

```


然后在引入Practice库
#### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
####  Step 2. Add the dependency
  
```
  
    	dependencies {
	        implementation 'com.github.jikun2008:Practice:1.1.4'
	}
  

```

### example:

```


    //注册
    protected void onCreate(Bundle savedInstanceState) {

   	 practice = new Practice.Builder().version(VersionEnum.VERSION_1_1)
                .build();
         practice.register(this);
    }

 

   
    //Connect
        Request request = new Request.Builder()
                .url("ws://192.168.137.1:8090/driver")
                .build();
        practice.startConnect(request);

    


    //当服务器连接成功的时候会调用这个方法 @OnStompConnect修饰的方法
    @OnStompConnect
    public void onConnect() {
        stringBuilder = new StringBuilder();
        stringBuilder.append("连接成功\n");
        tvTextView.setText(stringBuilder.toString());
	

    //发Subscribe消息到服务端
          //发送广播端点连接
          practice.sendStompMessage(
                  StompMessageHelper.createSubscribeStompMessage(
                          "/all/driverMessage/broadcast",
                          null
                  )
          );

          practice.sendStompMessage(
                  StompMessageHelper.createSubscribeStompMessage(
                          "/all/driverMessage/im/123456",
                          null
                  )
          );


          practice.sendStompMessage(
                  StompMessageHelper.createSubscribeStompMessage(
                          "/user/driverMessage/order",
                          null
                  )
          );
        
    }

  //连接断开的时候会调用这个方法 @OnStompDisConnect修饰的方法
    @OnStompDisConnect
    public void onDisConnect(Integer code, String info) {
        stringBuilder.append("连接失败:code=" + code + "__info=" + info + "\n");
        tvTextView.setText(stringBuilder.toString());
    }


   //例如当收到destination=/user/driverMessage/order的消息时候会回调这个方法
    @OnStompSubscribe("/user/driverMessage/order")
    public void onReviceOrderMessage(StompMessage message) {
        //接受服务端的消息
        stringBuilder.append(message.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());

    }

    @OnStompSubscribe("/all/driverMessage/broadcast")
    public void onReviceBroadcastMessage(StompMessage message) {
        //接受服务端的消息
        stringBuilder.append(message.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());

    }

    @OnStompSubscribe("/all/driverMessage/im/{id}")
    public void onReviceBroadcastIMiDMessage(StompMessage message, String id) {
        //接受服务端的消息
        Log.e("测试代码", "测试代码onReviceBroadcastIMiDMessage=" + id);
        stringBuilder.append(message.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
	//当不需要使用的取消注册
        practice.unregister(this);
	//断开连接
        practice.disConnect();
    }

    public void testDisConnect(View view) {
        //断开连接
        practice.disConnect();

    }
        



```




