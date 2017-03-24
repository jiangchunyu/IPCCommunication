package com.chunyu.clienttwoapp;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chunyu.contant.Contant;
import com.chunyu.impl.ConnectListener;
import com.chunyu.impl.MessageListener;
import com.chunyu.utils.ClientMsgUtils;

import static com.chunyu.contant.Contant.MSG_SUM;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "jcy_client_one";
    private TextView mTextView;
    private TextView tv_state;
    private ClientMsgUtils mClientUtils;
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
        mClientUtils = new ClientMsgUtils(this);
        mClientUtils.setConnectListener(mConnectListener);
        mClientUtils.setMessageListener(mMessageListener);
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
            int a = (int) (Math.random() * 100);
            int b = (int) (Math.random() * 100);
            String info = "";
            if (mTextView.getText().toString().length() > 0) {
                info = "\n";
            }
            info = info + a + " + " + b + " = ";
            mTextView.append(info);
            Message msgFromClient = Message.obtain(null, MSG_SUM, a, b);
            try {
                mClientUtils.send(msgFromClient);
            } catch (Exception e) {
                Log.e(TAG, "onGetServie: "+e.toString());
                e.printStackTrace();
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
        mClientUtils.bindService(Contant.SIMPLE_MSG_TPACKAGE);
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
    } ;

    /**
     * 消息回调
     */
   private MessageListener mMessageListener = new MessageListener() {
       @Override
       public void msgInfo(Message msgFromServer) {
           switch (msgFromServer.what){
               case MSG_SUM:
                   Log.i(TAG, "handleMessage: Thread-Name : "+Thread.currentThread().getName()+"   ,Id : "+Thread.currentThread().getId() );
                   mTextView.append(" "+msgFromServer.arg2);
                   break;
           }
       }
   };


}
