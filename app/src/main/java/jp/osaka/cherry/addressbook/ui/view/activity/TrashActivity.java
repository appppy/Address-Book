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
 * ???????????????
 */
public class TrashActivity extends AppCompatActivity implements
        SimpleClient.Callbacks,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        ICollectionView.Callbacks<SimpleAsset>,
        View.OnClickListener,
        State.Callbacks {

    /**
     * @serial ??????
     */
    public State mState = new State();

    /**
     * @serial ??????
     */
    private final String TAG = "TrashActivity";

    /**
     * @serial ??????
     */
    private Activity mSelf;

    /**
     * @serial ?????????????????????
     */
    private SharedPreferences mPref;

    /**
     * @serial ????????????
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial ??????????????????
     */
    private final SimpleClient mClient = new SimpleClient(this, this);

    /**
     * @serial ??????????????????
     */
    private ArrayList<SimpleAsset> mDataSet = new ArrayList<>();

    /**
     * @serial ??????????????????
     */
    private final ArrayList<SimpleAsset> mBackup = new ArrayList<>();

    /**
     * @serial ???????????????
     */
    private final ArrayList<SimpleAsset> mSelected = new ArrayList<>();

    /**
     * @serial ??????
     */
    private final ArrayList<Redo> mRedos = new ArrayList<>();

    /**
     * @serial ??????
     */
    private final ArrayList<Undo> mUndos = new ArrayList<>();

    /**
     * @serial ?????????????????????
     */
    private ActivityTrashBinding mBinding;

    /**
     * @serial ?????????
     */
    private ActionBarDrawerToggle mToggle;

    /**
     * @serial ??????????????????
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
                // ???????????????
                mDataSet.clear();
                mDataSet.addAll(assets);
                // ???????????????
                updateView(toList(mDataSet));
                // ????????????????????????
                //addGeofencesHandler(mDataSet);
            }
            // ?????????????????????
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

            // ????????????
            mState.changeSelection(UNSELECTED);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view) {
        // ????????????
        mState.changeSelection(UNSELECTED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectChanged(State state) {
        mHandler.post(() -> {
            try {
                // ?????????????????????
                switch (mState.getSelection()) {
                    case UNSELECTED: {
                        // ?????????????????????????????????
                        if (mCollectionView != null) {
                            // ??????????????????????????????
                            ActionBar bar = getSupportActionBar();
                            if (bar != null) {
                                bar.setDisplayHomeAsUpEnabled(false);
                            }

                            // ???????????????
                            mCollectionView.diselect();

                            // ?????????????????????????????????????????????
                            enableNavigationDrawer();

                            // ????????????????????????
                            updateTitle(toList(mDataSet));
                            mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_600));
                            // ??????????????????????????????
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.grey_800));
                            }
                            // ??????????????????
                            mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_800));

                            // ?????????????????????
                            invalidateOptionsMenu();
                        }
                        break;
                    }
                    // ???????????????
                    case SELECTED_ALL: {
                        // ?????????????????????????????????
                        if (mCollectionView != null) {

                            // ????????????????????????????????????
                            disableNavigationDrawer();

                            // ??????????????????????????????
                            ActionBar bar = getSupportActionBar();
                            if (bar != null) {
                                bar.setDisplayHomeAsUpEnabled(true);
                            }

                            // ?????????
                            mSelected.clear();
                            mSelected.addAll(mCollectionView.selectedAll());

                            // ????????????????????????
                            mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                            mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_600));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.grey_800));
                            }
                            // ??????????????????
                            mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_800));

                            // ??????????????????
                            invalidateOptionsMenu();
                        }
                        break;
                    }
                    case MULTI_SELECTED:
                    case SELECTED: {
                        // ????????????????????????????????????
                        disableNavigationDrawer();

                        // ??????????????????????????????
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(true);
                        }

                        // ????????????????????????
                        mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.grey_800));
                        }
                        // ??????????????????
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_800));

                        // ??????????????????
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
        // ????????????
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param collectionView ??????????????????
     * @param view           ????????????
     * @param item           ??????
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

            // ?????????????????????
            mSelected.clear();
            mSelected.add(item);

            // ????????????????????????????????????
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
        // ??????????????????????????????
        if(!mState.isResumed()) {
            return;
        }

        // ?????????????????????
        startDetailActivity_from_Line(this, this, view, getAsset(item.uuid, mDataSet));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedChanged(ICollectionView<SimpleAsset> collectionView, View view, SimpleAsset item, Collection<? extends SimpleAsset> collection) {
        // ??????????????????
        mSelected.clear();
        mSelected.addAll(getSelectedCollection(collection));

        // ?????????????????????
        if (mSelected.size() == collection.size()) {
            // ?????????
            if (!mState.changeSelection(SELECTED_ALL)) {
                // ?????????????????????
                // ??????????????????
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // ??????????????????
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() > 1) {
            // ???????????????
            if (!mState.changeSelection(MULTI_SELECTED)) {
                // ?????????????????????
                // ??????????????????
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // ??????????????????
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() == 1) {
            // ??????
            if (!mState.changeSelection(SELECTED)) {
                // ?????????????????????
                // ??????????????????
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // ??????????????????
                invalidateOptionsMenu();
            }
        } else {
            // ?????????
            mState.changeSelection(UNSELECTED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSwiped(ICollectionView<SimpleAsset> collectionView, SimpleAsset item) {
        // ??????????????????
        mBackup.clear();
        copy(mBackup, mDataSet);

        // ????????????????????????
        mUndos.clear();
        int position = mCollectionView.remove(toItem(item));
        mUndos.add(new Undo(INSERT, position, item));
        Collections.reverse(mUndos);

        // ??????????????????
        SimpleAsset asset = getAsset(item.uuid, mDataSet);
        asset.content = SimpleAsset.CONTENT.TRASH;
        mClient.setList(mDataSet);

        // ???????????????????????????
        String message = getString(R.string.restored_item);
        makeUndoSnackbar(mBinding.coordinatorLayout, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(ICollectionView<SimpleAsset> view) {
        // ????????????
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollFinished(ICollectionView<SimpleAsset> view) {
        // ????????????
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoveChanged(ICollectionView<SimpleAsset> view, Collection<? extends SimpleAsset> collection) {
        // ??????????????????
        ArrayList<SimpleAsset> list = new ArrayList<>(collection);

        // ?????????????????????????????????
        mState.changeSelection(SELECTING);

        // ????????????????????????????????????
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }

        // ???????????????????????????
        updateTitle(list);

        // ?????????????????????????????????????????????
        enableNavigationDrawer();

        // ???????????????????????????
        invalidateOptionsMenu();

        //????????????????????????
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
     * ????????????
     *
     * @param view       ????????????
     * @param collection ??????
     */
    @Override
    public void onUpdated(ICollectionView<SimpleAsset> view, Collection<? extends SimpleAsset> collection) {
        // ????????????
        ArrayList<SimpleAsset> arrayList = new ArrayList<>(collection);
        // ????????????????????????
        updateEmptyView(arrayList);
        // ??????????????????
        updateTitle(arrayList);
        // ?????????????????????
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
        // ???????????????
        mSelf = this;

        // ??????????????????????????????
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        // ??????????????????
        setTheme(R.style.AppTheme_Grey);

        // ????????????????????????
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_trash);

        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.trash);
        }
        // ??????????????????????????????
        enableNavigationDrawer();

        // ??????
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
        // ??????????????????????????????
        mState.setResumed(true);
        // ?????????????????????
        mClient.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        // ??????????????????????????????
        mState.setResumed(false);
        // ????????????????????????
        mClient.disconnect();
        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        // ??????
        mState.unregisterCallacks();
        super.onDestroy();
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void enableNavigationDrawer() {
        // ?????????????????????????????????????????????
        if (mToggle == null) {
            mToggle = new ActionBarDrawerToggle(
                    this,
                    mBinding.drawerLayout,
                    mBinding.toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }
        // ?????????????????????????????????????????????
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setToolbarNavigationClickListener(this);
        mToggle.syncState();
        mBinding.drawerLayout.addDrawerListener(mToggle);
        mBinding.drawerLayout.addDrawerListener(this);
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mBinding.navView.setNavigationItemSelectedListener(this);
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void disableNavigationDrawer() {
        // ???????????????????????????????????????????????????
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
            // ?????????????????????
            switch (mState.getSelection()) {
                case SELECTED_ALL:
                case MULTI_SELECTED:
                case SELECTING:
                case SELECTED: {
                    // ????????????
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
        // ??????????????????
        // ?????????????????????
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
        // ??????????????????????????????
        if(!mState.isResumed()) {
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();

        // ????????????????????????
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
            // ????????????
            case R.id.menu_empty: {

                // ??????????????????
                mBackup.clear();
                copy(mBackup, mDataSet);

                mDataSet = toEmptyTrash(mDataSet);

                // ????????????????????????
                mCollectionView.changeAll(toList(mDataSet));

                // ??????
                mClient.setList(mDataSet);

                // ?????????????????????
                String message = getString(R.string.empty_trash_is_done);
                Snackbar.make(mBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), v -> {
                            // ???????????????
                            mDataSet.clear();
                            copy(mDataSet, mBackup);
                            // ??????????????????
                            updateView(toList(mDataSet));
                            // ??????
                            mClient.setList(mDataSet);
                        })
                        .show();

                return true;
            }
            case R.id.menu_untrash: {
                mHandler.post(new UnTrashRunner(mSelected));
                // ????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_selected_all: {
                // ?????????
                mState.changeSelection(SELECTED_ALL);
                return true;
            }
            case R.id.menu_white: {
                mHandler.post(new ChangeColorRunner(mSelected, WHITE));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_red: {
                mHandler.post(new ChangeColorRunner(mSelected, RED));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_pink: {
                mHandler.post(new ChangeColorRunner(mSelected, PINK));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_purple: {
                mHandler.post(new ChangeColorRunner(mSelected, PURPLE));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_deep_purple: {
                mHandler.post(new ChangeColorRunner(mSelected, DEEP_PURPLE));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_indigo: {
                mHandler.post(new ChangeColorRunner(mSelected, INDIGO));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_blue: {
                mHandler.post(new ChangeColorRunner(mSelected, BLUE));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_green: {
                mHandler.post(new ChangeColorRunner(mSelected, GREEN));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_light_green: {
                mHandler.post(new ChangeColorRunner(mSelected, LIGHT_GREEN));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_lime: {
                mHandler.post(new ChangeColorRunner(mSelected, LIME));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_yellow: {
                mHandler.post(new ChangeColorRunner(mSelected, YELLOW));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_amber: {
                mHandler.post(new ChangeColorRunner(mSelected, AMBER));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_orange: {
                mHandler.post(new ChangeColorRunner(mSelected, ORANGE));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_deep_orange: {
                mHandler.post(new ChangeColorRunner(mSelected, DEEP_ORANGE));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_brown: {
                mHandler.post(new ChangeColorRunner(mSelected, BROWN));
                // ?????????????????????
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_blue_grey: {
                mHandler.post(new ChangeColorRunner(mSelected, BLUE_GREY));
                // ?????????????????????
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
     * ??????????????????
     */
    private void updateView(ArrayList<SimpleAsset> collection) {
        // ????????????????????????
        disableProgressBar();
        // ????????????????????????
        updateEmptyView(collection);
        // ?????????????????????
        updateMenu();
        // ????????????????????????
        LAYOUT layout = LAYOUT.valueOf(mPref.getString(EXTRA_LAYOUT, LINEAR.name()));
        updateCollectionView(layout, collection);
        // ??????????????????
        updateTitle(collection);
    }

    /**
     * ??????????????????????????????
     */
    private void disableProgressBar() {
        // ??????????????????????????????
        mBinding.productImageLoading.setVisibility(View.INVISIBLE);
    }

    /**
     * ????????????????????????
     */
    private void updateEmptyView(ArrayList<SimpleAsset> collection) {
        // ????????????????????????
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            // ????????????
            mBinding.emptyView.setVisibility(View.VISIBLE);
        } else {
            // ??????????????????
            mBinding.emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * ?????????????????????
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
     * ???????????????????????????
     */
    private void updateCollectionView(LAYOUT id, ArrayList<SimpleAsset> collection) {
        if (id == LAYOUT.GRID) {
            mCollectionView = ModuleFragment.newInstance(collection);
        } else {
            mCollectionView = ListFragment.newInstance(collection);
        }
        // ???????????????
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * ?????????????????????
     *
     * @param collection ??????
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
        // ????????????
        if (requestCode == DETAIL_ITEM.ordinal()) {
            if (resultCode == RESULT_OK) {
                // ??????????????????
                Bundle bundle = data.getExtras();
                SimpleAsset item = Objects.requireNonNull(bundle).getParcelable(EXTRA_SIMPLE_ASSET);
                // ????????????????????????????????????
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
        // ????????????
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        // ????????????
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
        // ????????????
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mState.setId(item.getItemId());
        // ?????????????????????????????????
        mPref.edit().putInt(EXTRA_CONTENT, item.getItemId()).apply();
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * ???????????????????????????
     *
     * @param layout  ???????????????
     * @param message ???????????????
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
                            // ????????????????????????
                            for (Undo undo : mUndos) {

                                // ?????????????????????????????????
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

                            // ???????????????
                            ArrayList<SimpleAsset> list = toList(mBackup);
                            // ????????????????????????
                            updateEmptyView(list);
                            // ??????????????????
                            updateTitle(list);
                            // ?????????????????????
                            updateMenu();

                            // ???????????????
                            mDataSet.clear();
                            copy(mDataSet, mBackup);

                            // ??????
                            mClient.setList(mDataSet);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    /**
     * ??????????????????
     */
    private class UnTrashRunner implements Runnable {
        /**
         * ?????????
         */
        List<SimpleAsset> mList;

        /**
         * ?????????????????????
         *
         * @param item ??????
         */
        UnTrashRunner(SimpleAsset item) {
            mList = new ArrayList<>();
            mList.add(item);
        }


        /**
         * ?????????????????????
         *
         * @param list ?????????
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

                // ????????????
                mState.changeSelection(UNSELECTED);

                // ??????????????????
                mBackup.clear();
                copy(mBackup, mDataSet);

                // ??????????????????
                for (SimpleAsset dest : mDataSet) {
                    for (SimpleAsset src : mSelected) {
                        // ??????
                        if (dest.equal(src)) {
                            // ??????????????????
                            dest.content = SimpleAsset.CONTENT.CONTACT;
                        }
                    }
                }

                // ????????????????????????
                mUndos.clear();
                for (SimpleAsset src : mSelected) {
                    int position;
                    position = mCollectionView.remove(toItem(src));
                    mUndos.add(new Undo(INSERT, position, getAsset(src.uuid, mBackup)));
                }
                Collections.reverse(mUndos);

                // ??????
                mClient.setList(mDataSet);

                // ?????????????????????
                mSelected.clear();

                // ???????????????????????????
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
     * ??????
     */
    private class ModifyRunner implements Runnable {

        /**
         * @serial ?????????
         */
        SimpleAsset mData;

        /**
         * ?????????????????????
         *
         * @param data ?????????
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

                // ??????????????????
                mBackup.clear();
                copy(mBackup, mDataSet);

                // ???????????????
                for (SimpleAsset dest : mDataSet) {
                    // ??????
                    if (dest.equal(mData)) {
                        dest.setParams(mData);
                    }
                }

                // ????????????????????????
                mUndos.clear();
                int position = mCollectionView.change(toItem(mData));
                mUndos.add(new Undo(CHANGE, position, getAsset(mData.uuid, mBackup)));
                Collections.reverse(mUndos);

                // ??????
                mClient.setList(mDataSet);

                // ???????????????????????????
                String message = getString(R.string.modified_item);
                makeUndoSnackbar(mBinding.coordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ???????????????????????????????????????
     */
    private class ChangeColorRunner implements Runnable {

        /**
         * ?????????
         */
        List<SimpleAsset> mList;

        /**
         * ???
         */
        COLOR mCOLOR;

        /**
         * ?????????????????????
         *
         * @param color ???
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
                // ?????????????????????
                mState.changeSelection(UNSELECTED);

                // ??????????????????
                for (SimpleAsset src : mList) {
                    // ?????????????????????
                    src.isSelected = false;
                    // ????????????
                    src.color = mCOLOR;
                    // ???????????????
                    src.imagePath = INVALID_STRING_VALUE;
                }
                for (SimpleAsset dest : mDataSet) {
                    for (SimpleAsset src : mList) {
                        // ??????
                        if (dest.equal(src)) {
                            // ??????????????????
                            dest.copy(src);
                        }
                    }
                }

                // ??????????????????????????????
                disableProgressBar();
                // ???????????????????????????
                for (SimpleAsset item : mList) {
                    // ?????????????????????
                    mCollectionView.change(toItem(item));
                }

                // ??????
                mClient.setList(mDataSet);

                // ?????????????????????
                mList.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ??????
     */
    private class ChangeRunner implements Runnable {

        /**
         * @serial ??????
         */
        SimpleAsset mItem;

        /**
         * ?????????????????????
         *
         * @param item ??????
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
                // ?????????????????????????????????
                // ???????????????????????????
                if ((mCollectionView != null) && (mItem != null)) {

                    // ??????
                    for (SimpleAsset dest : mDataSet) {
                        // ??????
                        if (dest.equal(mItem)) {
                            dest.copy(mItem);
                        }
                    }

                    // ??????????????????????????????
                    disableProgressBar();

                    // ????????????????????????
                    mCollectionView.change(toItem(getAsset(mItem.uuid, mDataSet)));

                    // ??????
                    mClient.setList(mDataSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????????????????
     *
     * @param collection ??????
     * @return ???????????????
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
     * ????????????????????????
     *
     * @param collection ??????
     * @return ???????????????
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
     * ????????????????????????
     *
     * @param asset ????????????
     */
    public SimpleAsset toItem(SimpleAsset asset) {
        asset.title = asset.displayName;
        asset.subtitle = asset.note;
        return asset;
    }
}
