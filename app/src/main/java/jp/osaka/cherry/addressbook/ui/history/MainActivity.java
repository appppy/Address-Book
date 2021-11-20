package jp.osaka.cherry.addressbook.ui.history;


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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.ISimpleCollectionView;
import jp.osaka.cherry.addressbook.databinding.ActivityHistoryBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.service.history.History;
import jp.osaka.cherry.addressbook.service.history.HistoryClient;
import jp.osaka.cherry.addressbook.ui.State;
import jp.osaka.cherry.addressbook.ui.view.BaseAdmobActivity;
import jp.osaka.cherry.addressbook.utils.AssetHelper;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.DETAIL_ITEM;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_HISTORY;

/**
 * メインアクティビティ
 */
public class MainActivity extends BaseAdmobActivity implements
        ISimpleCollectionView.Callbacks<History>,
        HistoryClient.Callbacks {

    /**
     * @serial バインディング
     */
    private ActivityHistoryBinding mBinding;

    /**
     * @serial データセット
     */
    private final ArrayList<History> mDataSet = new ArrayList<>();

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial クライアント
     */
    private final HistoryClient mClient = new HistoryClient(this, this);

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
        String TAG = "MainActivity";
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // 自身の取得

        // テーマの設
        setTheme(R.style.AppTheme_BlueGrey);

        // レイアウト設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_history);

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

        // サービスの接続
        mClient.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        // レジューム状態の設定
        mState.setResumed(false);

        // サービスの非接続
        mClient.disconnect();

        super.onPause();
    }

    /**
     * 表示の更新
     *
     * @param collection 一覧
     */
    private void updateView(ArrayList<History> collection) {
        // プログレスバーの更新
        updateProgressBar();
        // 空表示の更新
        updateEmptyView(collection);
        // 一覧表示の更新
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
     * プログレスバーの更新
     *
     */
    private void updateProgressBar() {
        ProgressBar bar = mBinding.productImageLoading;
        bar.setVisibility(View.INVISIBLE);
    }

    /**
     * 空表示の更新
     *
     * @param collection 一覧
     */
    private void updateEmptyView(List<History> collection) {
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
    private void updateCollectionView(ArrayList<History> collection) {
        // 一覧表示の取得
        ISimpleCollectionView<History> collectionView = getCollectionView(collection);
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
    private void updateTitle(ArrayList<History> collection) {
        StringBuilder sb = new StringBuilder();
        if (collection.isEmpty()) {
            sb.append(this.getString(R.string.history));
        } else {
            sb.append(this.getString(R.string.history)).append("  ").append(collection.size());
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
    private ISimpleCollectionView<History> getCollectionView(ArrayList<History> collection) {
        return ListFragment.newInstance(collection);
    }

    /**
     * ポップアップ表示
     *
     * @param view 表示
     * @param item 項目
     */
    // BEGIN_INCLUDE(show_popup)
    private void showPopupMenu(View view, final History item) {

        // Create a PopupMenu, giving it the clicked view for an anchor
        final PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.history_more, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.menu_open) {// 結果通知
                Intent intent = getIntent();
                intent.putExtra(EXTRA_HISTORY, item);
                setIntent(intent);
                setResult(RESULT_OK, intent);
                finish();
                return true;
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
            getMenuInflater().inflate(R.menu.history_empty, menu);
        } else if(mDataSet.size() == 1) {
            getMenuInflater().inflate(R.menu.history_linear_one, menu);
        } else {
            getMenuInflater().inflate(R.menu.history_linear, menu);
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
            case R.id.menu_swap_vert: {
                Collections.reverse(mDataSet);
                updateView(mDataSet);
                return true;
            }
            case R.id.menu_empty: {
                mClient.clear();
                mDataSet.clear();
                updateView(mDataSet);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 更新日でソートした一覧の取得
     *
     * @param collection 一覧
     */
    static void toSortByDateModifiedCollection(Collection<History> collection) {
        Collections.sort((List<History>) collection, (lhs, rhs) -> (int) (lhs.date - rhs.date));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedMore(ISimpleCollectionView<History> collectionView, final View view, final History item) {
        // We need to post a Runnable to show the file_selected_one to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(() -> showPopupMenu(view, item));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelected(ISimpleCollectionView<History> collectionView, final View view, final History item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return;
        }

        // アセットを取得
        ArrayList<SimpleAsset> assets = AssetHelper.toAssets(item.message);
        String name = DateFormat.getDateInstance().format(new Date(item.date)) + " " + item.title + " items";
        if (Build.VERSION.SDK_INT >= 21) {
            ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                    view, 0, 0, view.getWidth(), view.getHeight());
            Intent intent = DetailActivity.createIntent(this, assets, name);
            ActivityCompat.startActivityForResult(this, intent, DETAIL_ITEM.ordinal(), opts.toBundle());
        } else {
            Intent intent = DetailActivity.createIntent(this, assets, name);
            startActivityForResult(intent, DETAIL_ITEM.ordinal());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdatedHistoryList(Object object, final List<History> historyList) {
        mHandler.post(() -> {
            try {
                boolean result;
                result = mDataSet.isEmpty() || mDataSet.size() != historyList.size() || !mDataSet.equals(historyList);
                if (result) {
                    mDataSet.clear();
                    mDataSet.addAll(historyList);
                    toSortByDateModifiedCollection(mDataSet);
                    updateView(mDataSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
