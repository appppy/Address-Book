package jp.osaka.cherry.addressbook.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import jp.osaka.cherry.addressbook.utils.ImageHelper;
import jp.osaka.cherry.addressbook.utils.timer.SimpleTimer;
import jp.osaka.cherry.addressbook.utils.timer.TimerListener;

import static android.view.View.GONE;
import static jp.osaka.cherry.addressbook.Config.LOG_D;
import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_ITEM;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;
import static jp.osaka.cherry.addressbook.constants.TIMEOUT.FLOATING_ACTION_BUTTON_HIDE;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.isMultiSelected;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.isSelected;
import static jp.osaka.cherry.addressbook.utils.BackgroundHelper.setLineBackground;
import static jp.osaka.cherry.addressbook.utils.ImageHelper.decodeFile;
import static jp.osaka.cherry.addressbook.utils.timer.TimerHelper.createTimer;
import static jp.osaka.cherry.addressbook.utils.timer.TimerHelper.startTimer;
import static jp.osaka.cherry.addressbook.utils.timer.TimerHelper.stopTimer;

/**
 * ????????????????????????
 */
public class ListFragment extends Fragment implements
        ICollectionView<SimpleAsset>,
        ItemListener<SimpleAsset>,
        TimerListener {

    /**
     * @serial ??????
     */
    private final String TAG = "ListFragment";

    /**
     * @serial ??????????????????
     */
    private Callbacks<SimpleAsset> mCallbacks;

    /**
     * @serial ????????????????????????????????????????????????????????????
     */
    private SimpleTimer mTimer;

    /**
     * @serial ????????????
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial ??????
     */
    private final ListFragment mSelf;

    /**
     * @serial ????????????
     */
    private ListAdapter mAdapter;

    /**
     * @serial ?????????????????????
     */
    private FragmentBinding mBinding;

    /**
     * ????????????????????????
     *
     * @param collection ?????????
     */
    public static ListFragment newInstance(ArrayList<SimpleAsset> collection) {
        // ???????????????????????????
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_ITEM, collection);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * ?????????????????????
     */
    public ListFragment() {
        mSelf = this;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = ((Callbacks<SimpleAsset>) getActivity());
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

        // ???????????????
        mTimer = createTimer(mTimer, FLOATING_ACTION_BUTTON_HIDE, this);
        // ??????????????????
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
        // ???????????????
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

        mBinding = DataBindingUtil.bind(requireView());

        ArrayList<SimpleAsset> assets = requireArguments().getParcelableArrayList(EXTRA_ITEM);

        // ?????????????????????
        mAdapter = new ListAdapter(getContext(), this, assets);


        // ????????????????????????
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
                // ???????????????
                startTimer(mTimer);
                if (mCallbacks != null) {
                    mCallbacks.onScroll(mSelf);
                }
            }
        });
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            /**
             * {@inheritDoc}
             */
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // ???????????????????????????????????????????????????????????????
                if (isMultiSelected(mAdapter.getCollection())) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, 0) |
                            makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, 0);
                }
                // ????????????????????????
                if (isSelected(mAdapter.getCollection())) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END) |
                            makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, 0);
                }

                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP) |
                        makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START | ItemTouchHelper.END);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                if (from >= 0 && to >= 0) {

                    // ?????????????????????
                    SimpleAsset asset = mAdapter.getCollection().get(from);
                    asset.isSelected = false;

                    mAdapter.move(from, to);
                    mCallbacks.onMoveChanged(mSelf, mAdapter.getCollection());
                }
                return true;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                switch (actionState) {
                    case ItemTouchHelper.ACTION_STATE_IDLE:
                    case ItemTouchHelper.ACTION_STATE_SWIPE: {
                        break;
                    }
                    default: {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            float elevation = 8 * getResources().getDisplayMetrics().density;
                            viewHolder.itemView.setElevation(elevation);
                        }

                        ListAdapter.BindingHolder holder = (ListAdapter.BindingHolder) viewHolder;

                        // ???????????????
                        setLineBackground(getContext(), holder.getBinding().cardView, true);

                        // ?????????????????????
                        holder.getBinding().iconView.setVisibility(GONE);
                        holder.getBinding().icon2.setImageResource(R.drawable.ic_check_circle_black_24dp);
                        holder.getBinding().icon2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey_600));
                        holder.getBinding().icon2.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.itemView.setElevation(0);
                }

                ListAdapter.BindingHolder holder = (ListAdapter.BindingHolder) viewHolder;

                // ??????????????????????????????
                int position = viewHolder.getAdapterPosition();
                if (position >= 0) {
                    SimpleAsset item = mAdapter.getCollection().get(position);
                    // ????????????????????????????????????????????????????????????
                    if (!item.isSelected) {
                        // ???????????????
                        setLineBackground(getContext(), holder.getBinding().cardView, item.isSelected);

                        // ?????????????????????
                        if (item.imagePath.equals(INVALID_STRING_VALUE)) {
                            holder.getBinding().iconView.setVisibility(GONE);
                            holder.getBinding().icon2.setImageResource(R.drawable.ic_lens_black_24dp);
                            holder.getBinding().icon2.setColorFilter(ImageHelper.getImageColor(getContext(), item.color));
                            holder.getBinding().icon2.setVisibility(View.VISIBLE);
                        } else {
                            holder.getBinding().icon2.setVisibility(GONE);
                            try {
                                holder.getBinding().iconView.setColorFilter(null);
                                // ???????????????????????????
                                Bitmap bitmap = decodeFile(item.imagePath);
                                // imageView????????????
                                holder.getBinding().iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                holder.getBinding().iconView.setImageURI(null);
                                // ImageView????????????
                                holder.getBinding().iconView.setImageBitmap(bitmap);
                                holder.getBinding().iconView.setVisibility(View.VISIBLE);
                            } catch (OutOfMemoryError | Exception e) {
                                e.printStackTrace();
                            }
                        }
                        mCallbacks.onSelectedChanged(mSelf, holder.getBinding().cardView, item, mAdapter.getCollection());
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position >= 0) {
                    if (mCallbacks != null) {
                        mCallbacks.onSwiped(mSelf, mAdapter.getCollection().get(position));
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.6f;
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mBinding.collection);
        mAdapter.setItemTouchHelper(itemTouchHelper);


        if (LOG_I) {
            Log.i(TAG, "onActivityCreated#leave");
        }
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
     * ????????????
     *
     * @param view ??????
     * @param item ??????
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