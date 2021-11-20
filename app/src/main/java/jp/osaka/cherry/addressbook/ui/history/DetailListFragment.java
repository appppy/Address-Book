package jp.osaka.cherry.addressbook.ui.history;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.DividerItemDecoration;
import jp.osaka.cherry.addressbook.android.view.ICollectionView;
import jp.osaka.cherry.addressbook.android.view.adapter.ItemListener;
import jp.osaka.cherry.addressbook.databinding.FragmentBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.utils.timer.SimpleTimer;
import jp.osaka.cherry.addressbook.utils.timer.TimerListener;

import static jp.osaka.cherry.addressbook.Config.LOG_D;
import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_ITEM;
import static jp.osaka.cherry.addressbook.constants.TIMEOUT.FLOATING_ACTION_BUTTON_HIDE;
import static jp.osaka.cherry.addressbook.utils.timer.TimerHelper.createTimer;
import static jp.osaka.cherry.addressbook.utils.timer.TimerHelper.startTimer;
import static jp.osaka.cherry.addressbook.utils.timer.TimerHelper.stopTimer;


/**
 * 一覧フラグメント
 */
public class DetailListFragment extends Fragment implements
        ICollectionView<SimpleAsset>,
        ItemListener<SimpleAsset>,
        TimerListener {

    /**
     * @serial 目印
     */
    private final String TAG = "DetailListFragment";

    /**
     * @serial コールバック
     */
    private Callbacks<SimpleAsset> mCallbacks;

    /**
     * @serial フローティングアクションボタン表示タイマ
     */
    private SimpleTimer mTimer;

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial 自身
     */
    private final DetailListFragment mSelf;

    /**
     * @serial アダプタ
     */
    private DetailListAdapter mAdapter;

    /**
     * @serial バインディング
     */
    private FragmentBinding mBinding;

    /**
     * インスタンス取得
     *
     * @param collection カード
     */
    public static DetailListFragment newInstance(ArrayList<SimpleAsset> collection) {
        // フラグメントの生成
        DetailListFragment fragment = new DetailListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_ITEM, collection);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * コンストラクタ
     */
    public DetailListFragment() {
        mSelf = this;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
         mCallbacks = (Callbacks<SimpleAsset>) getActivity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // タイマ生成
        mTimer = createTimer(mTimer, FLOATING_ACTION_BUTTON_HIDE, this);
        // 再生成を抑止
        setRetainInstance(true);

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        if (LOG_I) {
            Log.i(TAG, "onDestroy#enter");
        }

        // タイマ停止
        stopTimer(mTimer);

        if (LOG_I) {
            Log.i(TAG, "onDestroy#leave");
        }
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment, container, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding = DataBindingUtil.bind(requireView());

        ArrayList<SimpleAsset> assets = requireArguments().getParcelableArrayList(EXTRA_ITEM);

        // アダプタの設定
        mAdapter = new DetailListAdapter(getContext(), this, assets);

        // レイアウトの設定
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mBinding.collection.addItemDecoration(new DividerItemDecoration(requireActivity()));
        mBinding.collection.setLayoutManager(layoutManager);
        mBinding.collection.setAdapter(mAdapter);
        mBinding.collection.setItemAnimator(new DefaultItemAnimator());
        mBinding.collection.setVerticalScrollBarEnabled(false);
        mBinding.collection.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // タイマ開始
                startTimer(mTimer);
                if (mCallbacks != null) {
                    mCallbacks.onScroll(mSelf);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTimer(Object timer, int timeOutCount, boolean timerState) {
        mHandler.post(() -> {
            if (null != mCallbacks) {
                mCallbacks.onScrollFinished(mSelf);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void diselect() {
        if (mAdapter != null) {
            Collection<SimpleAsset> collection = mAdapter.getCollection();
            for (SimpleAsset item : collection) {
                item.isSelected = false;
            }
            mAdapter.notifyDataSetChanged();
            if (LOG_D) {
                Log.d(TAG, "size: " + mAdapter.getCollection().size());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends SimpleAsset> selectedAll() {
        Collection<? extends SimpleAsset> result = null;
        if (mAdapter != null) {
            Collection<SimpleAsset> collection = mAdapter.getCollection();
            for (SimpleAsset item : collection) {
                item.isSelected = true;
            }
            mAdapter.notifyDataSetChanged();
            if (LOG_D) {
                Log.d(TAG, "size: " + mAdapter.getCollection().size());
            }
            result = mAdapter.getCollection();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(int index, SimpleAsset item) {
        if (mAdapter != null) {
            mAdapter.insert(index, item);
            if (index == 0) {
                mBinding.collection.scrollToPosition(index);
            }
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(@NonNull SimpleAsset item) {
        if (mAdapter != null) {
            mAdapter.insert(0, item);
            mBinding.collection.scrollToPosition(0);
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int remove(@NonNull SimpleAsset item) {
        int location = 0;
        if (mAdapter != null) {
            location = mAdapter.getCollection().indexOf(item);
            if (location >= 0) {
                mAdapter.remove(location);
            }
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int change(@NonNull SimpleAsset item) {
        int position = 0;
        if (mAdapter != null) {
            position = mAdapter.set(item);
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeAll(Collection<? extends SimpleAsset> collection) {
        if (mAdapter != null) {
            mAdapter.setAll(collection);
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClickMore(View view, SimpleAsset item) {
        if (mCallbacks != null) {
            mCallbacks.onSelectedMore(this, view, item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view, SimpleAsset item) {
        if (mCallbacks != null) {
            mCallbacks.onSelected(this, view, item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLongClick(View view, SimpleAsset item) {
        if (mCallbacks != null) {
            mCallbacks.onSelectedChanged(this, view, item, mAdapter.getCollection());
        }
    }
}