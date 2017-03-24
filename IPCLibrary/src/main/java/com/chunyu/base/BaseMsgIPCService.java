package com.chunyu.base;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * description :  进程间通信基础类，使用Message进行通信
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 下午1:42
 * mail        : 1055655886@qq.com
 */
public abstract class BaseMsgIPCService extends Service {

    private Messenger mMessenger = null;

    @Override
    public IBinder onBind(Intent intent) {
        initHander();
        return mMessenger.getBinder();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        mMessenger=null;
        return super.onUnbind(intent);
    }

    private void initHander(){
        if (mMessenger == null) {
            HandlerThread mHandlerThread = new HandlerThread("BaseMsgIPCService");
            mHandlerThread.start();
            Handler mHandler= new Handler(mHandlerThread.getLooper()){
                @Override
                public void handleMessage(Message msgfromClient)
                {
                    Message msgToClient = Message.obtain(msgfromClient);//返回给客户端的消息
                    boolean send = dealMessage(msgToClient,msgfromClient);//处理消息
                    if(send){
                        try
                        {
                            //通过Message进行回复消息
                            msgfromClient.replyTo.send(msgToClient);
                        } catch (RemoteException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    super.handleMessage(msgfromClient);
                }
            };
            mMessenger= new Messenger(mHandler);
        }
    }
    /**
     * 处理消息
     * @param msgToClient 发送给客户端的消息
     * @param msgfromClient 来自客户端的消息
     * @return true 回复消息 false 不回复消息
     */
    public abstract boolean dealMessage(Message msgToClient,Message msgfromClient);
}
