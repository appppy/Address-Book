package jp.osaka.cherry.addressbook.ui.view.timeline;

/**
 * 一覧表示のインタフェース
 */
public interface ICollectionView {

    /**
     * @serial コールバック定義
     */
    interface Callbacks {

        /**
         * スクロール開始
         *
         * @param view 一覧表示
         */
        void onScroll(ICollectionView view);

        /**
         * スクロール終了
         *
         * @param view 一覧表示
         */
        void onScrollFinished(ICollectionView view);
    }
}
