// IBookService.aidl
package com.jj.tt.aidlserver;

// Declare any non-default types here with import statements
import com.jj.tt.aidlserver.Book;
import com.jj.tt.aidlserver.BookCallBack;

interface IBookService {

    List<Book> getBookList();

    void addInBook(in Book book);

    void addOutBook(out Book book);

    void addInOutBook(inout Book book);
    
    void get4BookName();

    void get3BookName();

    void registerCallback(BookCallBack bc);

    void unregisterCallback(BookCallBack bc);
}
