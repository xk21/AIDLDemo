package com.jj.tt.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class BookAidlService extends Service {
    private static final String TAG = "BookAidlService";
    private List<Book> bookList = new ArrayList<>();
    private boolean isCount = true;

    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(getClass().getSimpleName(), "发送消息：" + msg.what);

            callBack(msg.what);
            super.handleMessage(msg);
        }
    };

    public BookAidlService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBookService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        for (int i = 1; i <= 5; i++) {
            Book book = new Book("小兵传奇:" + i);
            bookList.add(book);
        }
    }

    private final RemoteCallbackList<BookCallBack> RemoteCallbackList = new RemoteCallbackList<>();
    private final IBookService.Stub iBookService = new IBookService.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addInBook(Book book) throws RemoteException {
            if (book != null) {
                book.setName(book.getName() + "- 服务端改名 in");
                bookList.add(book);
            }
        }

        @Override
        public void addOutBook(Book book) throws RemoteException {
            if (book != null) {
                book.setName(book.getName() + "- 服务端改名 out");
                bookList.add(book);
            }
        }

        @Override
        public void addInOutBook(Book book) throws RemoteException {
            if (book != null) {
                book.setName(book.getName() + "- 服务端改名 inout");
                bookList.add(book);
            }
        }

        @Override
        public void get4BookName() throws RemoteException {
            query4Book();
        }

        @Override
        public void get3BookName() throws RemoteException {
            query3Book();
        }


        @Override
        public void registerCallback(BookCallBack bc) throws RemoteException {
            //获取回调对象
            RemoteCallbackList.register(bc);
            if (isCount){
                startCount();
            }
        }

        @Override
        public void unregisterCallback(BookCallBack bc) throws RemoteException {
            //注销回调对象
            RemoteCallbackList.unregister(bc);
        }

    };

    private void query4Book() {
        int N = RemoteCallbackList.beginBroadcast();
        if (bookList.size() >= 4) {
            for (int i =0;i<=bookList.size();i++){
                if (i==3) {
                    try {
                        for (int j = 0; j < N; j++) {
                            RemoteCallbackList.getBroadcastItem(j).
                                    get4BookName(bookList.get(i).getName());
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "jjyh", e);
                    }
                }
            }
        }else {
            try {
                for (int j = 0; j < N; j++) {
                    RemoteCallbackList.getBroadcastItem(j).
                            get4BookName("不存在该书");
                }
            } catch (RemoteException e) {
                Log.e(TAG, "jjyh", e);
            }
        }
        RemoteCallbackList.finishBroadcast();
    }

    private void query3Book() {
        int N = RemoteCallbackList.beginBroadcast();
        if (bookList.size() >= 3) {
            for (int i =0;i<=bookList.size();i++){
                if (i==2) {
                    try {
                        for (int j = 0; j < N; j++) {
                            RemoteCallbackList.getBroadcastItem(j).
                                    get3BookName(bookList.get(i).getName());
                            bookList.remove(i);
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "jjyh", e);
                    }
                }
            }
        }else {
            try {
                for (int j = 0; j < N; j++) {
                    RemoteCallbackList.getBroadcastItem(j).
                            get3BookName("书籍低于三本");
                }
            } catch (RemoteException e) {
                Log.e(TAG, "jjyh", e);
            }
        }
        RemoteCallbackList.finishBroadcast();
    }

    private void startCount() {
        isCount = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 1; i < 20; i++) {
                        Thread.sleep(1000);
                        Message message = new Message();
                        message.what = (int) i;
                        timeHandler.sendMessage(message);
                    }
                    isCount =true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void callBack(int info) {
        int N = RemoteCallbackList.beginBroadcast();
        try {
            for (int i = 0; i < N; i++) {
                RemoteCallbackList.getBroadcastItem(i).getCount(info);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        RemoteCallbackList.finishBroadcast();
    }
}
