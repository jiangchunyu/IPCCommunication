package com.chunyu.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.chunyu.impl.ConnectListener;
import com.chunyu.impl.ReceiverListener;
import com.chunyu.service.IPCService;

/**
 * description : 服务端绑定服务工具类
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 上午10:19
 * mail        : 1055655886@qq.com
 */
public class ServiceUtils {
    private ReceiverListener mReceiverListener;
    private ConnectListener mConnectListener;
    private Context mContext;
    private boolean isBind = false;
    private static final String TAG = "jcy_ServiceUtils";
    private boolean connect = false;

    /**
     * 初始化
     * @param context
     */
    public ServiceUtils(Context context) {
        isBind = false;
        mContext = context;
        connect = false;
    }

    /**
     * 绑定服务
     */
    public boolean bindService() {
        return bindService(null, mReceiverListener);
    }

    /**
     * 绑定服务
     * @param receiverListener
     */
    public boolean bindService(ReceiverListener receiverListener) {
        return bindService(null, receiverListener);
    }

    /**
     * 绑定服务
     * @param cls
     */
    public boolean bindService(Class<?> cls) {
       return bindService(cls, mReceiverListener);
    }

    /**
     * 绑定服务,传入服务类名,若cls为空则默认进入IPCService 服务
     * @param cls
     * @param receiverListener 消息回调函数
     */
    public boolean bindService(Class<?> cls, ReceiverListener receiverListener) {
        if (!isBind) {
            Log.d(TAG, " ---  bindService   --- ");
            connect = false;
            if (receiverListener != null)
                mReceiverListener = receiverListener;
            if (cls == null) {
                cls = IPCService.class;
            }
            Intent intent = new Intent(mContext, cls);
            mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
            isBind = true;
            if(mConnectListener!=null){
                mConnectListener.onBind();
            }
            return true;
        }
        return false;
    }

    /**
     * 取消绑定服务
     */
    public boolean unBindService() {
        if (isBind) {
            Log.d(TAG, " --- unBindService  --- ");
            isBind = false;
            connect = false;
            mContext.unbindService(conn);
            if(mConnectListener!=null){
                mConnectListener.onUnbind();
            }
            return true;
        }
        return false;
    }

    /**
     *
     */
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d(TAG, "---  onServiceDisconnected   --- ");
            if(mConnectListener!=null){
                mConnectListener.onDisconnected();
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub

            if (service != null) {
                Log.d(TAG, "---  onServiceConnected  sucessed   ---");
                if(mConnectListener!=null){
                    mConnectListener.onConnected(true);
                }
                connect = true;
                if (mReceiverListener != null) {
                    ((IPCService.IPCBinder) service).getService().setReceiverListener(mReceiverListener);
                }
            }else {
                Log.d(TAG, "---  onServiceConnected  failed service == null   ---");
                if(mConnectListener!=null){
                    mConnectListener.onConnected(false);
                }
            }
        }
    };

    /**
     * 消息回调函数,必须为同步
     * @param receiverListener
     */
    public void setReceiverListener(ReceiverListener receiverListener) {
        mReceiverListener = receiverListener;
    }

    /**
     * 连接相关回调
     * @param connectListener
     */
    public void setConnectListener(ConnectListener connectListener) {
        mConnectListener = connectListener;
    }

    /**
     * 是否连接成功
     * @return
     */
    public boolean isConnect() {
        return connect;
    }

}
