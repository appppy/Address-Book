
package jp.osaka.cherry.addressbook.service.history;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import jp.osaka.cherry.addressbook.android.IClient;
import jp.osaka.cherry.addressbook.constants.INTERFACE;

import static jp.osaka.cherry.addressbook.Config.LOG_I;

/**
 * 履歴クライアント
 */
public class HistoryClient implements IClient {
    /**
     * @serial 目印
     */
    private static final String TAG = "HistoryClient";

    /**
     * @serial コンテキスト
     */
    private final Context mContext;

    /**
     * @serial コールバック
     */
    private static Callbacks mCallbacks;

    /**
     * @serial インタフェース
     */
    private IHistoryService mBinder;

    /**
     * @serial 自身
     */
    private final HistoryClient mSelf;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public HistoryClient(Context context, Callbacks callbacks) {
        mSelf = this;
        mContext = context;
        mCallbacks = callbacks;
    }

    /** コネクション */
    private final ServiceConnection mConnection = new ServiceConnection() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = IHistoryService.Stub.asInterface(service);
            try {
                mBinder.registerCallback(mCallback);
                mBinder.getHistoryList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
            mContext.unbindService(mConnection);
        }
    };

    /**
     * @serial コールバック
     */
    private final IHistoryServiceCallback mCallback = new IHistoryServiceCallback.Stub() {
        /**
         * 更新
         *
         * @param historyList 履歴一覧
         */
        @Override
        public void update(List<History> historyList) {
            if (mCallbacks != null) {
                mCallbacks.onUpdatedHistoryList(mSelf, historyList);
            }
        }
    };

    /**
     * 接続
     */
    public void connect() {
        if (LOG_I) {
            Log.i(TAG, "connect#enter");
        }
        if (mBinder == null) {
            try {
                Intent intent = new Intent(INTERFACE.IHistoryService);
                intent.setPackage("jp.osaka.cherry.addressbook");
                mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (LOG_I) {
            Log.i(TAG, "connect#leave");
        }
    }

    /**
     * 解除
     */
    public void disconnect() {
        if (LOG_I) {
            Log.i(TAG, "disconnect#enter");
        }
        if (mBinder != null) {
            try {
                mBinder.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBinder = null;
        }
        if (mContext != null) {
            try {
                mContext.unbindService(mConnection);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (LOG_I) {
            Log.i(TAG, "disconnect#leave");
        }
    }

    /**
     * 履歴の設定
     *
     */
    public void clear() {
        if (LOG_I) {
            Log.i(TAG, "clear#enter");
        }
        if (mBinder != null) {
            try {
                mBinder.delete();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (LOG_I) {
            Log.i(TAG, "clear#leave");
        }
    }

    /**
     * コールバックインタフェース
     */
    public interface Callbacks {
        /**
         * コマンド更新通知
         *
         * @param object オブジェクト
         * @param historyList 履歴一覧
         */
        void onUpdatedHistoryList(Object object, List<History> historyList);
    }
}
