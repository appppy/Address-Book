package jp.osaka.cherry.addressbook.ui.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.osaka.cherry.addressbook.Config;
import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.ISimpleCollectionView;
import jp.osaka.cherry.addressbook.databinding.ActivityFileBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.ui.State;
import jp.osaka.cherry.addressbook.ui.view.BaseAdmobActivity;
import jp.osaka.cherry.addressbook.utils.FileHelper;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.DETAIL_ITEM;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_FILE_NAME;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.toCSV;
import static jp.osaka.cherry.addressbook.utils.FileHelper.readFile;
import static jp.osaka.cherry.addressbook.utils.FileHelper.toSortByDateModifiedCollection;
import static jp.osaka.cherry.addressbook.utils.FileHelper.toSortByNameFileCollection;

/**
 * メインアクティビティ
 */
public class MainActivity extends BaseAdmobActivity implements
        ISimpleCollectionView.Callbacks<SimpleFile> {

    /**
     * @serial 目印
     */
    private final String TAG = "MainActivity";

    /**
     * @serial 自身
     */
    private MainActivity mSelf;

    /**
     * @serial バインディング
     */
    private ActivityFileBinding mBinding;

    /**
     * @serial データセット
     */
    private final ArrayList<SimpleFile> mDataSet = new ArrayList<>();

    /**
     * @serial 一覧表示
     */
    private ISimpleCollectionView<SimpleFile> collectionView;

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial 状態
     */
    private final State mState = new State();

    /**
     * インテントの生成
     *
     * @param context コンテキスト
     * @return インテント
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
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
        //setTheme(R.style.AppTheme_BlueGrey);

        // レイアウト設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_file);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

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

        // レジューム状態の設定
        mState.setResumed(true);

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
            updateView(mDataSet);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        // レジューム状態の設定
        mState.setResumed(false);

        super.onPause();
    }

    /**
     * 一覧表示の更新
     *
     * @param collection 一覧
     */
    private void updateView(ArrayList<SimpleFile> collection) {
        // プログレスバーの更新
        disableProgressBar();
        // 空表示の更新
        updateEmptyView(collection);
        // 一覧表示更新
        updateCollectionView(collection);
        // メニューの更新
        updateMenu();
        // タイトル更新
        updateTitle(collection);
    }

    /**
     * メニューの更新
     */
    private void updateMenu() {
        invalidateOptionsMenu();
    }

    /**
     * プログレスバーの非表示
     */
    private void disableProgressBar() {
        ProgressBar bar = mBinding.productImageLoading;
        bar.setVisibility(View.INVISIBLE);
    }

    /**
     * 空表示の更新
     *
     * @param collection 一覧
     */
    private void updateEmptyView(List<SimpleFile> collection) {
        // 空表示の更新
        ImageView view = mBinding.emptyView;
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            view.setVisibility(View.VISIBLE);
        } else {

            view.setVisibility(View.GONE);
        }
    }

    /**
     * 一覧表示の更新
     *
     * @param collection 一覧
     */
    private void updateCollectionView(ArrayList<SimpleFile> collection) {
        // 一覧表示の取得
        collectionView = getCollectionView(collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) collectionView)
                .commit();
    }

    /**
     * タイトルの更新
     *
     * @param collection 一覧
     */
    private void updateTitle(ArrayList<SimpleFile> collection) {
        StringBuilder sb = new StringBuilder();
        if (collection.isEmpty()) {
            sb.append(this.getString(R.string.file));
        } else {
            sb.append(this.getString(R.string.file)).append("  ").append(collection.size());
        }
        mBinding.toolbar.setTitle(sb.toString());
        sb.delete(0, sb.length());
    }

    /**
     * 一覧表示の取得
     *
     * @param collection 一覧
     * @return 一覧表示
     */
    private ISimpleCollectionView<SimpleFile> getCollectionView(ArrayList<SimpleFile> collection) {
        return ListFragment.newInstance(collection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedMore(ISimpleCollectionView<SimpleFile> collectionView, final View view, final SimpleFile item) {
        // We need to post a Runnable to show the file_selected_one to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(() -> showPopupMenu(view, item));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelected(final ISimpleCollectionView<SimpleFile> collectionView, final View view, final SimpleFile item) {

        // レジューム状態の確認
        if(!mState.isResumed()) {
            return;
        }

        // アセットを取得
        ArrayList<SimpleAsset> assets = readFile(mSelf, item.name + ".csv");
        if (Build.VERSION.SDK_INT >= 21) {
            ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                    view, 0, 0, view.getWidth(), view.getHeight());
            Intent intent = DetailActivity.createIntent(this, assets, item.name);
            ActivityCompat.startActivityForResult(this, intent, DETAIL_ITEM.ordinal(), opts.toBundle());
        } else {
            Intent intent = DetailActivity.createIntent(this, assets, item.name);
            startActivityForResult(intent, DETAIL_ITEM.ordinal());
        }
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
                    if (collectionView != null) {
                        collectionView.remove(item);
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
                        // メニューの更新
                        updateMenu();
                        // タイトルの更新
                        updateTitle(mDataSet);
                    });
                    return true;
                }
                case R.id.menu_share: {
                    // IntentBuilder をインスタンス化
                    ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(MainActivity.this);
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
    // END_INCLUDE(show_popup)


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDataSet.isEmpty()) {
            getMenuInflater().inflate(R.menu.file_empty, menu);
        } else if(mDataSet.size() == 1) {
            getMenuInflater().inflate(R.menu.file_linear_one, menu);
        } else {
            getMenuInflater().inflate(R.menu.file_linear, menu);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // 識別子ごとの処理
        switch (id) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.menu_by_name: {
                ArrayList<SimpleFile> collection = (ArrayList<SimpleFile>) toSortByNameFileCollection(mDataSet);
                updateView(collection);
                return true;
            }
            case R.id.menu_by_date_modified: {
                ArrayList<SimpleFile> collection = (ArrayList<SimpleFile>) toSortByDateModifiedCollection(mDataSet);
                Collections.reverse(collection);
                updateView(collection);
                return true;
            }
            case R.id.menu_swap_vert: {
                Collections.reverse(mDataSet);
                updateView(mDataSet);
                return true;
            }
            case R.id.menu_search: {
                Intent intent = SearchActivity.createIntent(getApplicationContext());
                startActivity(intent);
                overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
