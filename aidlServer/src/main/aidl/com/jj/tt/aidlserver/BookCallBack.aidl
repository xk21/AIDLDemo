// BookCallBack.aidl
package com.jj.tt.aidlserver;

// Declare any non-default types here with import statements

interface BookCallBack {

    void getCount(int count);
    void get4BookName(String bookName);//获取集合第四个元素书名
    void get3BookName(String bookName);//获取已经删除的第三个元素书名
}
