package jp.osaka.cherry.addressbook.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.constants.ActivityTransition;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.ui.search.SearchActivity;
import jp.osaka.cherry.addressbook.ui.view.activity.AddressActivity;
import jp.osaka.cherry.addressbook.ui.view.activity.ArchiveActivity;
import jp.osaka.cherry.addressbook.ui.view.activity.CallActivity;
import jp.osaka.cherry.addressbook.ui.view.activity.PlaceActivity;
import jp.osaka.cherry.addressbook.ui.view.activity.TrashActivity;
import jp.osaka.cherry.addressbook.ui.view.activity.UrlActivity;
import jp.osaka.cherry.addressbook.ui.view.detail.DetailActivity;
import jp.osaka.cherry.addressbook.ui.view.detail.ImageCreateActivity;
import jp.osaka.cherry.addressbook.ui.view.detail.ImageDetailActivity;
import jp.osaka.cherry.addressbook.ui.view.detail.ImageEditActivity;
import jp.osaka.cherry.addressbook.ui.view.detail.NoteDetailActivity;
import jp.osaka.cherry.addressbook.ui.view.timeline.TimelineActivity;

import static jp.osaka.cherry.addressbook.constants.ActivityTransition.CREATE_ITEM;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.DETAIL_ITEM;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.EDIT_ITEM;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;


/**
 * アクティビティヘルパ
 */
public class ActivityHelper {

    /**
     * 詳細画面の開始
     *
     * @param v    表示
     * @param item 項目
     */
    public static void startDetailActivity_from_Line(Activity activity, Context context, View v, SimpleAsset item) {
        if (item.imagePath.equals(INVALID_STRING_VALUE)) {
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight());
                Intent intent = DetailActivity.createIntent(context, item);
                ActivityCompat.startActivityForResult(activity, intent, DETAIL_ITEM.ordinal(), opts.toBundle());
            } else {
                Intent intent = DetailActivity.createIntent(context, item);
                activity.startActivityForResult(intent, DETAIL_ITEM.ordinal());
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight());
                Intent intent = ImageDetailActivity.createIntent(context, item);
                ActivityCompat.startActivityForResult(activity, intent, DETAIL_ITEM.ordinal(), opts.toBundle());
            } else {
                Intent intent = ImageDetailActivity.createIntent(context, item);
                activity.startActivityForResult(intent, DETAIL_ITEM.ordinal());
            }
        }
    }

    /**
     * 詳細画面の開始
     *
     * @param v    表示
     * @param item 項目
     */
    public static void startDetailActivity_from_Module(Activity activity, Context context, View v, SimpleAsset item) {
        if (item.imagePath.equals(INVALID_STRING_VALUE)) {
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight());
                Intent intent = DetailActivity.createIntent(context, item);
                ActivityCompat.startActivityForResult(activity, intent, DETAIL_ITEM.ordinal(), opts.toBundle());
            } else {
                Intent intent = DetailActivity.createIntent(context, item);
                activity.startActivityForResult(intent, DETAIL_ITEM.ordinal());
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight());
                Intent intent = ImageDetailActivity.createIntent(context, item);
                ActivityCompat.startActivityForResult(activity, intent, DETAIL_ITEM.ordinal(), opts.toBundle());
            } else {
                Intent intent = ImageDetailActivity.createIntent(context, item);
                activity.startActivityForResult(intent, DETAIL_ITEM.ordinal());
            }
        }
    }

    /**
     * 詳細画面の開始
     *
     * @param item 項目
     */
    public static void startDetailActivity_from_Line(Activity activity, Context context, SimpleAsset item) {
        Intent intent;
        if (item.imagePath.equals(INVALID_STRING_VALUE)) {
            intent = DetailActivity.createIntent(context, item);
        } else {
            intent = ImageDetailActivity.createIntent(context, item);
        }
        activity.startActivityForResult(intent, DETAIL_ITEM.ordinal());
    }

    /**
     * 詳細画面の開始
     *
     * @param item 項目
     */
    public static void startDetailActivity_from_Module(Activity activity, Context context, SimpleAsset item) {
        Intent intent;
        if (item.imagePath.equals(INVALID_STRING_VALUE)) {
            intent = DetailActivity.createIntent(context, item);
        } else {
            intent = ImageDetailActivity.createIntent(context, item);
        }
        activity.startActivityForResult(intent, DETAIL_ITEM.ordinal());
    }

    /**
     * 詳細画面の開始
     *
     * @param item 項目
     */
    public static void startNoteDetailActivity(Activity activity, Context context, SimpleAsset item) {
        Intent intent = NoteDetailActivity.createIntent(context, item);
        activity.startActivityForResult(intent, DETAIL_ITEM.ordinal());
    }

    /**
     * 編集画面の開始
     *
     * @param v    表示
     * @param item 項目
     */
    public static void startEditActivity(Activity activity, Context context, View v, SimpleAsset item) {
        if (Build.VERSION.SDK_INT >= 21) {
            ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                    v, 0, 0, v.getWidth(), v.getHeight());
            Intent intent = ImageEditActivity.createIntent(context, item);
            ActivityCompat.startActivityForResult(activity, intent, EDIT_ITEM.ordinal(), opts.toBundle());
        } else {
            Intent intent = ImageEditActivity.createIntent(context, item);
            activity.startActivityForResult(intent, EDIT_ITEM.ordinal());
        }
    }

    /**
     * 編集画面の開始
     *
     * @param item 項目
     */
    public static void startEditActivity(Activity activity, Context context, SimpleAsset item) {
        Intent intent = ImageEditActivity.createIntent(context, item);
        activity.startActivityForResult(intent, EDIT_ITEM.ordinal());
    }

    /**
     * 編集画面の開始
     *
     * @param v    表示
     * @param item 項目
     */
    public static void startCreateActivity(Activity activity, Context context, View v, SimpleAsset item) {
        if (Build.VERSION.SDK_INT >= 21) {
            ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                    v, 0, 0, v.getWidth(), v.getHeight());
            Intent intent = ImageCreateActivity.createIntent(context, item);
            ActivityCompat.startActivityForResult(activity, intent, CREATE_ITEM.ordinal(), opts.toBundle());
        } else {
            Intent intent = ImageCreateActivity.createIntent(context, item);
            activity.startActivityForResult(intent, CREATE_ITEM.ordinal());
        }
    }

    /**
     * フォルダ画面の開始
     *
     * @param activity 画面
     */
    public static void startFolderActivity(Activity activity) {
        try {
            Intent intent = jp.osaka.cherry.addressbook.ui.files.MainActivity.createIntent(activity);
            activity.startActivityForResult(intent, ActivityTransition.OPEN_FILE.ordinal());
            activity.overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 検索画面の開始
     *
     * @param activity アクティビティ
     */
    public static void startSearchActivity(Activity activity) {
        try {
            Intent intent = SearchActivity.createIntent(activity);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 履歴画面の開始
     *
     * @param activity アクティビティ
     */
    public static void startHistoryActivity(Activity activity) {
        try {
            Intent intent = jp.osaka.cherry.addressbook.ui.history.MainActivity.createIntent(activity);
            activity.startActivityForResult(intent, ActivityTransition.OPEN_HISTORY.ordinal());
            activity.overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 開始アクティビティの取得
     *
     * @param id 識別子
     * @return 開始アクティビティ
     */
    @SuppressLint("NonConstantResourceId")
    public static Class<?> getStartActivity(int id) {
        Class<?> c = AddressActivity.class;

        switch (id) {
            case R.id.address: {
                c = AddressActivity.class;
                break;
            }
            case R.id.call: {
                c = CallActivity.class;
                break;
            }
            case R.id.url: {
                c = UrlActivity.class;
                break;
            }
            case R.id.archive: {
                c = ArchiveActivity.class;
                break;
            }
            case R.id.trash: {
                c = TrashActivity.class;
                break;
            }
            case R.id.timeline: {
                c = TimelineActivity.class;
                break;
            }
            case R.id.place: {
                c = PlaceActivity.class;
                break;
            }
            default: {
                break;
            }
        }
        return c;
    }
}
