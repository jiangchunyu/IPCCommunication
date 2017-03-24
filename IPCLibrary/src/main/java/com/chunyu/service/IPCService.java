package com.chunyu.service;

import android.os.Bundle;

import com.chunyu.base.BaseIPCService;

import static com.chunyu.contant.Contant.RETRUN;

/**
 * description : IPC通信通用类
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 上午10:43
 * mail        : 1055655886@qq.com
 */
public class IPCService extends BaseIPCService {

    @Override
    public Bundle dealMessage(int code, Bundle msg) {
        Bundle bundle =null;
        if(mReceiverListener!=null){
            bundle = mReceiverListener.receiveInfo(code,msg);
            bundle.putString(RETRUN,"Message is dealwith ");
        }else {
            bundle = new Bundle();
            bundle.putString(RETRUN,"MessageListener is null ");
        }
        return bundle;
    }
}
