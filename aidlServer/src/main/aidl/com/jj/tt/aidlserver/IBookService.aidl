// IBookService.aidl
package com.jj.tt.aidlserver;

// Declare any non-default types here with import statements
import com.jj.tt.aidlserver.Book;//注意导包
import com.jj.tt.aidlserver.BookCallBack;

interface IBookService {

    List<Book> getBookList();

    void addInBook(in Book book);

    void addOutBook(out Book book);

    void addInOutBook(inout Book book);

    void get4BookName();//获取集合第四个元素

    void get3BookName();//调用删除集合第三个元素

    void registerCallback(BookCallBack bc);

    void unregisterCallback(BookCallBack bc);
}
