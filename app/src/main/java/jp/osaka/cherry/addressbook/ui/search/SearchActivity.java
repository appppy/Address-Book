package jp.osaka.cherry.addressbook.ui.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.ISearchCollectionCallbacks;
import jp.osaka.cherry.addressbook.android.view.ISearchCollectionView;
import jp.osaka.cherry.addressbook.databinding.ActivitySearchBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.service.SimpleClient;
import jp.osaka.cherry.addressbook.ui.Redo;
import jp.osaka.cherry.addressbook.ui.Undo;
import jp.osaka.cherry.addressbook.ui.view.BaseAdmobActivity;
import jp.osaka.cherry.addressbook.ui.view.activity.AddressActivity;

import static jp.osaka.cherry.addressbook.Config.LOG_D;
import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.R.drawable.search_frame;
import static jp.osaka.cherry.addressbook.constants.ACTION.CHANGE;
import static jp.osaka.cherry.addressbook.constants.ACTION.INSERT;
import static jp.osaka.cherry.addressbook.constants.ACTION.MODIFY;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.DETAIL_ITEM;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SIMPLE_ASSET;
import static jp.osaka.cherry.addressbook.service.SimpleAsset.CONTENT.ARCHIVE;
import static jp.osaka.cherry.addressbook.service.SimpleAsset.CONTENT.TRASH;
import static jp.osaka.cherry.addressbook.ui.search.SearchHelper.toItem;
import static jp.osaka.cherry.addressbook.ui.search.SearchHelper.toList;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startDetailActivity_from_Line;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startEditActivity;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startNoteDetailActivity;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.copy;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.getAsset;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.isModified;


/**
 * サーチアクティビティ
 */
public class SearchActivity extends BaseAdmobActivity implements
        SimpleClient.Callbacks,
        ISearchCollectionCallbacks<SimpleAsset> {

    /**
     * @serial 目印
     */
    private final String TAG = "SearchActivity";

    /**
     * @serial 自身
     */
    private Activity mSelf;

    /**
     * @serial バインディング
     */
    private ActivitySearchBinding mBinding;

    /**
     * @serial データセット
     */
    private final ArrayList<SimpleAsset> mDataSet = new ArrayList<>();

    /**
     * @serial バックアップ
     */
    private final ArrayList<SimpleAsset> mBackup = new ArrayList<>();

    /**
     * @serial クライアント
     */
    private final SimpleClient mClient = new SimpleClient(this, this);

    /**
     * @serial 戻る
     */
    private final ArrayList<Undo> mUndos = new ArrayList<>();

    /**
     * @serial 進む
     */
    private final ArrayList<Redo> mRedos = new ArrayList<>();

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial 一覧表示
     */
    private ISearchCollectionView<SimpleAsset> mCollectionView;

    /**
     * インテントの生成
     *
     * @param context コンテキスト
     * @return インテント
     */
    public static Intent createIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SearchActivity.class);
        return intent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // 自身の取得
        mSelf = this;

        // テーマの設定
        setTheme(R.style.AppTheme_Light);

        // レイアウト設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        setSupportActionBar(mBinding.toolbar);
        initActionBar();

        mBinding.editSearch.setInputType(InputType.TYPE_CLASS_TEXT);
        mBinding.editSearch.addTextChangedListener(new TextWatcher() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 処理なし
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 一覧表示の更新
                updateView(toListOf(mDataSet, charSequence.toString()));
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void afterTextChanged(Editable editable) {
                // 処理なし
            }
        });

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdated(Object object, final List<SimpleAsset> assets) {
        mHandler.post(() -> {
            boolean result;
            result = mDataSet.isEmpty() || mDataSet.size() != assets.size() || !mDataSet.equals(assets);
            if (result) {
                // データ設定
                mDataSet.clear();
                mDataSet.addAll(assets);
                // データ更新
                updateView(toListOf(mDataSet, mBinding.editSearch.getText().toString()));

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mBinding.editSearch, 0);
            }

            // 次の動作を指定
            for (Redo doAction : mRedos) {
                if (doAction.action.equals(MODIFY)) {
                    SimpleAsset dest = getAsset(doAction.object.uuid, mDataSet);
                    if (!dest.equals(doAction.object)) {
                        switch (doAction.object.content) {
                            case TRASH: {
                                TrashRunner runner = new TrashRunner(doAction.object);
                                mHandler.post(runner);
                                break;
                            }
                            case ARCHIVE: {
                                ArchiveRunner runner = new ArchiveRunner(doAction.object);
                                mHandler.post(runner);
                                break;
                            }
                            default: {
                                if (isModified(dest, doAction.object)) {
                                    ModifyRunner runner = new ModifyRunner(doAction.object);
                                    mHandler.post(runner);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            mRedos.clear();
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#enter");
        }

        // 結果確認
        if (requestCode == DETAIL_ITEM.ordinal()) {
            if (resultCode == RESULT_OK) {
                // データの取得
                Bundle bundle = data.getExtras();
                SimpleAsset item = Objects.requireNonNull(bundle).getParcelable(EXTRA_SIMPLE_ASSET);
                // レジューム後に動作させる
                mRedos.clear();
                mRedos.add(new Redo(MODIFY, item));
            }
        }
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        // サービスの接続
        mClient.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        // サービスの非接続
        mClient.disconnect();
        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // 識別子ごとの処理
        if (id == android.R.id.home) {
            finishActivity();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 画面終了
     */
    private void finishActivity() {
        Intent intent = getIntent();
        intent.setClass(this, AddressActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
    }

    /**
     * 画面表示更新
     *
     * @param collection 一覧
     */
    private void updateView(ArrayList<SimpleAsset> collection) {
        // 空表示の更新
        updateEmptyView(collection);
        // 一覧表示の更新
        updateCollectionView(collection);
    }

    /**
     * 空表示更新
     *
     * @param collection 一覧
     */
    private void updateEmptyView(List<SimpleAsset> collection) {
        // 空表示の更新
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            // 空の場合
            mBinding.emptyView.setVisibility(View.VISIBLE);
        } else {
            // 空でない場合
            mBinding.emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * 一覧表示更新
     *
     * @param collection 一覧
     */
    private void updateCollectionView(ArrayList<SimpleAsset> collection) {
        mCollectionView = SearchListFragment.newInstance(collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedMore(ISearchCollectionView<SimpleAsset> collectionView, final View view, final SimpleAsset item) {
        // We need to post a Runnable to show the file_selected_one to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(() -> showPopupMenu(view, item));
    }

    /**
     * ポップアップ表示
     *
     * @param view 画面表示
     * @param item 項目
     */
    // BEGIN_INCLUDE(show_popup)
    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(final View view, final SimpleAsset item) {

        // Create a PopupMenu, giving it the clicked view for an anchor
        final PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.address_search_more, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_edit_note: {
                    // アセットの取得
                    SimpleAsset asset = getAsset(item.uuid, mDataSet);
                    // タイムスタンプを保存
                    asset.timestamp = System.currentTimeMillis();
                    mClient.setList(mDataSet);
                    startNoteDetailActivity(mSelf, mSelf, asset);
                    return true;
                }
                case R.id.menu_info: {
                    // アセットの取得
                    SimpleAsset asset = getAsset(item.uuid, mDataSet);
                    // タイムスタンプを保存
                    asset.timestamp = System.currentTimeMillis();
                    mClient.setList(mDataSet);
                    startDetailActivity_from_Line(mSelf, mSelf, asset);
                    return true;
                }
                case R.id.menu_edit: {
                    // アセットの取得
                    SimpleAsset asset = getAsset(item.uuid, mDataSet);
                    // タイムスタンプを保存
                    asset.timestamp = System.currentTimeMillis();
                    mClient.setList(mDataSet);
                    startEditActivity(mSelf, mSelf, asset);
                    return true;
                }
                case R.id.menu_archive: {
                    // アセットの取得
                    SimpleAsset asset = getAsset(item.uuid, mDataSet);
                    // 選択したアーカイブの解除
                    asset.content = ARCHIVE;
                    mHandler.post(new ModifyRunner(asset));
                    return true;
                }
                case R.id.menu_trash: {
                    // アセットの取得
                    SimpleAsset asset = getAsset(item.uuid, mDataSet);
                    // 選択したアーカイブの解除
                    asset.content = TRASH;
                    mHandler.post(new ModifyRunner(asset));
                    return true;
                }
                case R.id.menu_share: {
                    // アセットの取得
                    SimpleAsset asset = getAsset(item.uuid, mDataSet);
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TITLE, asset.displayName);
                        intent.putExtra(Intent.EXTRA_SUBJECT, asset.displayName);
                        intent.putExtra(Intent.EXTRA_PHONE_NUMBER, asset.call);
                        //intent.putExtra(Intent.EXTRA_EMAIL, asset.send);
                        intent.putExtra(Intent.EXTRA_TEXT, asset.note);
                        intent.setType("text/plain");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                default: {
                    break;
                }
            }
            return false;
        });

        // Finally show the PopupMenu
        popup.show();
    }
    // END_INCLUDE(show_popup)


    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelected(ISearchCollectionView<SimpleAsset> collectionView, View view, SimpleAsset item) {
        // アセットを取得
        SimpleAsset asset = getAsset(item.uuid, mDataSet);

        // タイムスタンプを保存
        asset.timestamp = System.currentTimeMillis();

        // データ保存
        mClient.setList(mDataSet);

        // 詳細画面の表示
        startDetailActivity_from_Line(mSelf, mSelf, view, asset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdated(ISearchCollectionView<SimpleAsset> view, Collection<? extends SimpleAsset> collection) {
        // 処理なし
    }

    /**
     * アクションバー初期化
     */
    private void initActionBar() {
        // ToolBarの場合はもっとスマートなやり方があるかもしれません。
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(ContextCompat.getDrawable(this, search_frame));
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        finishActivity();
        super.onBackPressed();
    }

    /**
     * スナックバーの生成
     *
     * @param layout  レイアウト
     * @param message メッセージ
     */
    private void makeUndoSnackbar(CoordinatorLayout layout, String message) {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo), new View.OnClickListener() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onClick(View v) {
                        try {
                            // 一覧表示の更新
                            for (Undo undo : mUndos) {

                                // 選択状態は解除しておく
                                undo.object.isSelected = false;

                                switch (undo.action) {
                                    case INSERT: {
                                        mCollectionView.insert(undo.arg, toItem(undo.object));
                                        break;
                                    }
                                    case CHANGE: {
                                        mCollectionView.change(toItem(undo.object));
                                        break;
                                    }
                                    case REMOVE: {
                                        mCollectionView.remove(toItem(undo.object));
                                        break;
                                    }
                                }
                            }
                            mUndos.clear();

                            // データ取得
                            ArrayList<SimpleAsset> list = toList(mBackup);
                            // 空表示の更新
                            updateEmptyView(list);

                            // データ設定
                            mDataSet.clear();
                            copy(mDataSet, mBackup);

                            // 設定
                            mClient.setList(mDataSet);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    /**
     * 編集
     */
    private class ModifyRunner implements Runnable {

        /**
         * @serial データ
         */
        SimpleAsset mData;

        /**
         * コンストラクタ
         *
         * @param data データ
         */
        ModifyRunner(SimpleAsset data) {
            mData = data;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                // データ変更
                for (SimpleAsset dest : mDataSet) {
                    // 一致
                    if (dest.equal(mData)) {
                        dest.setParams(mData);
                    }
                }

                // 一覧表示の更新
                mUndos.clear();
                int position = mCollectionView.change(toItem(mData));
                mUndos.add(new Undo(CHANGE, position, getAsset(mData.uuid, mBackup)));
                Collections.reverse(mUndos);

                // 設定
                mClient.setList(mDataSet);

                // スナックバーの生成
                String message = getString(R.string.modified_item);
                makeUndoSnackbar(mBinding.coordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 検索したメール一覧の取得
     *
     * @param collection 一覧
     * @param src        検索文字
     * @return 検索したメール一覧
     */
    public static ArrayList<SimpleAsset> toListOf(ArrayList<SimpleAsset> collection, String src) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        if (src.isEmpty()) {
            return result;
        }
        for (SimpleAsset asset : collection) {
            switch (asset.content) {
                case ARCHIVE:
                case TRASH: {
                    break;
                }
                default: {
                    if (asset.displayName.contains(src)) {
                        result.add(toItem(asset));
                    } else if (asset.note.contains(src)) {
                        result.add(toItem(asset));
                    } else if (asset.place.contains(src)) {
                        result.add(toItem(asset));
                    } else if (asset.call.contains(src)) {
                        result.add(toItem(asset));
                    } else if (asset.url.contains(src)) {
                        result.add(toItem(asset));
                    //} else if (asset != null && (asset.send.contains(src))) {
                    //    result.add(toItem(asset));
                    }
                    break;
                }
            }
        }
        return result;
    }


    /**
     * アーカイブ
     */
    private class ArchiveRunner implements Runnable {

        /**
         * データ
         */
        List<SimpleAsset> mList;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        ArchiveRunner(SimpleAsset item) {
            mList = new ArrayList<>();
            mList.add(item);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                if(LOG_D) {
                    for (SimpleAsset dest : mDataSet) {
                        Log.d(TAG, "mData:" + dest.uuid);
                    }
                    for (SimpleAsset src : mList) {
                        Log.d(TAG, "mList:" + src.uuid);
                    }
                }

                // データの変更
                for (SimpleAsset dest : mDataSet) {
                    for (SimpleAsset src : mList) {
                        // 一致
                        if (dest.equal(src)) {
                            dest.content = ARCHIVE;
                        }
                    }
                }

                // 一覧表示の更新
                mUndos.clear();
                for (SimpleAsset src : mList) {
                    int position;
                    position = mCollectionView.remove(toItem(src));
                    mUndos.add(new Undo(INSERT, position, getAsset(src.uuid, mBackup)));
                }
                Collections.reverse(mUndos);

                // 設定
                mClient.setList(mDataSet);

                // クリア
                mList.clear();

                // スナックバーの生成
                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.moved_to_archive_item);
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.moved_to_archive_some_items, mUndos.size());
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ゴミ箱
     */
    private class TrashRunner implements Runnable {

        /**
         * データ
         */
        List<SimpleAsset> mList;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        TrashRunner(SimpleAsset item) {
            mList = new ArrayList<>();
            mList.add(item);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                // データの変更
                for (SimpleAsset data : mDataSet) {
                    for (SimpleAsset src : mList) {
                        // 一致
                        if (data.equal(src)) {
                            data.content = TRASH;
                        }
                    }
                }

                // 一覧表示の更新
                mUndos.clear();
                for (SimpleAsset src : mList) {
                    int position;
                    position = mCollectionView.remove(toItem(src));
                    mUndos.add(new Undo(INSERT, position, getAsset(src.uuid, mBackup)));
                }
                Collections.reverse(mUndos);

                // 設定
                mClient.setList(mDataSet);

                // クリア
                mList.clear();

                // スナックバーの生成
                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.moved_to_trash_item);
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.moved_to_trash_some_items, mUndos.size());
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
