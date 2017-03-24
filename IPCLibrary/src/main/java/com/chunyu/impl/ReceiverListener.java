package com.chunyu.impl;

import android.os.Bundle;

/**
 * description : 消息回调,用于处理自定义binder中的数据
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 下午2:29
 * mail        : 1055655886@qq.com
 */
public interface ReceiverListener {
    Bundle receiveInfo(int code, Bundle msg);
}
