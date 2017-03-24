package com.chunyu.impl;

/**
 * description : 绑定服务的回调函数
 * autour      : 姜春雨@沈阳
 * date        : 17-3-24 上午10:38
 * mail        : 1055655886@qq.com
 */
public interface ConnectListener {
    void onBind();
    void onDisconnected();
    void onConnected(boolean success);
    void onUnbind();
}
