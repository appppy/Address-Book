
package jp.osaka.cherry.addressbook.service.timeline;

import jp.osaka.cherry.addressbook.service.timeline.TimelinePoint;

interface ITimelineProxy {
    /**
     * 設定
     */
    void setTimeline(in TimelinePoint timelinepoint);
}
