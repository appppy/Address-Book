package jp.osaka.cherry.addressbook.ui.search;

import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.cherry.addressbook.service.SimpleAsset;


/**
 * ヘルパ
 */
class SearchHelper {
    /**
     * メール一覧取得
     *
     * @param collection 一覧
     * @return メール一覧
     */
    public static ArrayList<SimpleAsset> toList(Collection<SimpleAsset> collection) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        for (SimpleAsset asset : collection) {
            switch (asset.content) {
                case ARCHIVE:
                case TRASH: {
                    break;
                }
                default: {
                    result.add(toItem(asset));
                    break;
                }
            }
        }
        return result;
    }


    /**
     * 取得
     *
     * @param asset アセット
     */
    public static SimpleAsset toItem(SimpleAsset asset) {
        asset.title = asset.displayName;
        asset.subtitle = asset.note;
        return asset;
    }
}
