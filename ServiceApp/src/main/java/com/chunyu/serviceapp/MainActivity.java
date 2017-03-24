package com.chunyu.serviceapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.chunyu.impl.ConnectListener;
import com.chunyu.impl.ReceiverListener;
import com.chunyu.utils.ServiceUtils;

public class MainActivity extends AppCompatActivity {

    private ServiceUtils mServiceUtils;
    private static final String TAG = "jcy_service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServiceUtils = new ServiceUtils(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //调用默认IPC 服务
        mServiceUtils.setConnectListener(mConnectListener);
        mServiceUtils.bindService();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServiceUtils.unBindService();
    }

    ConnectListener mConnectListener = new ConnectListener() {

        @Override
        public void onBind() {
        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onConnected(boolean success) {
            mServiceUtils.setReceiverListener(mReceiverListener);
        }

        @Override
        public void onUnbind() {
            mServiceUtils.setReceiverListener(null);
        }

        ;
    };

    private ReceiverListener mReceiverListener = new ReceiverListener() {
        @Override
        public Bundle receiveInfo(int code, Bundle msg) {
            String msgStr = msg.getString("msgStr");
            int msgInt = msg.getInt("msgInt");
            Log.d(TAG, "receiveInfo: msgStr " + msgStr + "   ,msgInt : " + msgInt);
            Bundle ret = new Bundle();
            ret.putString("return", "服务端返回  code " + code + " ,msgStr " + msgStr + "   ,msgInt : " + msgInt);
            return ret;
        }
    };


}
