
package jp.osaka.cherry.addressbook.service.timeline;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.osaka.cherry.addressbook.constants.INTERFACE;
import jp.osaka.cherry.addressbook.service.history.History;
import jp.osaka.cherry.addressbook.service.history.IHistoryProxy;
import jp.osaka.cherry.addressbook.utils.controller.BaseCommand;
import jp.osaka.cherry.addressbook.utils.controller.Controller;
import jp.osaka.cherry.addressbook.utils.controller.IWorker;
import jp.osaka.cherry.addressbook.utils.controller.command.Backup;
import jp.osaka.cherry.addressbook.utils.controller.command.Delete;
import jp.osaka.cherry.addressbook.utils.controller.command.Restore;
import jp.osaka.cherry.addressbook.utils.controller.status.BaseStatus;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_HISTORY;


/**
 * タイムラインサービス
 */
public class TimelineService extends Service implements
        IWorker.Callbacks {

    /**
     * @serial 目印
     */
    private static final String TAG = "TimelineService";

    /**
     * @serial コールバック一覧
     */
    private final RemoteCallbackList<ITimelineServiceCallback> mCallbacks =
            new RemoteCallbackList<>();

    /**
     * @serial コントローラ
     */
    private final Controller mController = new Controller();

    /**
     * @serial インタフェース
     */
    private final ITimelineService.Stub mBinder = new ITimelineService.Stub() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void registerCallback(ITimelineServiceCallback callback) {
            mCallbacks.register(callback);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void unregisterCallback(ITimelineServiceCallback callback) {
            mCallbacks.unregister(callback);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void getTimeline() {
            // リストア
            mController.start(new Restore());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void delete() {
            // リストア
            mController.start(new Delete());
        }
    };

    /**
     * @serial インタフェース
     */
    private final IHistoryProxy.Stub mProxy = new IHistoryProxy.Stub() {

        /**
         * 設定
         *
         * @param history 履歴
         */
        @Override
        public void setHistory(History history) {
            // バックアップ
            BaseCommand command = new Backup();
            command.args.putParcelable(EXTRA_HISTORY, history);
            mController.start(command);
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // 登録
        mController.register(new TimelineWorker(this, this));


        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        if (LOG_I) {
            Log.i(TAG, "onDestroy#enter");
        }
        // コールバックの削除
        mCallbacks.kill();

        // 停止
        mController.stop();

        // 解除
        mController.unregisterAll();

        if (LOG_I) {
            Log.i(TAG, "onDestroy#leave");
        }
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        IBinder result = null;
        if (INTERFACE.ITimelineProxy.equals(intent.getAction())) {
            result = mProxy;
        }
        if (INTERFACE.ITimelineService.equals(intent.getAction())) {
            result = mBinder;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStarted(IWorker worker, BaseCommand command) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdated(IWorker worker, BaseCommand command, BaseStatus status) {
        try {
            // データベースコントローラー
            if (worker instanceof TimelineWorker) {
                // コマンド
                if (command instanceof Restore) {
                    // リストア
                    if (status instanceof Timeline) {
                        Timeline s = (Timeline) status;
                        // ブロードキャスト
                        broadcast(s.collection);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccessed(IWorker worker, BaseCommand command) {
    }

    /**
     * ブロードキャスト
     *
     * @param collection 一覧
     */
    public void broadcast(Collection<TimelinePoint> collection) {
        List<TimelinePoint> list = new ArrayList<>(collection);
        int n = mCallbacks.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                mCallbacks.getBroadcastItem(i).update(list);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
    }
}
