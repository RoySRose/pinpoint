
/*
 * Copyright 2021 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.api.service;

import com.navercorp.pinpoint.api.dao.ApplicationTraceListDao;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Roy Kim
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ApplicationTraceListDao applicationTraceListDao;

    public TransactionServiceImpl(ApplicationTraceListDao applicationTraceListDao) {
        this.applicationTraceListDao = Objects.requireNonNull(applicationTraceListDao, "applicationTraceListDao");
//        this.traceDao = Objects.requireNonNull(traceDao, "traceDao");
    }


    @Override
    public List<TransactionId> selectTransanctionList(String applicationName, long from, long to, int limit) {

        Objects.requireNonNull(applicationName, "applicationName");
        Objects.requireNonNull(from, "range");
        Objects.requireNonNull(to, "range");
//        LimitTransactionIdedScanResult<List<TransactionId>> scanResult = applicationTraceListDao.scanTraceList(applicationName, range, limit, backwardDirection);
        List<TransactionId> scanResult = applicationTraceListDao.scanTraceList(applicationName, from, to, limit);//, backwardDirection);

//        ScatterDataBuilder builder = new ScatterDataBuilder(range.getFrom(), range.getTo(), xGroupUnit, yGroupUnit);
//        builder.addDot(scanResult.getScanData());

        return scanResult;
    }
}
