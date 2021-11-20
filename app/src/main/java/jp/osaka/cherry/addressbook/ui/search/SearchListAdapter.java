package jp.osaka.cherry.addressbook.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.adapter.ItemListener;
import jp.osaka.cherry.addressbook.android.view.adapter.RecyclerArrayAdapter;
import jp.osaka.cherry.addressbook.databinding.ItemLineBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;

import static android.view.View.GONE;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;

/**
 * 検索一覧アダプタ
 */
class SearchListAdapter extends RecyclerArrayAdapter<SimpleAsset, SearchListAdapter.BindingHolder> {

    /**
     * @serial レイアウトインフレータ
     */
    private final LayoutInflater mInflater;

    /**
     * @serial リスナ
     */
    private final ItemListener<SimpleAsset> mListener;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param listener   リスナ
     * @param collection 一覧
     */
    SearchListAdapter(Context context, ItemListener<SimpleAsset> listener, List<SimpleAsset> collection) {
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
    @Override
    public void onBindViewHolder(@NonNull final BindingHolder holder, int position) {
        // 項目
        final SimpleAsset item = getCollection().get(position);
        if (item != null) {
            // タッチの設定
            // 短押しの設定
            holder.getBinding().cardView.setOnClickListener(new View.OnClickListener() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onClick(View view) {
                    // 短押し
                    mListener.onClick(view, item);
                }
            });

            // アイコン
            holder.getBinding().iconView.setVisibility(View.GONE);
            holder.getBinding().icon2.setVisibility(View.INVISIBLE);

            // タイトル
            holder.getBinding().title.setText(item.title);
            // サブタイトル
            if(item.subtitle.equals(INVALID_STRING_VALUE)) {
                holder.getBinding().title.setPadding(0,8,0,0);
                holder.getBinding().subtitle.setVisibility(GONE);
            } else {
                holder.getBinding().title.setPadding(0,0,0,0);
                holder.getBinding().subtitle.setText(item.subtitle);
            }

            // Moreの設定
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
        }
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

        BindingHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public ItemLineBinding getBinding() {
            return mBinding;
        }
    }
}

