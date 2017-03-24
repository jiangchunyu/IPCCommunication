package com.chunyu.service;

import android.os.Message;

import com.chunyu.base.BaseMsgIPCService;

import static com.chunyu.contant.Contant.MSG_SUM;

/**
 * description : 使用Message 处理数据demo
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 下午2:50
 * mail        : 1055655886@qq.com
 */
public class SimpleMsgService extends BaseMsgIPCService{

    @Override
    public boolean dealMessage(Message msgToClient, Message msgfromClient) {
        switch (msgfromClient.what) {
            case MSG_SUM:
                msgToClient.arg2 = msgfromClient.arg1 + msgfromClient.arg2;
                break;
        }
        return true;
    }
}
