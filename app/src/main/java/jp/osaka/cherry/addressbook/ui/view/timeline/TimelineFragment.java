package jp.osaka.cherry.addressbook.ui.view.timeline;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.service.timeline.TimelinePoint;
import jp.osaka.cherry.addressbook.utils.timer.SimpleTimer;
import jp.osaka.cherry.addressbook.utils.timer.TimerListener;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_ITEM;
import static jp.osaka.cherry.addressbook.constants.TIMEOUT.FLOATING_ACTION_BUTTON_HIDE;
import static jp.osaka.cherry.addressbook.utils.timer.TimerHelper.createTimer;
import static jp.osaka.cherry.addressbook.utils.timer.TimerHelper.startTimer;
import static jp.osaka.cherry.addressbook.utils.timer.TimerHelper.stopTimer;

/**
 * 一覧フラグメント
 */
public class TimelineFragment extends Fragment implements
        ICollectionView,
        TimerListener {

    /**
     * @serial 目印
     */
    private final String TAG = "TimelineFragment";

    /**
     * @serial コールバック
     */
    private Callbacks mCallbacks;

    /**
     * @serial フローティングアクションボタン表示タイマー
     */
    private SimpleTimer mTimer;

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial 自身
     */
    private final TimelineFragment mSelf;

    /**
     * インスタンス取得
     *
     * @param collection カード
     */
    public static TimelineFragment newInstance(ArrayList<TimelinePoint> collection) {
        // フラグメントの生成
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_ITEM, collection);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * コンストラクタ
     */
    public TimelineFragment() {
        mSelf = this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = ((Callbacks) getActivity());
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
    public void onDestroy() {
        // タイマ停止
        stopTimer(mTimer);
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
        if (LOG_I) {
            Log.i(TAG, "onActivityCreated#enter");
        }

        jp.osaka.cherry.addressbook.databinding.FragmentBinding mBinding = DataBindingUtil.bind(requireView());
        ArrayList<TimelinePoint> timelinePoints = requireArguments().getParcelableArrayList(EXTRA_ITEM);
        ArrayList<TimelinePoint> results = new ArrayList<>();
        if(!Objects.requireNonNull(timelinePoints).isEmpty()) {
            String headerStrings = "";
            for (TimelinePoint point : timelinePoints) {
                String newHeaderTitle = DateFormat.getDateInstance().format(new Date(point.date));
                if (!newHeaderTitle.equals(headerStrings)) {
                    headerStrings = newHeaderTitle;
                    TimelinePoint header = new TimelinePoint(System.currentTimeMillis(), newHeaderTitle, "");
                    header.setCategory(1);
                    results.add(header);
                }
                results.add(point);
            }
        }

        // アダプタの設定
        TimelineGroupAdapter mAdapter = new TimelineGroupAdapter(results);


        // レイアウトの設定
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        Objects.requireNonNull(mBinding).collection.setLayoutManager(layoutManager);
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
}