package com.chunyu.clientoneapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chunyu.impl.ConnectListener;
import com.chunyu.utils.ClientUtils;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "jcy_client_one";
    private TextView mTextView;
    private TextView tv_state;
    private ClientUtils mClientUtils;
    private Button btnGet, btn_bind, btn_unbind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.tv_service);
        tv_state = (TextView) findViewById(R.id.tv_state);
        btnGet = (Button) findViewById(R.id.btn_getService);
        btn_bind = (Button) findViewById(R.id.btn_bind);
        btn_unbind = (Button) findViewById(R.id.btn_unbind);
        btnGet.setEnabled(false);
        tv_state.setText("  服务未绑定 ");
        mClientUtils = new ClientUtils(this);
        mClientUtils.setConnectListener(mConnectListener);
        btn_bind.setEnabled(true);
        btn_unbind.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mClientUtils!=null){
            mClientUtils.unBindService();
        }
    }

    /**
     * 获取数据
     *
     * @param v
     */
    public void onGetServie(View v) {
        if (mClientUtils.isConnect()) {
            Bundle send = new Bundle();
            send.putString("msgStr", "ClientOneApp发送消息");
            send.putInt("msgInt", 110);
            try {
                Bundle ret = mClientUtils.send(110, send);
                if (ret == null) {
                    Log.e(TAG, "onGetServie:  ret==null");
                } else {
                    String retStr = ret.getString("return");
                    mTextView.append("\n"+retStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onGetServie:Exception   " + e.toString());
            }
        } else {
            Toast.makeText(this, "未连接服务，无法获取消息", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 绑定服务
     * @param v
     */
    public void onBindServie(View v) {
        mClientUtils.bindService();
    }

    /**
     * 解除绑定
     * @param v
     */
    public void onUnBindServie(View v) {
        mClientUtils.unBindService();
    }



    /**
     * 连接状态回调
     */
    private ConnectListener mConnectListener = new ConnectListener() {
        @Override
        public void onBind() {
            tv_state.setText("  服务正在绑定中…… ");
        }

        @Override
        public void onDisconnected() {
            btnGet.setEnabled(false);
            tv_state.setText("  服务断开连接 ");
            btn_bind.setEnabled(true);
            btn_unbind.setEnabled(false);
        }

        @Override
        public void onConnected(boolean success) {
            if (success) {
                tv_state.setText("  服务绑定成功 ");
                btn_bind.setEnabled(false);
                btn_unbind.setEnabled(true);
                btnGet.setEnabled(true);
            } else {
                btnGet.setEnabled(false);
                tv_state.setText("  服务绑定失败 ");
                mClientUtils.unBindService();
                btn_bind.setEnabled(true);
                btn_unbind.setEnabled(false);
            }
        }

        @Override
        public void onUnbind() {
            btn_bind.setEnabled(true);
            btn_unbind.setEnabled(false);
            tv_state.setText("  服务已解除绑定 ");
            mTextView.setText("");
        }
    };

}
