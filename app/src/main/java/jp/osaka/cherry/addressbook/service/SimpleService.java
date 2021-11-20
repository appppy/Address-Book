
package jp.osaka.cherry.addressbook.service;

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
import jp.osaka.cherry.addressbook.utils.controller.BaseCommand;
import jp.osaka.cherry.addressbook.utils.controller.Controller;
import jp.osaka.cherry.addressbook.utils.controller.IWorker;
import jp.osaka.cherry.addressbook.utils.controller.command.Backup;
import jp.osaka.cherry.addressbook.utils.controller.command.Restore;
import jp.osaka.cherry.addressbook.utils.controller.status.BaseStatus;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_ASSETS;


/**
 * サービス
 */
public class SimpleService extends Service implements
        IWorker.Callbacks {

    /**
     * @serial 目印
     */
    private static final String TAG = "SimpleService";

    /**
     * @serial コールバック一覧
     */
    private final RemoteCallbackList<ISimpleServiceCallback> mCallbacks =
            new RemoteCallbackList<>();

    /**
     * @serial 制御
     */
    private final Controller mController = new Controller();


    /**
     * @serial インタフェース
     */
    private final ISimpleService.Stub mBinder = new ISimpleService.Stub() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void registerCallback(ISimpleServiceCallback callback) {
            mCallbacks.register(callback);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void unregisterCallback(ISimpleServiceCallback callback) {
            mCallbacks.unregister(callback);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setList(List<SimpleAsset> list) {
            // バックアップ
            BaseCommand command = new Backup();
            ArrayList<SimpleAsset> arrayList = new ArrayList<>(list);
            command.args.putParcelableArrayList(EXTRA_ASSETS, arrayList);
            mController.start(command);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void getList() {
            // リストア
            mController.start(new Restore());
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
        mController.register(new SimpleAccessor(this, this));


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
        if (INTERFACE.ISimpleService.equals(intent.getAction())) {
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
            if (worker instanceof SimpleAccessor) {
                // コマンド
                if (command instanceof Restore) {
                    // リストア
                    if (status instanceof SimpleResult) {
                        SimpleResult s = (SimpleResult) status;
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
    public void broadcast(Collection<SimpleAsset> collection) {
        List<SimpleAsset> list = new ArrayList<>(collection);
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
