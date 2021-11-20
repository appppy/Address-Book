package jp.osaka.cherry.addressbook.service;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;

import jp.osaka.cherry.addressbook.utils.controller.BaseCommand;
import jp.osaka.cherry.addressbook.utils.controller.IWorker;
import jp.osaka.cherry.addressbook.utils.controller.command.Backup;
import jp.osaka.cherry.addressbook.utils.controller.command.Restore;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_ASSETS;


/**
 * アクセサ
 */
class SimpleAccessor implements IWorker {

    /**
     * @serial 目印
     */
    private static final String TAG = "SimpleAccessor";

    /**
     * @serial メッセージの定義
     */
    private enum MESSAGE {
        RESTORE,
        BACKUP;
        /**
         * 値に合致する enum 定数を返す。
         *
         * @param index インデックス
         * @return メッセージ
         */
        public static MESSAGE get(int index) {
            // 値から enum 定数を特定して返す処理
            for (MESSAGE msg : MESSAGE.values()) {
                if (msg.ordinal() == index) {
                    return msg;
                }
            }
            return null; // 特定できない場合
        }
    }

    /**
     * @serial データベース
     */
    private final SimpleDatabase mDatabase;

    /**
     * @serial コールバック
     */
    private final Callbacks mCallbacks;

    /**
     * @serial 自身
     */
    private final SimpleAccessor mSelf;

    /**
     * @serial スレッド
     */
    private final HandlerThread mThread;

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param callbacks コールバック
     */
    SimpleAccessor(Context context, Callbacks callbacks) {

        mCallbacks = callbacks;

        // モデルの生成
        mDatabase = new SimpleDatabase(context);

        mSelf = this;

        mThread = new HandlerThread("SimpleAccessor");
        mThread.start();

        mHandler =  new Handler(mThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 処理開始を通知
                if (mCallbacks != null) {
                    mCallbacks.onStarted(mSelf, (BaseCommand) msg.obj);
                }

                MESSAGE type = MESSAGE.get(msg.what);
                switch(Objects.requireNonNull(type)) {
                    case RESTORE: {
                        Restore command = (Restore) msg.obj;
                        mDatabase.restore();
                        SimpleResult status = new SimpleResult(mDatabase.getAssets());
                        // 処理結果を通知
                        if (mCallbacks != null) {
                            mCallbacks.onUpdated(mSelf, command, status);
                        }
                        if (mCallbacks != null) {
                            mCallbacks.onSuccessed(mSelf, (BaseCommand) msg.obj);
                        }
                        break;
                    }
                    case BACKUP: {
                        Backup command = (Backup) msg.obj;
                        ArrayList<SimpleAsset> collection = command.args.getParcelableArrayList(EXTRA_ASSETS);
                        mDatabase.backup(collection);
                        if (mCallbacks != null) {
                            mCallbacks.onSuccessed(mSelf, (BaseCommand) msg.obj);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BaseCommand command) {
        if (LOG_I) {
            Log.i(TAG, "start#enter");
        }
        if (command instanceof Restore) {
            if (mHandler != null) {
                mHandler.removeMessages(MESSAGE.RESTORE.ordinal());
                Message msg = mHandler.obtainMessage(MESSAGE.RESTORE.ordinal(), command);
                mHandler.sendMessage(msg);
            }
        }
        if (command instanceof Backup) {
            if (mHandler != null) {
                mHandler.removeMessages(MESSAGE.BACKUP.ordinal());
                Message msg = mHandler.obtainMessage(MESSAGE.BACKUP.ordinal(), command);
                mHandler.sendMessage(msg);
            }
        }
        if (LOG_I) {
            Log.i(TAG, "start#enter");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (LOG_I) {
            Log.i(TAG, "stop#enter");
        }
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE.RESTORE.ordinal());
            mHandler.removeMessages(MESSAGE.BACKUP.ordinal());
        }
        if (mThread != null && mThread.isAlive()) {
            mThread.quit();
        }
        if (LOG_I) {
            Log.i(TAG, "stop#enter");
        }
    }
}
