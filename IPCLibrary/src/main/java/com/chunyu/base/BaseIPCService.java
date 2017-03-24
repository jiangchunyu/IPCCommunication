package com.chunyu.base;

/**
 * description : $todo
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 下午1:40
 * mail        : 1055655886@qq.com
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.chunyu.contant.Contant;
import com.chunyu.impl.ReceiverListener;

/**
 * description : 进程间通讯基础类
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 下午1:41
 * mail        : 1055655886@qq.com
 */
public abstract class BaseIPCService extends Service {
    protected ReceiverListener mReceiverListener;


    @Override
    public IBinder onBind(Intent intent) {
        return mIPCBinder;
    }
    private IPCBinder mIPCBinder = new IPCBinder();

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

    public void setReceiverListener(ReceiverListener receiverListener) {
        mReceiverListener = receiverListener;
    }

    public abstract Bundle dealMessage(int code, Bundle msg);

}
