package jp.osaka.cherry.addressbook.android.view.adapter;

import android.view.View;

import java.util.EventListener;


/**
 * 項目リスナ
 */
public interface ItemListener<T> extends EventListener {

    /**
     * クリック
     *
     * @param view 表示
     * @param item 項目
     */
    void onClickMore(View view, T item);


    /**
     * クリック
     *
     * @param view 表示
     * @param item 項目
     */
    void onClick(View view, T item);


    /**
     * ロングクリック
     *
     * @param view 表示
     * @param item 項目
     */
    void onLongClick(View view, T item);
}
