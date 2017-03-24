**[IPCCommunication github地址](https://github.com/jiangchunyu/IPCCommunication)**

#进程间通信
说到Android进程间通信，大家肯定能想到的是编写aidl文件，然后通过aapt生成的类方便的完成服务端，以及客户端代码的编写。
# 什么是[IPCCommunication](https://github.com/jiangchunyu/IPCCommunication)
[IPCCommunication](https://github.com/jiangchunyu/IPCCommunication)是通过两种方式实现进程间通信，一种是手写binder实现进程间通信，互相传递使用Bunder，可以在Bunder中自定义协议，另外一种是基于Message，客户端与服务端之间使用Message进行通信;

#为什么要写这个框架
曾经听过一个人说过，程序中尽量使用通用组件或者框架，这样可以尽量的少写代码，这样有两个好处，一是少写代码就意味着少犯错误，二是可以多出喝咖啡的时间 ;
#使用
手写Binder方式有两种方式
1.使用通用的IPCService服务库，包括与Activity通信
**服务端**
服务端可以通过Activity 启动也可以不启动，若不启动可以通过继承BaseIPCService的方式，具体实现查看[SimpleService](https://github.com/jiangchunyu/IPCCommunication/blob/master/IPCLibrary/src/main/java/com/chunyu/service/SimpleService.java)
```
public class MainActivity extends AppCompatActivity {

    private ServiceUtils mServiceUtils;
    private static final String TAG = "jcy_service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServiceUtils = new ServiceUtils(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //调用默认IPC 服务
        mServiceUtils.setConnectListener(mConnectListener);
        mServiceUtils.bindService();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServiceUtils.unBindService();
    }

    ConnectListener mConnectListener = new ConnectListener() {

        @Override
        public void onBind() {
        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onConnected(boolean success) {
            mServiceUtils.setReceiverListener(mReceiverListener);
        }

        @Override
        public void onUnbind() {
            mServiceUtils.setReceiverListener(null);
        }

        ;
    };

    private ReceiverListener mReceiverListener = new ReceiverListener() {
        @Override
        public Bundle receiveInfo(int code, Bundle msg) {
            String msgStr = msg.getString("msgStr");
            int msgInt = msg.getInt("msgInt");
            Log.d(TAG, "receiveInfo: msgStr " + msgStr + "   ,msgInt : " + msgInt);
            Bundle ret = new Bundle();
            ret.putString("return", "服务端返回  code " + code + " ,msgStr " + msgStr + "   ,msgInt : " + msgInt);
            return ret;
        }
    };
}
```
**客户端**
```
public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "jcy_client_one";
    private TextView mTextView;
    private TextView tv_state;
    private ClientUtils mClientUtils;
    private Button btnGet, btn_bind, btn_unbind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.tv_service);
        tv_state = (TextView) findViewById(R.id.tv_state);
        btnGet = (Button) findViewById(R.id.btn_getService);
        btn_bind = (Button) findViewById(R.id.btn_bind);
        btn_unbind = (Button) findViewById(R.id.btn_unbind);
        btnGet.setEnabled(false);
        tv_state.setText("  服务未绑定 ");
        mClientUtils = new ClientUtils(this);
        mClientUtils.setConnectListener(mConnectListener);
        btn_bind.setEnabled(true);
        btn_unbind.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mClientUtils!=null){
            mClientUtils.unBindService();
        }
    }

    /**
     * 获取数据
     *
     * @param v
     */
    public void onGetServie(View v) {
        if (mClientUtils.isConnect()) {
            Bundle send = new Bundle();
            send.putString("msgStr", "ClientOneApp发送消息");
            send.putInt("msgInt", 110);
            try {
                Bundle ret = mClientUtils.send(110, send);
                if (ret == null) {
                    Log.e(TAG, "onGetServie:  ret==null");
                } else {
                    String retStr = ret.getString("return");
                    mTextView.append("\n"+retStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onGetServie:Exception   " + e.toString());
            }
        } else {
            Toast.makeText(this, "未连接服务，无法获取消息", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 绑定服务
     * @param v
     */
    public void onBindServie(View v) {
        mClientUtils.bindService();
    }

    /**
     * 解除绑定
     * @param v
     */
    public void onUnBindServie(View v) {
        mClientUtils.unBindService();
    }



    /**
     * 连接状态回调
     */
    private ConnectListener mConnectListener = new ConnectListener() {
        @Override
        public void onBind() {
            tv_state.setText("  服务正在绑定中…… ");
        }

        @Override
        public void onDisconnected() {
            btnGet.setEnabled(false);
            tv_state.setText("  服务断开连接 ");
            btn_bind.setEnabled(true);
            btn_unbind.setEnabled(false);
        }

        @Override
        public void onConnected(boolean success) {
            if (success) {
                tv_state.setText("  服务绑定成功 ");
                btn_bind.setEnabled(false);
                btn_unbind.setEnabled(true);
                btnGet.setEnabled(true);
            } else {
                btnGet.setEnabled(false);
                tv_state.setText("  服务绑定失败 ");
                mClientUtils.unBindService();
                btn_bind.setEnabled(true);
                btn_unbind.setEnabled(false);
            }
        }

        @Override
        public void onUnbind() {
            btn_bind.setEnabled(true);
            btn_unbind.setEnabled(false);
            tv_state.setText("  服务已解除绑定 ");
            mTextView.setText("");
        }
    };

}
```
另外还支持自定义Service 的方式 具体查看SimpleService类
Message的使用方式与自定义Binder的形式类似，大部分使用方式几乎相同

#分析
**手写Binder**
 
![Binder.png](http://upload-images.jianshu.io/upload_images/2642181-09020caa4dc12d0c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
这个图是盗用别人的嘿嘿;
首先Activity通过调用bindService去绑定一个远程的服务（Service），绑定成功后返回一个IBinder对象。这时候双方就算是建立了连接了。
建立连接之后，双方就可以通过持有的IBinder进行通信。Activity使用IBinder的transact方法去给底层的Binder Driver（Linux层）发送消息间接调用底层的IBinder的execTransact方法。
而execTransact导致的结果就是调用onTransact方法。那么这时候事件的处理就可以在该环节进行了。
上层主要使用 这个方法进行通信 处理消息;
```
public boolean onTransact(int code, android.os.Parcel data, 
android.os.Parcel reply, int flags) 
```
 这个方法 一般情况下 都是返回true的，也只有返回true的时候才有意义，如果返回false了 就代表这个方法执行失败，onTransact 这个方法 就是运行在Binder线程池中的，一般就是客户端发起请求，然后android底层代码把这个客户端发起的，请求 封装成3个参数 来调用这个onTransact方法，第一个参数code 就代表通信的标志位，data就是方法参数（客户端传递过来的数据），reply就是方法返回值（通过这个方法可以向客户端中返回数据）。

自定义Binder为
```
/**
     * description : 自定义Binder实现消息传递
     * autour      : 姜春雨@沈阳
     * date        : 17-3-24 上午10:33
     * mail        : 1055655886@qq.com
     */
    public class IPCBinder extends Binder {

        /**
         *  处理消息
         * @param code 识别码
         * @param data 调用transact的对象传送过去的参数
         * @param reply 调用onTransact的对象返回的参数
         * @param flags Java里面默认的native方法都是阻塞的，当不需要阻塞的时候设置为IBinder.FLAG_ONEWAY，否则设置为0
         * @return
         * @throws RemoteException
         */
        @Override
        protected boolean onTransact(final int code, final Parcel data, final Parcel reply, final int flags) throws RemoteException {
            data.enforceInterface(Contant.DESCRIPTOR);
            Bundle bundle = dealMessage(code,data.readBundle());
            reply.writeNoException();
            reply.writeBundle(bundle);
            return true;
        }

        /**
         * 获取当前Service,用于与Service通信
         * @return
         */
        public BaseIPCService getService(){
            return BaseIPCService.this;
        }
    }

```
暂时还未找到如何实现异步通信

客户端发送消息
```
private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d(TAG, "---  onServiceDisconnected   --- ");
            connect = false;
            if (mConnectListener != null) {
                mConnectListener.onDisconnected();
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            if (service == null) {
                connect = false;
                Log.d(TAG, "---  onServiceConnected  failed service == null   ---");
                if (mConnectListener != null) {
                    mConnectListener.onConnected(false);
                }
            } else {
                Log.d(TAG, "---  onServiceConnected  sucessed   ---");
                connect = true;
                if (mConnectListener != null) {
                    mConnectListener.onConnected(true);
                }
                mIBinder = service;
            }
        }
    };
````
连接成功后通过可以获取到Binder对象
发送消息使用  mIBinder.transact(code, _data, _reply, 0);

```
 /**
     * 向服务端发送消息
     * @param code 消息类型
     * @param bundle 消息体
     * @return 返回值为服务器的返回
     * @throws Exception
     */
    public Bundle send(int code, Bundle bundle) throws Exception {
        if (mIBinder == null) {
            throw new NullPointerException("IBinder is Null");
        }
        Bundle result = null;
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeBundle(bundle);
            mIBinder.transact(code,
                    _data, _reply, 0);
            _reply.readException();
            result = _reply.readBundle();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            _reply.recycle();
            _data.recycle();
            return result;
        }
    }
```
#Message通信
服务端的onBind是这么写的：
```
public IBinder onBind(Intent intent)
    {
        return mMessenger.getBinder();
    }
```
那么点进去：
```
public IBinder getBinder() {
        return mTarget.asBinder();
    }
```
可以看到返回的是mTarget.asBinder();

mTarget是哪来的呢？

```
HandlerThread mHandlerThread = new HandlerThread("BaseMsgIPCService");
            mHandlerThread.start();
            Handler mHandler= new Handler(mHandlerThread.getLooper()){
                @Override
                public void handleMessage(Message msgfromClient)
                {
                    super.handleMessage(msgfromClient);
                }
            };
            mMessenger= new Messenger(mHandler);

 public Messenger(Handler target) {
        mTarget = target.getIMessenger();
    }
```
原来是Handler返回的，我们继续跟进去

```
    final IMessenger getIMessenger() {
        synchronized (mQueue) {
            if (mMessenger != null) {
                return mMessenger;
            }
            mMessenger = new MessengerImpl();
            return mMessenger;
        }
    }

     private final class MessengerImpl extends IMessenger.Stub {
        public void send(Message msg) {
            msg.sendingUid = Binder.getCallingUid();
            Handler.this.sendMessage(msg);
        }
    }
```
mTarget是一个MessengerImpl对象，那么asBinder实际上是返回this，也就是MessengerImpl对象；
**客户端**
客户端首先通过onServiceConnected拿到sevice（Ibinder）对象，这里没什么特殊的，我们平时的写法也是这样的，只不过我们平时会这么写：
IMessenger.Stub.asInterface(service)拿到接口对象进行调用；
而，我们的代码中是
mService = new Messenger(service);
跟进去，你会发现：
```
public Messenger(IBinder target) { mTarget = IMessenger.Stub.asInterface(target); }
```
和我们平时的写法一模一样！
到这里就可以明白，客户端与服务端通信，实际上和我们平时的写法没有任何区别，通过编写aidl文件，服务端onBind利用Stub编写接口实现返回；客户端利用回调得到的IBinder对象，使用IMessenger.Stub.asInterface(target)拿到接口实例进行调用。
（2）服务端与客户端通信
那么，客户端与服务端通信的确没什么特殊的地方，我们完全也可以编写个类似的aidl文件实现；那么服务端是如何与客户端通信的呢？
还记得，客户端send方法发送的是一个Message，这个Message.replyTo指向的是一个mMessenger，我们在Activity中初始化的。
那么将消息发送到服务端，肯定是通过序列化与反序列化拿到Message对象，我们看下Message的反序列化的代码：
```
# Message
private void readFromParcel(Parcel source) {
        what = source.readInt();
        arg1 = source.readInt();
        arg2 = source.readInt();
        if (source.readInt() != 0) {
            obj = source.readParcelable(getClass().getClassLoader());
        }
        when = source.readLong();
        data = source.readBundle();
        replyTo = Messenger.readMessengerOrNullFromParcel(source);
        sendingUid = source.readInt();
    }
```
主要看replyTo，调用的是Messenger.readMessengerOrNullFromParcel
```
public static Messenger readMessengerOrNullFromParcel(Parcel in) {
        IBinder b = in.readStrongBinder();
        return b != null ? new Messenger(b) : null;
    }

    public static void writeMessengerOrNullToParcel(Messenger messenger,
            Parcel out) {
        out.writeStrongBinder(messenger != null ? messenger.mTarget.asBinder()
                : null);
    }
```
通过上面的writeMessengerOrNullToParcel可以看到，它将客户端的messenger.mTarget.asBinder()对象进行了恢复，客户端的message.mTarget.asBinder()是什么？
客户端也是通过Handler创建的Messenger，于是asBinder返回的是：
```
public Messenger(Handler target) {
        mTarget = target.getIMessenger();
    }
 final IMessenger getIMessenger() {
        synchronized (mQueue) {
            if (mMessenger != null) {
                return mMessenger;
            }
            mMessenger = new MessengerImpl();
            return mMessenger;
        }
    }

    private final class MessengerImpl extends IMessenger.Stub {
        public void send(Message msg) {
            msg.sendingUid = Binder.getCallingUid();
            Handler.this.sendMessage(msg);
        }
    }

   public IBinder getBinder() {
        return mTarget.asBinder();
    }
```

那么asBinder，实际上就是MessengerImpl extends IMessenger.Stub
中的asBinder了。
```
#IMessenger.Stub

@Override 
public android.os.IBinder asBinder()
{
return this;
}
```

那么其实返回的就是MessengerImpl对象自己。到这里可以看到message.mTarget.asBinder()其实返回的是客户端的MessengerImpl对象。
最终，发送给客户端的代码是这么写的：
```
msgfromClient.replyTo.send(msgToClient);

public void send(Message message) throws RemoteException {
        mTarget.send(message);
    }
```
这个mTarget实际上就是对客户端的MessengerImpl对象的封装，那么send(message)（屏蔽了transact/onTransact的细节），这个message最终肯定传到客户端的handler的handleMessage方法中。
Message源码分析部分摘抄自[鸿洋_](http://blog.csdn.net/lmj623565791/article/details/47017485)的博客




**[IPCCommunication github地址](https://github.com/jiangchunyu/IPCCommunication)**
