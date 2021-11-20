package jp.osaka.cherry.addressbook.android.view;

import android.view.View;

import java.util.Collection;

/**
 * 検索一覧コールバックインタフェース
 */
public interface ISearchCollectionCallbacks<T> {
    /**
     * 項目のポップアップメニュー選択
     *
     * @param collectionView 一覧表示
     * @param view           項目表示
     * @param item           項目
     */
    void onSelectedMore(ISearchCollectionView<T> collectionView, View view, T item);

    /**
     * 項目の選択
     *
     * @param collectionView 一覧表示
     * @param view           項目表示
     * @param item           項目
     */
    void onSelected(ISearchCollectionView<T> collectionView, View view, T item);

    /**
     * 更新通知
     *
     * @param view 表示
     * @param collection 一覧
     */
    void onUpdated(ISearchCollectionView<T> view, Collection<? extends T> collection);
}
