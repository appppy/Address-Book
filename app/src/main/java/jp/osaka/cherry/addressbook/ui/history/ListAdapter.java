package jp.osaka.cherry.addressbook.ui.history;

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
import jp.osaka.cherry.addressbook.service.history.History;

/**
 * 一覧アダプタ
 */
class ListAdapter extends RecyclerArrayAdapter<History, ListAdapter.BindingHolder> {

    /**
     * @serial レイアウトインフレータ
     */
    private final LayoutInflater mInflater;

    /**
     * @serial リスナ
     */
    private final ItemListener<History> mListener;

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
    ListAdapter(Context context, ItemListener<History> listener, List<History> collection) {
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
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull final BindingHolder holder, int position) {

        // 項目
        final History item = getCollection().get(position);
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
                // タッチ表示保持をクリア
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
        holder.getBinding().icon2.setImageResource(R.drawable.ic_restore_black_24dp);
        holder.getBinding().title.setText(DateFormat.getDateInstance().format(new Date(item.date)) + " " + item.title + " items");
        holder.getBinding().subtitle.setText(DateFormat.getDateTimeInstance().format(new Date(item.date)));
    }

    /**
     * {@inheritDoc}
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
         * @serial バインディング
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

