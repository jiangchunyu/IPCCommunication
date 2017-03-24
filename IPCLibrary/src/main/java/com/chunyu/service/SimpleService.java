package com.chunyu.service;

import android.os.Bundle;
import android.util.Log;

import com.chunyu.base.BaseIPCService;

import static android.content.ContentValues.TAG;

/**
 * description : 不使用IPCService自己实现Service的demo
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 下午1:28
 * mail        : 1055655886@qq.com
 */
public class SimpleService extends BaseIPCService {
    @Override
    public Bundle dealMessage(int code, Bundle msg) {
        String msgStr = msg.getString("msgStr");
        int msgInt= msg.getInt("msgInt");
        Log.d(TAG, "receiveInfo: msgStr "+msgStr+"   ,msgInt : "+msgInt);
        Bundle ret = new Bundle();
        ret.putString("return","SimpleService 返回  code "+code+" ,msgStr "+msgStr+"   ,msgInt : "+msgInt);
        return ret;
    }
}
