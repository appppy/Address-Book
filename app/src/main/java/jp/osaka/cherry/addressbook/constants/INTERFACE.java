/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.osaka.cherry.addressbook.constants;

/**
 * インターフェース
 */
public final class INTERFACE {
    /**
     * @serial サービスIF
     */
    public static final String ISimpleService = "jp.osaka.cherry.addressbook.service.ISimpleService";

    /**
     * @serial 履歴IF
     */
    public static final String IHistoryProxy = "jp.osaka.cherry.addressbook.service.history.IHistoryProxy";

    /**
     * @serial 履歴サービスIF
     */
    public static final String IHistoryService = "jp.osaka.cherry.addressbook.service.history.IHistoryService";

    /**
     * @serial タイムラインIF
     */
    public static final String ITimelineProxy = "jp.osaka.cherry.addressbook.service.timeline.ITimelineProxy";

    /**
     * @serial タイムラインサービスIF
     */
    public static final String ITimelineService = "jp.osaka.cherry.addressbook.service.timeline.ITimelineService";
}
