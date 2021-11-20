package jp.osaka.cherry.addressbook.ui.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceManager;

import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.ICollectionView;
import jp.osaka.cherry.addressbook.constants.COLOR;
import jp.osaka.cherry.addressbook.constants.LAYOUT;
import jp.osaka.cherry.addressbook.databinding.ActivityTrashBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.service.SimpleClient;
import jp.osaka.cherry.addressbook.ui.Redo;
import jp.osaka.cherry.addressbook.ui.State;
import jp.osaka.cherry.addressbook.ui.Undo;
import jp.osaka.cherry.addressbook.ui.view.ListFragment;
import jp.osaka.cherry.addressbook.ui.view.ModuleFragment;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.ACTION.CHANGE;
import static jp.osaka.cherry.addressbook.constants.ACTION.INSERT;
import static jp.osaka.cherry.addressbook.constants.ACTION.MODIFY;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.DETAIL_ITEM;
import static jp.osaka.cherry.addressbook.constants.COLOR.AMBER;
import static jp.osaka.cherry.addressbook.constants.COLOR.BLUE;
import static jp.osaka.cherry.addressbook.constants.COLOR.BLUE_GREY;
import static jp.osaka.cherry.addressbook.constants.COLOR.BROWN;
import static jp.osaka.cherry.addressbook.constants.COLOR.DEEP_ORANGE;
import static jp.osaka.cherry.addressbook.constants.COLOR.DEEP_PURPLE;
import static jp.osaka.cherry.addressbook.constants.COLOR.GREEN;
import static jp.osaka.cherry.addressbook.constants.COLOR.INDIGO;
import static jp.osaka.cherry.addressbook.constants.COLOR.LIGHT_GREEN;
import static jp.osaka.cherry.addressbook.constants.COLOR.LIME;
import static jp.osaka.cherry.addressbook.constants.COLOR.ORANGE;
import static jp.osaka.cherry.addressbook.constants.COLOR.PINK;
import static jp.osaka.cherry.addressbook.constants.COLOR.PURPLE;
import static jp.osaka.cherry.addressbook.constants.COLOR.RED;
import static jp.osaka.cherry.addressbook.constants.COLOR.WHITE;
import static jp.osaka.cherry.addressbook.constants.COLOR.YELLOW;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_CONTENT;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LAYOUT;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SIMPLE_ASSET;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;
import static jp.osaka.cherry.addressbook.constants.LAYOUT.LINEAR;
import static jp.osaka.cherry.addressbook.constants.SELECTION.MULTI_SELECTED;
import static jp.osaka.cherry.addressbook.constants.SELECTION.SELECTED;
import static jp.osaka.cherry.addressbook.constants.SELECTION.SELECTED_ALL;
import static jp.osaka.cherry.addressbook.constants.SELECTION.SELECTING;
import static jp.osaka.cherry.addressbook.constants.SELECTION.UNSELECTED;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.getStartActivity;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startDetailActivity_from_Line;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startDetailActivity_from_Module;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.copy;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.getAsset;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.getSelectedCollection;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.isModified;

/**
 * ゴミ箱画面
 */
public class TrashActivity extends AppCompatActivity implements
        SimpleClient.Callbacks,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        ICollectionView.Callbacks<SimpleAsset>,
        View.OnClickListener,
        State.Callbacks {

    /**
     * @serial 状態
     */
    public State mState = new State();

    /**
     * @serial 目印
     */
    private final String TAG = "TrashActivity";

    /**
     * @serial 自身
     */
    private Activity mSelf;

    /**
     * @serial プリファレンス
     */
    private SharedPreferences mPref;

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial クライアント
     */
    private final SimpleClient mClient = new SimpleClient(this, this);

    /**
     * @serial データセット
     */
    private ArrayList<SimpleAsset> mDataSet = new ArrayList<>();

    /**
     * @serial バックアップ
     */
    private final ArrayList<SimpleAsset> mBackup = new ArrayList<>();

    /**
     * @serial 選択データ
     */
    private final ArrayList<SimpleAsset> mSelected = new ArrayList<>();

    /**
     * @serial 進む
     */
    private final ArrayList<Redo> mRedos = new ArrayList<>();

    /**
     * @serial 戻る
     */
    private final ArrayList<Undo> mUndos = new ArrayList<>();

    /**
     * @serial バインディング
     */
    private ActivityTrashBinding mBinding;

    /**
     * @serial トグル
     */
    private ActionBarDrawerToggle mToggle;

    /**
     * @serial 一覧画面表示
     */
    private ICollectionView<SimpleAsset> mCollectionView;

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
                updateView(toList(mDataSet));
                // ジオフェンス開始
                //addGeofencesHandler(mDataSet);
            }
            // 次の動作を指定
            for (Redo doAction : mRedos) {
                if (doAction.action.equals(MODIFY)) {
                    SimpleAsset dest = getAsset(doAction.object.uuid, mDataSet);
                    if (!dest.equals(doAction.object)) {
                        if (dest.content == SimpleAsset.CONTENT.TRASH) {
                            if (isModified(dest, doAction.object)) {
                                ModifyRunner runner = new ModifyRunner(doAction.object);
                                mHandler.post(runner);
                            } else {
                                ChangeRunner runner = new ChangeRunner(doAction.object);
                                mHandler.post(runner);
                            }
                        } else {
                            UnTrashRunner runner = new UnTrashRunner(doAction.object);
                            mHandler.post(runner);
                        }
                    }
                }
            }
            mRedos.clear();

            // 選択解除
            mState.changeSelection(UNSELECTED);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view) {
        // 選択解除
        mState.changeSelection(UNSELECTED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectChanged(State state) {
        mHandler.post(() -> {
            try {
                // 選択状態の取得
                switch (mState.getSelection()) {
                    case UNSELECTED: {
                        // 一覧画面表示が空の場合
                        if (mCollectionView != null) {
                            // アクションバーの設定
                            ActionBar bar = getSupportActionBar();
                            if (bar != null) {
                                bar.setDisplayHomeAsUpEnabled(false);
                            }

                            // 選択の解除
                            mCollectionView.diselect();

                            // ナビゲーションドローワーの設定
                            enableNavigationDrawer();

                            // ツールバーの変更
                            updateTitle(toList(mDataSet));
                            mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_600));
                            // ステータスバーの変更
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.grey_800));
                            }
                            // 背景色の変更
                            mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_800));

                            // メニューを設定
                            invalidateOptionsMenu();
                        }
                        break;
                    }
                    // 何もしない
                    case SELECTED_ALL: {
                        // 一覧画面表示が空の場合
                        if (mCollectionView != null) {

                            // ナビゲーションの設定解除
                            disableNavigationDrawer();

                            // アクションバーの設定
                            ActionBar bar = getSupportActionBar();
                            if (bar != null) {
                                bar.setDisplayHomeAsUpEnabled(true);
                            }

                            // 全選択
                            mSelected.clear();
                            mSelected.addAll(mCollectionView.selectedAll());

                            // ツールバーの設定
                            mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                            mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_600));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.grey_800));
                            }
                            // 背景色の変更
                            mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_800));

                            // メニュー更新
                            invalidateOptionsMenu();
                        }
                        break;
                    }
                    case MULTI_SELECTED:
                    case SELECTED: {
                        // ナビゲーションの設定解除
                        disableNavigationDrawer();

                        // アクションバーの設定
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(true);
                        }

                        // ツールバーの設定
                        mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.grey_800));
                        }
                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_800));

                        // メニュー更新
                        invalidateOptionsMenu();
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisplayChanged(State state) {
        // 処理なし
    }

    /**
     * 項目のポップアップメニュー選択
     *
     * @param collectionView 一覧画面表示
     * @param view           項目表示
     * @param item           項目
     */
    @Override
    public void onSelectedMore(ICollectionView<SimpleAsset> collectionView, final View view, final SimpleAsset item) {
        // We need to post a Runnable to show the file_selected_one to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(() -> showPopupMenu(view, item));
    }


    // BEGIN_INCLUDE(show_popup)
    private void showPopupMenu(final View view, final SimpleAsset item) {

        // Create a PopupMenu, giving it the clicked view for an anchor
        final PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.trash_selected_one_more, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {

            // 項目を選択する
            mSelected.clear();
            mSelected.add(item);

            // 項目のメニューを選択する
            onOptionsItemSelected(menuItem);

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
    public void onSelected(ICollectionView<SimpleAsset> collectionView, View view, SimpleAsset item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return;
        }

        // 詳細を表示する
        startDetailActivity_from_Line(this, this, view, getAsset(item.uuid, mDataSet));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedChanged(ICollectionView<SimpleAsset> collectionView, View view, SimpleAsset item, Collection<? extends SimpleAsset> collection) {
        // 選択数の変更
        mSelected.clear();
        mSelected.addAll(getSelectedCollection(collection));

        // 選択状態の確認
        if (mSelected.size() == collection.size()) {
            // 全選択
            if (!mState.changeSelection(SELECTED_ALL)) {
                // 遷移なしの場合
                // 選択数を変更
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() > 1) {
            // マルチ選択
            if (!mState.changeSelection(MULTI_SELECTED)) {
                // 遷移なしの場合
                // 選択数を変更
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() == 1) {
            // 選択
            if (!mState.changeSelection(SELECTED)) {
                // 遷移なしの場合
                // 選択数を変更
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            }
        } else {
            // 非選択
            mState.changeSelection(UNSELECTED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSwiped(ICollectionView<SimpleAsset> collectionView, SimpleAsset item) {
        // バックアップ
        mBackup.clear();
        copy(mBackup, mDataSet);

        // 一覧画面表示更新
        mUndos.clear();
        int position = mCollectionView.remove(toItem(item));
        mUndos.add(new Undo(INSERT, position, item));
        Collections.reverse(mUndos);

        // データの設定
        SimpleAsset asset = getAsset(item.uuid, mDataSet);
        asset.content = SimpleAsset.CONTENT.TRASH;
        mClient.setList(mDataSet);

        // スナックバーの生成
        String message = getString(R.string.restored_item);
        makeUndoSnackbar(mBinding.coordinatorLayout, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(ICollectionView<SimpleAsset> view) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollFinished(ICollectionView<SimpleAsset> view) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoveChanged(ICollectionView<SimpleAsset> view, Collection<? extends SimpleAsset> collection) {
        // キャストする
        ArrayList<SimpleAsset> list = new ArrayList<>(collection);

        // 選択状態を選択中とする
        mState.changeSelection(SELECTING);

        // アクションバーを設定する
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }

        // タイトルを更新する
        updateTitle(list);

        // ナビゲーションバーを有効にする
        enableNavigationDrawer();

        // メニューを更新する
        invalidateOptionsMenu();

        //データを設定する
        ArrayList<SimpleAsset> assets = new ArrayList<>();
        for (SimpleAsset item : list) {
            for (SimpleAsset asset : mDataSet) {
                if (asset.uuid.equals(item.uuid)) {
                    assets.add(asset);
                    break;
                }
            }
        }
        mDataSet.clear();
        copy(mDataSet, assets);
        mClient.setList(mDataSet);
    }

    /**
     * 更新通知
     *
     * @param view       画面表示
     * @param collection 一覧
     */
    @Override
    public void onUpdated(ICollectionView<SimpleAsset> view, Collection<? extends SimpleAsset> collection) {
        // キャスト
        ArrayList<SimpleAsset> arrayList = new ArrayList<>(collection);
        // 空画面表示の更新
        updateEmptyView(arrayList);
        // タイトル更新
        updateTitle(arrayList);
        // メニューの更新
        updateMenu();
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

        // プリファレンスの設定
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        // テーマの設定
        setTheme(R.style.AppTheme_Grey);

        // レイアウトの設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_trash);

        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.trash);
        }
        // ナビゲーションの設定
        enableNavigationDrawer();

        // 登録
        mState.setId(R.id.trash);
        mState.registerCallacks(this);
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
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        // 解除
        mState.unregisterCallacks();
        super.onDestroy();
    }

    /**
     * ナビゲーションドローワ―の有効化
     */
    private void enableNavigationDrawer() {
        // ナビゲーションドローワ―の生成
        if (mToggle == null) {
            mToggle = new ActionBarDrawerToggle(
                    this,
                    mBinding.drawerLayout,
                    mBinding.toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }
        // ナビゲーションドローワ―の設定
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setToolbarNavigationClickListener(this);
        mToggle.syncState();
        mBinding.drawerLayout.addDrawerListener(mToggle);
        mBinding.drawerLayout.addDrawerListener(this);
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mBinding.navView.setNavigationItemSelectedListener(this);
    }

    /**
     * ナビゲーションドローワ―の無効化
     */
    private void disableNavigationDrawer() {
        // ナビゲーションドローワ―の設定解除
        if (mToggle != null) {
            mToggle.setDrawerIndicatorEnabled(false);
            mToggle.syncState();
            mBinding.drawerLayout.removeDrawerListener(mToggle);
            mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // 選択状態の取得
            switch (mState.getSelection()) {
                case SELECTED_ALL:
                case MULTI_SELECTED:
                case SELECTING:
                case SELECTED: {
                    // 選択解除
                    mState.changeSelection(UNSELECTED);
                    break;
                }
                default: {
                    super.onBackPressed();
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        // メニュー設定
        // 選択状態の確認
        switch (mState.getSelection()) {
            case SELECTING:
            case UNSELECTED: {
                if (toList(mDataSet).isEmpty()) {
                    getMenuInflater().inflate(R.menu.trash_empty, menu);
                } else {
                    getMenuInflater().inflate(R.menu.trash, menu);
                }
                break;
            }
            case SELECTED:
            case MULTI_SELECTED: {
                getMenuInflater().inflate(R.menu.trash_selected, menu);
                break;
            }
            case SELECTED_ALL: {
                if (mSelected.size() == 1) {
                    getMenuInflater().inflate(R.menu.trash_selected_all_one, menu);
                } else {
                    getMenuInflater().inflate(R.menu.trash_selected_all, menu);
                }
                break;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();

        // 識別子ごとの処理
        switch (id) {
            case R.id.menu_info: {
                SimpleAsset asset = getAsset(mSelected.get(0).uuid, mDataSet);
                mSelected.clear();
                asset.timestamp = System.currentTimeMillis();
                for (SimpleAsset dest : mDataSet) {
                    if (dest.equal(asset)) {
                        dest.copy(asset);
                    }
                }
                mClient.setList(mDataSet);

                LAYOUT layout = LAYOUT.valueOf(mPref.getString(EXTRA_LAYOUT, LINEAR.name()));
                if (layout == LAYOUT.GRID) {
                    startDetailActivity_from_Module(this, this, asset);
                } else {
                    startDetailActivity_from_Line(this, this, asset);
                }
                return true;
            }
            // 一覧表示
            case R.id.menu_empty: {

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                mDataSet = toEmptyTrash(mDataSet);

                // 一覧画面表示更新
                mCollectionView.changeAll(toList(mDataSet));

                // 設定
                mClient.setList(mDataSet);

                // メッセージ保持
                String message = getString(R.string.empty_trash_is_done);
                Snackbar.make(mBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), v -> {
                            // データ設定
                            mDataSet.clear();
                            copy(mDataSet, mBackup);
                            // 画面表示更新
                            updateView(toList(mDataSet));
                            // 設定
                            mClient.setList(mDataSet);
                        })
                        .show();

                return true;
            }
            case R.id.menu_untrash: {
                mHandler.post(new UnTrashRunner(mSelected));
                // 選択解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_selected_all: {
                // 全選択
                mState.changeSelection(SELECTED_ALL);
                return true;
            }
            case R.id.menu_white: {
                mHandler.post(new ChangeColorRunner(mSelected, WHITE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_red: {
                mHandler.post(new ChangeColorRunner(mSelected, RED));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_pink: {
                mHandler.post(new ChangeColorRunner(mSelected, PINK));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_purple: {
                mHandler.post(new ChangeColorRunner(mSelected, PURPLE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_deep_purple: {
                mHandler.post(new ChangeColorRunner(mSelected, DEEP_PURPLE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_indigo: {
                mHandler.post(new ChangeColorRunner(mSelected, INDIGO));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_blue: {
                mHandler.post(new ChangeColorRunner(mSelected, BLUE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_green: {
                mHandler.post(new ChangeColorRunner(mSelected, GREEN));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_light_green: {
                mHandler.post(new ChangeColorRunner(mSelected, LIGHT_GREEN));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_lime: {
                mHandler.post(new ChangeColorRunner(mSelected, LIME));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_yellow: {
                mHandler.post(new ChangeColorRunner(mSelected, YELLOW));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_amber: {
                mHandler.post(new ChangeColorRunner(mSelected, AMBER));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_orange: {
                mHandler.post(new ChangeColorRunner(mSelected, ORANGE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_deep_orange: {
                mHandler.post(new ChangeColorRunner(mSelected, DEEP_ORANGE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_brown: {
                mHandler.post(new ChangeColorRunner(mSelected, BROWN));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_blue_grey: {
                mHandler.post(new ChangeColorRunner(mSelected, BLUE_GREY));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 画面表示更新
     */
    private void updateView(ArrayList<SimpleAsset> collection) {
        // プログレスの終了
        disableProgressBar();
        // 空画面表示の更新
        updateEmptyView(collection);
        // メニューの更新
        updateMenu();
        // 一覧画面表示更新
        LAYOUT layout = LAYOUT.valueOf(mPref.getString(EXTRA_LAYOUT, LINEAR.name()));
        updateCollectionView(layout, collection);
        // タイトル更新
        updateTitle(collection);
    }

    /**
     * プログレスバーの更新
     */
    private void disableProgressBar() {
        // プログレスバーの終了
        mBinding.productImageLoading.setVisibility(View.INVISIBLE);
    }

    /**
     * 空画面表示の更新
     */
    private void updateEmptyView(ArrayList<SimpleAsset> collection) {
        // 空画面表示の更新
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
     * メニューの更新
     *
     */
    private void updateMenu() {
        String layout = mPref.getString(EXTRA_LAYOUT, LINEAR.name());
        if (LAYOUT.valueOf(layout) == LAYOUT.GRID) {
            mState.changeLayout(LINEAR);
        } else {
            mState.changeLayout(LAYOUT.GRID);
        }
        invalidateOptionsMenu();
    }

    /**
     * 一覧画面表示の更新
     */
    private void updateCollectionView(LAYOUT id, ArrayList<SimpleAsset> collection) {
        if (id == LAYOUT.GRID) {
            mCollectionView = ModuleFragment.newInstance(collection);
        } else {
            mCollectionView = ListFragment.newInstance(collection);
        }
        // 画面の取得
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * タイトルの更新
     *
     * @param collection 一覧
     */
    private void updateTitle(ArrayList<SimpleAsset> collection) {
        StringBuilder sb = new StringBuilder();
        if (collection.isEmpty()) {
            sb.append(this.getString(R.string.trash));
        } else {
            sb.append(this.getString(R.string.trash)).append("  ").append(collection.size());
        }
        mBinding.toolbar.setTitle(sb.toString());
        sb.delete(0, sb.length());
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
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        if (mState.getId() != R.id.trash) {
            Intent intent = getIntent();
            intent.setClass(getApplicationContext(), getStartActivity(mState.getId()));
            startActivity(intent);
            overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
            finish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerStateChanged(int newState) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mState.setId(item.getItemId());
        // コンテンツ識別子の保存
        mPref.edit().putInt(EXTRA_CONTENT, item.getItemId()).apply();
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
                            // 一覧画面表示更新
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
                            // 空画面表示の更新
                            updateEmptyView(list);
                            // タイトル更新
                            updateTitle(list);
                            // メニューの更新
                            updateMenu();

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
     * ゴミ箱の解除
     */
    private class UnTrashRunner implements Runnable {
        /**
         * データ
         */
        List<SimpleAsset> mList;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        UnTrashRunner(SimpleAsset item) {
            mList = new ArrayList<>();
            mList.add(item);
        }


        /**
         * コンストラクタ
         *
         * @param list データ
         */
        UnTrashRunner(ArrayList<SimpleAsset> list) {
            mList = list;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // 選択解除
                mState.changeSelection(UNSELECTED);

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                // データの設定
                for (SimpleAsset dest : mDataSet) {
                    for (SimpleAsset src : mSelected) {
                        // 一致
                        if (dest.equal(src)) {
                            // データの変更
                            dest.content = SimpleAsset.CONTENT.CONTACT;
                        }
                    }
                }

                // 一覧画面表示更新
                mUndos.clear();
                for (SimpleAsset src : mSelected) {
                    int position;
                    position = mCollectionView.remove(toItem(src));
                    mUndos.add(new Undo(INSERT, position, getAsset(src.uuid, mBackup)));
                }
                Collections.reverse(mUndos);

                // 設定
                mClient.setList(mDataSet);

                // 選択一覧の削除
                mSelected.clear();

                // スナックバーの生成
                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.restored_item);
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.restored_some_items, mUndos.size());
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

                // 一覧画面表示更新
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
     * 選択したアーカイブの色変更
     */
    private class ChangeColorRunner implements Runnable {

        /**
         * データ
         */
        List<SimpleAsset> mList;

        /**
         * 色
         */
        COLOR mCOLOR;

        /**
         * コンストラクタ
         *
         * @param color 色
         */
        ChangeColorRunner(ArrayList<SimpleAsset> list, COLOR color) {
            mList = list;
            mCOLOR = color;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);

                // データの設定
                for (SimpleAsset src : mList) {
                    // 選択状態の解除
                    src.isSelected = false;
                    // 色の設定
                    src.color = mCOLOR;
                    // 画像の設定
                    src.imagePath = INVALID_STRING_VALUE;
                }
                for (SimpleAsset dest : mDataSet) {
                    for (SimpleAsset src : mList) {
                        // 一致
                        if (dest.equal(src)) {
                            // データの変更
                            dest.copy(src);
                        }
                    }
                }

                // プログレスバーの更新
                disableProgressBar();
                // 一覧画面表示の更新
                for (SimpleAsset item : mList) {
                    // 画面表示を更新
                    mCollectionView.change(toItem(item));
                }

                // 設定
                mClient.setList(mDataSet);

                // 選択一覧の削除
                mList.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 変更
     */
    private class ChangeRunner implements Runnable {

        /**
         * @serial 項目
         */
        SimpleAsset mItem;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        ChangeRunner(SimpleAsset item) {
            mItem = item;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {
                // 一覧画面表示が空でなく
                // 項目が空でない場合
                if ((mCollectionView != null) && (mItem != null)) {

                    // 設定
                    for (SimpleAsset dest : mDataSet) {
                        // 一致
                        if (dest.equal(mItem)) {
                            dest.copy(mItem);
                        }
                    }

                    // プログレスバーの更新
                    disableProgressBar();

                    // 一覧画面表示更新
                    mCollectionView.change(toItem(getAsset(mItem.uuid, mDataSet)));

                    // 設定
                    mClient.setList(mDataSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ゴミ箱一覧の取得
     *
     * @param collection 一覧
     * @return ゴミ箱一覧
     */
    public ArrayList<SimpleAsset> toList(Collection<SimpleAsset> collection) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        for (SimpleAsset asset : collection) {
            if (asset.content == SimpleAsset.CONTENT.TRASH) {
                result.add(toItem(asset));
            }
        }
        return result;
    }

    /**
     * ゴミ箱一覧の取得
     *
     * @param collection 一覧
     * @return ゴミ箱一覧
     */
    public ArrayList<SimpleAsset> toEmptyTrash(Collection<SimpleAsset> collection) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        for (SimpleAsset asset : collection) {
            if (asset.content == SimpleAsset.CONTENT.TRASH) {
                result.add(toItem(asset));
            }
        }
        return result;
    }

    /**
     * ゴミ箱の項目取得
     *
     * @param asset アセット
     */
    public SimpleAsset toItem(SimpleAsset asset) {
        asset.title = asset.displayName;
        asset.subtitle = asset.note;
        return asset;
    }
}
