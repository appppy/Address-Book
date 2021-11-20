package jp.osaka.cherry.addressbook.android.view;

import android.view.View;

import java.util.Collection;

/**
 * 一覧表示のインタフェース
 */
public interface ICollectionView<T> {

    /**
     * 挿入
     *
     * @param item 項目
     */
    void insert(int index, T item);

    /**
     * 追加
     *
     * @param item 項目
     */
    void add(T item);

    /**
     * 削除
     *
     * @param item 項目
     * @return 削除した項目位置
     */
    int remove(T item);

    /**
     * 選択解除
     */
    void diselect();

    /**
     * 全選択
     */
    Collection<? extends T> selectedAll();

    /**
     * 変更通知
     *
     * @param item 項目
     * @return 変更前の項目位置
     */
    int change(T item);

    /**
     * データセット変更通知
     *
     * @param collection 一覧
     */
    void changeAll(Collection<? extends T> collection);

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
        void onSelectedMore(ICollectionView<T> collectionView, View view, T item);

        /**
         * 項目の選択
         *
         * @param collectionView 一覧表示
         * @param view 項目表示
         * @param item 項目
         */
        void onSelected(ICollectionView<T> collectionView, View view, T item);

        /**
         * 項目の選択状態の変更
         *
         * @param collectionView 一覧表示
         * @param view 項目表示
         * @param item 項目
         * @param collection 一覧
         */
        void onSelectedChanged(ICollectionView<T> collectionView, View view, T item, Collection<? extends T> collection);

        /**
         * 項目のスワイプ
         *
         * @param collectionView 一覧表示
         * @param item 項目
         */
        void onSwiped(ICollectionView<T> collectionView, T item);

        /**
         * スクロール開始
         *
         * @param view 一覧表示
         */
        void onScroll(ICollectionView<T> view);

        /**
         * スクロール終了
         *
         * @param view 一覧表示
         */
        void onScrollFinished(ICollectionView<T> view);

        /**
         * 移動変化通知
         *
         * @param view 表示
         * @param collection 一覧
         */
        void onMoveChanged(ICollectionView<T> view, Collection<? extends T> collection);

        /**
         * 更新通知
         *
         * @param view 表示
         * @param collection 一覧
         */
        void onUpdated(ICollectionView<T> view, Collection<? extends T> collection);
    }
}
