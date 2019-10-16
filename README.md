# Practice  
Practice  是一个使用在stomp协议并用websocket连接的Android库

底层websocket是需要okhttp依赖 

如何使用  

### 首先你需要引入 okhttp库


然后在引入Practice库
Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
  Step 2. Add the dependency
  
```
  
    	dependencies {
	        implementation 'com.github.jikun2008:Practice:1.0.0'
	}
  

```

code:

```


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTextView = findViewById(R.id.tvTextView);

        practice = new Practice.Builder().version(VersionEnum.VERSION_1_1)
                .build();
        practice.register(this);

    }

    public void testConnect(View view) {
        Request request = new Request.Builder().url("ws://10.28.6.69:8091/noSocketJs").build();
        practice.startConnect(request);

    }

    @OnStompConnect
    public void onConnect() {
        stringBuilder = new StringBuilder();
        stringBuilder.append("连接成功\n");
        tvTextView.setText(stringBuilder.toString());
        practice.sendStompMessage(StompMessageHelper.createSubscribeStompMessage("/topic/hello", null));
    }

    @OnStompDisConnect
    public void onDisConnect(Integer code, String info) {
        stringBuilder.append("连接失败:code=" + code + "__info=" + info + "\n");
        tvTextView.setText(stringBuilder.toString());
    }


    @OnStompSubscribe("/topic/hello")
    public void message(StompMessage stompMessage) {
        stringBuilder.append(stompMessage.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        practice.unregister(this);
        practice.disConnect();
    }

    public void testDisConnect(View view) {
        practice.disConnect();

    }
        



```




