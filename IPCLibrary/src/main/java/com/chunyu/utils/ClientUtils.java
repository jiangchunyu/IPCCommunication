package com.chunyu.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;

import com.chunyu.contant.Contant;
import com.chunyu.impl.ConnectListener;

import static com.chunyu.contant.Contant.DESCRIPTOR;


/**
 * description : 客户端绑定服务工具类
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 上午10:25
 * mail        : 1055655886@qq.com
 */
public class ClientUtils {
    private Context mContext;
    private IBinder mIBinder;
    private boolean isBind = false;
    private static final String TAG = "jcy_ClientUtils";
    private boolean connect = false;
    private ConnectListener mConnectListener;
    private String action="";

    /**
     * 初始化
     *
     * @param context
     */
    public ClientUtils(Context context) {
        isBind = false;
        mContext = context;
        connect = false;
    }

    /**
     * 绑定服务
     */
    public boolean bindService() {
        action=Contant.DEFAUL_TPACKAGE;
        return bindService(Contant.DEFAUL_TPACKAGE);
    }

    /**
     * 绑定服务
     *
     * @param action
     */
    public boolean bindService(String action) {
        if (!isBind) {
            this.action=action;
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
                mIBinder = service;
            }
        }
    };


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

    public void setConnectListener(ConnectListener connectListener) {
        mConnectListener = connectListener;
    }

    public boolean isConnect() {
        return connect;
    }
}
