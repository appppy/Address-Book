package jp.osaka.cherry.addressbook.ui.files;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.cherry.addressbook.service.SimpleAsset;


/**
 * ヘルパ
 */
public class DetailListHelper {

    /**
     * 連絡先のコンテンツの一覧取得
     *
     * @param collection 一覧
     * @return 連絡先一覧
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
     * 項目に変更
     *
     * @param asset アセット
     * @return アセット
     */
    public static SimpleAsset toItem(SimpleAsset asset) {
        asset.title = asset.displayName;
        asset.subtitle = DateFormat.getDateTimeInstance().format(asset.creationDate);
        return asset;
    }
}
