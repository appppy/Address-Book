package jp.osaka.cherry.addressbook.ui.view.timeline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.adapter.RecyclerArrayAdapter;
import jp.osaka.cherry.addressbook.databinding.ItemTimelineBinding;
import jp.osaka.cherry.addressbook.databinding.ItemTimelineHeaderBinding;
import jp.osaka.cherry.addressbook.service.timeline.TimelinePoint;

/**
 * タイムラインアダプタ
 */
class TimelineGroupAdapter extends RecyclerArrayAdapter<TimelinePoint, TimelineGroupAdapter.ViewHolder> {
    /**
     * コンストラクタ
     *
     * @param collection 一覧
     */
    TimelineGroupAdapter(List<TimelinePoint> collection) {
        super(collection);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1: {
                ItemTimelineHeaderBinding binding = DataBindingUtil
                        .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_timeline_header, parent, false);
                return new HeaderViewHolder(binding);
            }
            case 2: {
                ItemTimelineBinding binding = DataBindingUtil
                        .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_timeline, parent, false);
                return new ViewHolder(binding);
            }
        }
        ItemTimelineHeaderBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_timeline_header, parent, false);
        return new HeaderViewHolder(binding);
    }

    /**
     * 表示種類取得
     *
     * @param position 位置
     * @return 表示種類
     */
    public int getItemViewType(int position) {
        return getCollection().get(position).getCategory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 1:
                onBindHeaderItemViewHolder(holder, position);
                break;
            case 2:
                onBindItemViewHolder(holder, position);
                break;
        }
    }

    /**
     * ヘッダ項目保持
     *
     * @param holder 保持
     * @param position 位置
     */
    private void onBindHeaderItemViewHolder(final ViewHolder holder, int position) {
        ItemTimelineHeaderBinding binding = holder.getHeaderItemBinding();

        final TimelinePoint point = getCollection().get(position);

        binding.setPoint(point);

        binding.executePendingBindings();
    }


    /**
     * 項目表示保持
     *
     * @param holder 保持
     * @param position 位置
     */
    public void onBindItemViewHolder(final ViewHolder holder, int position) {
        // 項目
        final TimelinePoint item = getCollection().get(position);
        if (item != null) {
            holder.getBinding().title.setText(item.message);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns the total number of items in the data change hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return getCollection().size();
    }

    /**
     * 表示保持
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * @serial バインディング
         */
        private final ItemTimelineBinding mBinding;

        /**
         * コンストラクタ
         *
         * @param view 表示
         */
        ViewHolder(View view) {
            super(view);
            mBinding = null;
        }

        /**
         * コンストラクタ
         *
         * @param binding バインディング
         */
        ViewHolder(ItemTimelineBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        /**
         * バインディング取得
         *
         * @return バインディング
         */
        public ItemTimelineBinding getBinding() {
            return mBinding;
        }

        /**
         * タイムラインヘッダ項目バインディング取得
         *
         * @return タイムラインヘッダ項目バインディング
         */
        public ItemTimelineHeaderBinding getHeaderItemBinding() {
            return null;
        }
    }

    /**
     * ヘッダ表示保持
     */
    static class HeaderViewHolder extends ViewHolder {
        /**
         * @serial バインディング
         */
        private final ItemTimelineHeaderBinding mBinding;

        /**
         * コンストラクタ
         *
         * @param binding バインディング
         */
        HeaderViewHolder(ItemTimelineHeaderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        /**
         * バインディングを取得
         *
         * @return バインディング
         */
        public ItemTimelineBinding getBinding() {
            return null;
        }

        /**
         * ヘッダ項目バインディング取得
         *
         * @return ヘッダ項目バインディング
         */
        public ItemTimelineHeaderBinding getHeaderItemBinding() {
            return mBinding;
        }
    }


}

