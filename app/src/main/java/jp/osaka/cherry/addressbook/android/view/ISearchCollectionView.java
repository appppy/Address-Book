package jp.osaka.cherry.addressbook.android.view;

/**
 * 検索一覧表示のインタフェース
 */
public interface ISearchCollectionView<T> {

    /**
     * 項目挿入
     *
     * @param item 項目
     */
    void insert(int index, T item);

    /**
     * 項目削除
     *
     * @param item 項目
     * @return 削除した項目位置
     */
    int remove(T item);

    /**
     * 項目変更
     *
     * @param item 項目
     * @return 変更前の項目位置
     */
    int change(T item);

}
