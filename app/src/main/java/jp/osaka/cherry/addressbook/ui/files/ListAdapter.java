package jp.osaka.cherry.addressbook.ui.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.adapter.ItemListener;
import jp.osaka.cherry.addressbook.android.view.adapter.RecyclerArrayAdapter;
import jp.osaka.cherry.addressbook.databinding.ItemLineBinding;


/**
 * 一覧アダプタ
 */
class ListAdapter extends RecyclerArrayAdapter<SimpleFile, ListAdapter.BindingHolder> {

    /**
     * @serial レイアウトインフレータ
     */
    private final LayoutInflater mInflater;

    /**
     * @serial リスナ
     */
    private final ItemListener<SimpleFile> mListener;

    /**
     * @serial タッチ表示保持
     */
    private final Collection<View> touchViewHolder = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param context    コンテキスト
     * @param listener   リスナ
     * @param collection 一覧
     */
    ListAdapter(Context context, ItemListener<SimpleFile> listener, List<SimpleFile> collection) {
        super(collection);
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BindingHolder(
                mInflater.inflate(R.layout.item_line, parent, false));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final BindingHolder holder, int position) {
        final SimpleFile item = getCollection().get(position);
        if (item == null) {
            return;
        }

        // タッチの設定
        holder.getBinding().cardView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                touchViewHolder.clear();
                touchViewHolder.add(view);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                for (View holdview : touchViewHolder) {
                    if (holdview.equals(view)) {
                        // 短押し
                        mListener.onClick(view, item);
                    }
                }
                touchViewHolder.clear();
            }
            return false;
        });
        // 短押しの設定
        holder.getBinding().cardView.setOnClickListener(new View.OnClickListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick(View v) {
                // 処理なし
            }
        });
        // 長押しの設定
        holder.getBinding().cardView.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean onLongClick(View v) {
                // タッチ表示保持を解除
                touchViewHolder.clear();
                return true;
            }
        });

        holder.getBinding().buttonPopup.setOnClickListener(new View.OnClickListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick(View v) {
                // 短押し
                mListener.onClickMore(v, item);
            }
        });

        // タイトル
        holder.getBinding().icon2.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
        holder.getBinding().title.setText(item.name);
        holder.getBinding().subtitle.setText(DateFormat.getDateTimeInstance().format(new Date(item.date)));
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
    static class BindingHolder extends RecyclerView.ViewHolder {
        /**
         * バインディング
         */
        private final ItemLineBinding mBinding;

        /**
         * コンストラクタ
         *
         * @param itemView 項目表示
         */
        BindingHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        /**
         * バインディング取得
         *
         * @return バインディング
         */
        public ItemLineBinding getBinding() {
            return mBinding;
        }
    }

}

