package jp.osaka.cherry.addressbook.ui.files;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import jp.osaka.cherry.addressbook.Config;
import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.ISimpleCollectionView;
import jp.osaka.cherry.addressbook.ui.view.BaseAdmobActivity;
import jp.osaka.cherry.addressbook.databinding.ActivityFileSearchBinding;
import jp.osaka.cherry.addressbook.utils.FileHelper;
import jp.osaka.cherry.addressbook.service.SimpleAsset;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.R.drawable.search_frame;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_FILE_NAME;
import static jp.osaka.cherry.addressbook.utils.FileHelper.readFile;
import static jp.osaka.cherry.addressbook.utils.FileHelper.toListOf;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.toCSV;


/**
 * サーチアクティビティ
 */
public class SearchActivity extends BaseAdmobActivity implements
        ISimpleCollectionView.Callbacks<SimpleFile> {

    /**
     * @serial 目印
     */
    private final String TAG = "SearchActivity";

    /**
     * @serial 自身
     */
    private Activity mSelf;

    /**
     * @serial データセット
     */
    private final ArrayList<SimpleFile> mDataSet = new ArrayList<>();

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial バインディング
     */
    private ActivityFileSearchBinding mBinding;

    /**
     * @serial 一覧表示
     */
    private ISimpleCollectionView<SimpleFile> mCollectionView;

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

        // レイアウト設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_file_search);


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
                // 一覧表示更新
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
    protected void onResume() {
        super.onResume();
        mHandler.post(() -> {
            String[] fileList = mSelf.fileList();
            ArrayList<SimpleFile> list = new ArrayList<>();
            for (String filename : fileList) {
                if (filename.endsWith(".csv")) {
                    String name = filename.replace(".csv", "");
                    java.io.File out = mSelf.getFileStreamPath(name);
                    SimpleFile file = new SimpleFile();
                    file.name = name;
                    file.date = out.lastModified();
                    list.add(file);
                }
            }
            mDataSet.clear();
            mDataSet.addAll(list);
            // 一覧表示更新
            updateView(toListOf(mDataSet, mBinding.editSearch.getText().toString()));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // 識別子ごとの処理
        if (id == android.R.id.home) {
            finishActivity();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * 画面終了
     */
    private void finishActivity() {
        Intent intent = getIntent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
    }

    /**
     * 表示更新
     *
     * @param collection 一覧
     */
    private void updateView(ArrayList<SimpleFile> collection) {
        // 空表示の更新
        updateEmptyView(collection);
        // 一覧表示の更新
        updateCollectionView(collection);
    }

    /**
     * 空表示の更新
     *
     * @param collection 一覧
     */
    private void updateEmptyView(List<SimpleFile> collection) {
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
     * 一覧表示の更新
     *
     * @param collection 一覧
     */
    private void updateCollectionView(ArrayList<SimpleFile> collection) {
        mCollectionView = ListFragment.newInstance(collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * 項目のポップアップメニュー選択
     *
     * @param collectionView 一覧表示
     * @param view           項目表示
     * @param item           項目
     */
    @Override
    public void onSelectedMore(ISimpleCollectionView<SimpleFile> collectionView, View view, SimpleFile item) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelected(ISimpleCollectionView<SimpleFile> collectionView, final View view, final SimpleFile item) {
        // We need to post a Runnable to show the file_selected_one to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(() -> showPopupMenu(view, item));
    }

    /**
     * ポップアップ表示
     *
     * @param view 表示
     * @param item 項目
     */
    // BEGIN_INCLUDE(show_popup)
    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View view, final SimpleFile item) {
        // Create a PopupMenu, giving it the clicked view for an anchor
        final PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.file_selected_one, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_open: {
                    // 結果通知
                    Intent intent = getIntent();
                    if (Config.LOG_D) {
                        Log.d(TAG, item.name);
                    }
                    intent.putExtra(EXTRA_FILE_NAME, item.name);
                    setIntent(intent);
                    setResult(RESULT_OK, intent);
                    finish();
                    return true;
                }
                case R.id.menu_delete: {
                    FileHelper.deleteFile(mSelf, item.name);
                    if (mCollectionView != null) {
                        mCollectionView.remove(item);
                    }
                    mHandler.post(() -> {
                        String[] fileList = mSelf.fileList();
                        ArrayList<SimpleFile> list = new ArrayList<>();
                        for (String filename : fileList) {
                            if (filename.endsWith(".csv")) {
                                String name = filename.replace(".csv", "");
                                java.io.File out = mSelf.getFileStreamPath(name);
                                SimpleFile file = new SimpleFile();
                                file.name = name;
                                file.date = out.lastModified();
                                list.add(file);
                            }
                        }
                        mDataSet.clear();
                        mDataSet.addAll(list);
                        // 空表示の更新
                        updateEmptyView(mDataSet);
                        // 一覧表示の更新
                        //updateCollectionView(mDataSet);
                    });
                    return true;
                }
                case R.id.menu_share: {
                    // IntentBuilder をインスタンス化
                    ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(mSelf);
                    // データをセットする
                    ArrayList<SimpleAsset> list = readFile(mSelf, item.name);
                    builder.setSubject(item + ".csv");
                    builder.setText(toCSV(list));
                    builder.setType("text/plain");
                    // Intent を起動する
                    builder.startChooser();
                    return true;
                }
            }
            return false;
        });

        // Finally show the PopupMenu
        popup.show();
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
}
