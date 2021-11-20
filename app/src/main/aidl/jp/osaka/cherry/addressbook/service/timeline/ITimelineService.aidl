package jp.osaka.cherry.addressbook.service.timeline;

import jp.osaka.cherry.addressbook.service.timeline.ITimelineServiceCallback;
import jp.osaka.cherry.addressbook.service.timeline.TimelinePoint;

interface ITimelineService {
    /**
     * コールバック登録
     */
    void registerCallback(ITimelineServiceCallback callback);

    /**
     * コールバック解除
     */
    void unregisterCallback(ITimelineServiceCallback callback);

    /**
     * 取得
     */
    void getTimeline();

    /**
     * 削除
     */
    void delete();
}
