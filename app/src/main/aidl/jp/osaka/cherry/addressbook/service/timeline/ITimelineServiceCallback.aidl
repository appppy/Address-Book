package jp.osaka.cherry.addressbook.service.timeline;

import jp.osaka.cherry.addressbook.service.timeline.TimelinePoint;

interface ITimelineServiceCallback {
    /**
     * 更新
     */
    void update(in List<TimelinePoint> timeline);
}
