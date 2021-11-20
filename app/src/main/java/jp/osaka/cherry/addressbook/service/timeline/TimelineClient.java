
package jp.osaka.cherry.addressbook.service.timeline;

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
 * タイムラインクライアント
 */
public class TimelineClient implements IClient {

    /**
     * @serial 目印
     */
    private static final String TAG = "TimelineClient";

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
    private ITimelineService mBinder;

    /**
     * @serial 自身
     */
    private final TimelineClient mSelf;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public TimelineClient(Context context, Callbacks callbacks) {
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
            mBinder = ITimelineService.Stub.asInterface(service);
            try {
                mBinder.registerCallback(mCallback);
                mBinder.getTimeline();
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
    private final ITimelineServiceCallback mCallback = new ITimelineServiceCallback.Stub() {
        /**
         * 更新
         *
         * @param timeline タイムライン
         */
        @Override
        public void update(List<TimelinePoint> timeline) {
            if (mCallbacks != null) {
                mCallbacks.onUpdatedTimeline(mSelf, timeline);
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
                Intent intent = new Intent(INTERFACE.ITimelineService);
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
         * @param timeline タイムライン
         */
        void onUpdatedTimeline(Object object, List<TimelinePoint> timeline);
    }
}
