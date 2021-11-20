package jp.osaka.cherry.addressbook.android.view;

import android.view.View;

/**
 * 一覧表示のインタフェース
 */
public interface ISimpleCollectionView<T> {

    /**
     * 項目削除
     *
     * @param item 項目
     * @return 削除した項目位置
     */
    int remove(T item);

    /**
     * @serial コールバック定義
     */
    interface Callbacks<T> {
        /**
         * 項目のポップアップメニュー選択
         *
         * @param collectionView 一覧表示
         * @param view           項目表示
         * @param item           項目
         */
        void onSelectedMore(ISimpleCollectionView<T> collectionView, View view, T item);

        /**
         * 項目の選択
         *
         * @param collectionView 一覧表示
         * @param view 項目表示
         * @param item 項目
         */
        void onSelected(ISimpleCollectionView<T> collectionView, View view, T item);
    }
}
