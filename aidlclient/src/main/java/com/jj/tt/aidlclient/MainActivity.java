package com.jj.tt.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jj.tt.aidlserver.Book;
import com.jj.tt.aidlserver.BookCallBack;
import com.jj.tt.aidlserver.IBookService;

import java.util.IllegalFormatCodePointException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_1;
    private Button bt_2;
    private Button bt_3;
    private Button bt_4;
    private Button bt_5;
    private Button bt_6;
    private TextView tv_1;
    private TextView tv_2;
    private TextView tv_3;
    private boolean isConnectService = false;
    private IBookService iBookService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnectService)
            unbindService(serviceConnection);
        if (iBookService!=null){
            try {
                iBookService.unregisterCallback(bookCallBack);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        bt_1 = findViewById(R.id.bt_1);
        bt_2 = findViewById(R.id.bt_2);
        bt_3 = findViewById(R.id.bt_3);
        bt_4 = findViewById(R.id.bt_4);
        bt_5 = findViewById(R.id.bt_5);
        bt_6 = findViewById(R.id.bt_6);

        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_3 = findViewById(R.id.tv_3);

        tv_3.setMovementMethod(new ScrollingMovementMethod());

        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
        bt_3.setOnClickListener(this);
        bt_4.setOnClickListener(this);
        bt_5.setOnClickListener(this);
        bt_6.setOnClickListener(this);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(getLocalClassName(), "jjyh 完成AIDLService服务");
            iBookService = IBookService.Stub.asInterface(service);
            isConnectService = true;
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(getLocalClassName(), "jjyh 无法绑定aidlserver的AIDLService服务");
            isConnectService = false;
        }
    };

    //链接服务端
    private void connectService() {
        Intent intent = new Intent();
        intent.setPackage("com.jj.tt.aidldemo");
        intent.setAction("android.intent.action.BOOK");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    //实现回调接口获取相关数据
    private final BookCallBack.Stub bookCallBack = new BookCallBack.Stub() {
        @Override
        public void getCount(final int count) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_2.setText("收到服务端数据:" + count);
                }
            });
        }

        @Override
        public void get4BookName(String bookName) throws RemoteException {
            tv_1.setText("获取到第4号书名:" + bookName);
        }

        @Override
        public void get3BookName(String bookName) throws RemoteException {
            tv_1.setText("获取到删除的第3号书名:" + bookName);
        }
    };

    @Override
    public void onClick(View v) {
        if (!isConnectService || iBookService == null) {
            connectService();
            Toast.makeText(this, "当前与服务端处于未连接状态，正在尝试重连，" +
                    "请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.bt_1:

                Book book1 = new Book("客户端书 in");
                try {
                    iBookService.addInBook(book1);
                    tv_1.setText("in由客户端流向服务端,客户端不能收到服务端具体信息,如下\n书名:"
                            + book1.getName());
                    getBookList(iBookService.getBookList());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.bt_2:

                Book book2 = new Book("客户端书 out");
                try {
                    iBookService.addOutBook(book2);
                    tv_1.setText("out由服务端流向客户端,服务端不能收到客户端具体信息,如下\n书名:"
                            +book2.getName());
                    getBookList(iBookService.getBookList());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.bt_3:

                Book book3 = new Book("客户端书 inOut");
                try {
                    iBookService.addInOutBook(book3);
                    tv_1.setText("inout客户端和服务端双向流通,如下\n书名:"+book3.getName());
                    getBookList(iBookService.getBookList());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.bt_4:
                try {
                    iBookService.registerCallback(bookCallBack);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_5:
                try {
                    iBookService.registerCallback(bookCallBack);
                    iBookService.get4BookName();
                    getBookList(iBookService.getBookList());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_6:
                try {
                    iBookService.registerCallback(bookCallBack);
                    iBookService.get3BookName();
                    getBookList(iBookService.getBookList());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void getBookList(List<Book> bookList) {
        if (bookList.size()>0) {
            StringBuffer buffer = new StringBuffer();
            for (int i=0;i<bookList.size();i++){
                buffer.append(i+1+"号:"+bookList.get(i).getName()+"\n");
            }
            tv_3.setText(buffer);
        }
    }
}
