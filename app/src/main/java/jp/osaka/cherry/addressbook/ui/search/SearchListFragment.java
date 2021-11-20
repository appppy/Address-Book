package jp.osaka.cherry.addressbook.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.DividerItemDecoration;
import jp.osaka.cherry.addressbook.android.view.ISearchCollectionCallbacks;
import jp.osaka.cherry.addressbook.android.view.ISearchCollectionView;
import jp.osaka.cherry.addressbook.android.view.adapter.ItemListener;
import jp.osaka.cherry.addressbook.databinding.FragmentBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LIST;

/**
 * 検索一覧フラグメント
 */
public class SearchListFragment extends Fragment implements
        ISearchCollectionView<SimpleAsset>,
        ItemListener<SimpleAsset> {

    /**
     * @serial データバインディング
     */
    private FragmentBinding mBinding;

    /**
     * @serial コールバック
     */
    private ISearchCollectionCallbacks<SimpleAsset> mCallbacks;

    /**
     * @serial アダプタ
     */
    private SearchListAdapter mAdapter;

    /**
     * インスタンス取得
     *
     * @param collection カード
     */
    public static SearchListFragment newInstance(ArrayList<SimpleAsset> collection) {
        // フラグメントの生成
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_LIST, collection);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof ISearchCollectionCallbacks)) {
            throw new ClassCastException(requireActivity().toString()
                    + " must implement Callbacks");
        }

        mCallbacks = (ISearchCollectionCallbacks<SimpleAsset>) getActivity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String TAG = "SearchListFragment";
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

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
        // データ取得
        ArrayList<SimpleAsset> list = requireArguments().getParcelableArrayList(EXTRA_LIST);
        // データバインディング取得
        mBinding = DataBindingUtil.bind(requireView());
        // アダプタの設定
        mAdapter = new SearchListAdapter(getContext(), this, list);
        // レイアウトの設定
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        if (mBinding != null) {
            mBinding.collection.addItemDecoration(new DividerItemDecoration(requireActivity()));
            mBinding.collection.setLayoutManager(layoutManager);
            mBinding.collection.setAdapter(mAdapter);
            mBinding.collection.setItemAnimator(new DefaultItemAnimator());
            mBinding.collection.setVerticalScrollBarEnabled(false);
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
        // 処理なし
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

}