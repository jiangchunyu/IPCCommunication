package com.chunyu.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.chunyu.contant.Contant;
import com.chunyu.impl.ConnectListener;
import com.chunyu.impl.MessageListener;

/**
 * description : 客户端绑定服务工具类(使用Message进行通信)
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 下午2:20
 * mail        : 1055655886@qq.com
 */
public class ClientMsgUtils {
    private Context mContext;
    private Messenger mService;
    private boolean isBind = false;
    private static final String TAG = "jcy_ClientUtils";
    private boolean connect = false;
    private ConnectListener mConnectListener;
    private MessageListener mMessageListener;
    private Messenger mMessenger;
    /**
     * 初始化
     *
     * @param context
     */
    public ClientMsgUtils(Context context) {
        isBind = false;
        mContext = context;
        connect = false;

    }


    private void initMessage(){
        if(mMessenger!=null){
            return;
        }
        HandlerThread mHandlerThread = new HandlerThread("ClientMsg");
        mHandlerThread.start();
        Handler mHandler= new Handler(mHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msgFromServer)
            {
                Log.e(TAG, "handleMessage: Thread-Name : "+Thread.currentThread().getName()+"   ,Id : "+Thread.currentThread().getId() );
                if(mMessageListener!=null){
                    mMessageListener.msgInfo(msgFromServer);
                }
                super.handleMessage(msgFromServer);
            }
        };
        mMessenger= new Messenger(mHandler);
    }

    /**
     * 绑定服务
     */
    public boolean bindService() {
        return bindService(Contant.DEFAUL_TPACKAGE);
    }

    /**
     * 绑定服务
     *
     * @param action
     */
    public boolean bindService(String action) {
        if (!isBind) {
            initMessage();
            if (mConnectListener != null) {
                mConnectListener.onBind();
            }
            Log.d(TAG, " ---  bindService   --- ");
            connect = false;
            Intent intent = new Intent();
            intent.setAction(action);
            mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
            isBind = true;
            return true;
        }
        return false;
    }

    /**
     * 解除绑定
     * @return
     */
    public boolean unBindService() {
        if (isBind) {
            Log.d(TAG, " --- unBindService  --- ");
            isBind = false;
            mContext.unbindService(conn);
            connect = false;
            if (mConnectListener != null) {
                mConnectListener.onUnbind();
            }
            mMessenger=null;
            return true;
        }
        return false;

    }

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
                mService = new Messenger(service);
            }
        }
    };


    /**
     * 向服务端发送消息
     * @return
     * @throws Exception
     */
    public boolean send(Message msgFromClient) throws Exception {
        if (mService == null) {
            throw new Exception("IBinder is Null");
        }
        msgFromClient.replyTo = mMessenger;
        //往服务端发送消息
        mService.send(msgFromClient);
        return true;
    }

    public void setConnectListener(ConnectListener connectListener) {
        mConnectListener = connectListener;
    }

    public boolean isConnect() {
        return connect;
    }


    public void setMessageListener(MessageListener messageListener) {
        mMessageListener = messageListener;
    }
}
