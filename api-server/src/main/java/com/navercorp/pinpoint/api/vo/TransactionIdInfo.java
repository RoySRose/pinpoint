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

package com.navercorp.pinpoint.api.vo;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;

import java.util.Objects;

/**
 * @author Roy Kim
 */

public class TransactionIdInfo {

    private final TransactionId transactionId;
    private final long acceptedTime;

    public TransactionIdInfo(TransactionId transactionId, long acceptedTime) {
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId");
        this.acceptedTime = acceptedTime;
    }

    public TransactionId getTransactionId() {
        return transactionId;
    }

    public long getAcceptedTime() {
        return acceptedTime;
    }

    @Override
    public String toString() {
        return "TransactionIdInfo{" +
                "transactionId=" + transactionId +
                ", acceptedTime=" + acceptedTime +
                '}';
    }
}
