/*
 *  Copyright 2021 NAVER Corp.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/*
 *  Copyright 2021 NAVER Corp.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.navercorp.pinpoint.api.transactionid;

import com.navercorp.pinpoint.api.vo.TransactionIdInfo;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roy Kim
 */
public class TransactionIdListBuilder {

//    private final long from;
//    private final long to;
//    private final int xGroupUnitMillis;
//    private final int yGroupUnitMillis;

//    private ScatterAgentMetadataRepository scatterAgentMetadataRepository = new ScatterAgentMetadataRepository();
//    private Map<Long, DotGroups> scatterData = new HashMap<>();

    private List<TransactionId> transactionIdList = new ArrayList<>();

    private long oldestAcceptedTime = Long.MAX_VALUE;
//    private long latestAcceptedTime = Long.MIN_VALUE;

    public TransactionIdListBuilder() {
//        if (from <= 0) {
//            throw new IllegalArgumentException("from value must be higher than 0");
//        }
//        if (from > to) {
//            throw new IllegalArgumentException("from value must be lower or equal to to value");
//        }
//
//        this.from = from;
//        this.to = to;
//        this.xGroupUnitMillis = xGroupUnitMillis;
//        this.yGroupUnitMillis = yGroupUnitMillis;
    }

    public void addTransactionIdList(List<TransactionIdInfo> transactionIdList) {
        for (TransactionIdInfo transactionIdInfo : transactionIdList) {
            addTransactionId(transactionIdInfo);
        }
    }

    public List<TransactionId> getTransactionIdList() {
        return transactionIdList;
    }

    public long getOldestAcceptedTime() {
        return oldestAcceptedTime;
    }

    private void addTransactionId(TransactionIdInfo transactionIdInfo) {
        if (transactionIdInfo == null) {
            return;
        }

//        long acceptedTimeDiff = dot.getAcceptedTime() - from;
//        long x = acceptedTimeDiff - (acceptedTimeDiff  % xGroupUnitMillis);
//        if (x < 0) {
//            x = 0L;
//        }
//        int y = dot.getElapsedTime() - (dot.getElapsedTime() % yGroupUnitMillis);
//
//        Coordinates coordinates = new Coordinates(x, y);
//        addDot(coordinates, new Dot(dot.getTransactionId(), acceptedTimeDiff, dot.getElapsedTime(), dot.getExceptionCode(), dot.getAgentId()));

        transactionIdList.add(transactionIdInfo.getTransactionId());

        oldestAcceptedTime = Math.min(oldestAcceptedTime, transactionIdInfo.getAcceptedTime());
//        latestAcceptedTime = Math.max(latestAcceptedTime, transactionIdInfo.getAcceptedTime());
    }

    public List<TransactionId> build() {

        return transactionIdList;
    }
}
