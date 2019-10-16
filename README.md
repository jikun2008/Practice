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

会根据服务时间发送心跳,服务器也会向客户端发送心跳
    


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
	        implementation 'com.github.jikun2008:Practice:1.0.0'
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
     Request request = new Request.Builder().url("ws://10.28.6.69:8091/noSocketJs").build();
     practice.startConnect(request);

    


    //当服务器连接成功的时候会调用这个方法 @OnStompConnect修饰的方法
    @OnStompConnect
    public void onConnect() {
        stringBuilder = new StringBuilder();
        stringBuilder.append("连接成功\n");
        tvTextView.setText(stringBuilder.toString());
	

    //发Subscribe消息到服务端
    practice.sendStompMessage(StompMessageHelper.createSubscribeStompMessage("/topic/hello", null));
        
    }

  //连接断开的时候会调用这个方法 @OnStompDisConnect修饰的方法
    @OnStompDisConnect
    public void onDisConnect(Integer code, String info) {
        stringBuilder.append("连接失败:code=" + code + "__info=" + info + "\n");
        tvTextView.setText(stringBuilder.toString());
    }


   //例如当收到destination=/topic/hello的消息时候会回调这个方法
    @OnStompSubscribe("/topic/hello")
    public void message(StompMessage stompMessage) {
        stringBuilder.append(stompMessage.compile() + "\n");
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




