/*
 * Copyright 2021 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.api.service;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.vo.GetTraceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Roy Kim
 */
@Service
public class SpanServiceImpl implements SpanService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TraceDao traceDao;

//    private final SqlMetaDataDao sqlMetaDataDao;
//
//    private final MetaDataFilter metaDataFilter;
//
//    private final ApiMetaDataDao apiMetaDataDao;
//
//    private final StringMetaDataDao stringMetaDataDao;
//
//    private final ServiceTypeRegistryService serviceTypeRegistryService;
//
//    private final SqlParser sqlParser = new DefaultSqlParser();
//    private final OutputParameterParser outputParameterParser = new OutputParameterParser();

    public SpanServiceImpl(@Qualifier("hbaseTraceDaoFactory") TraceDao traceDao
//                           ,SqlMetaDataDao sqlMetaDataDao
//                           ,Optional<MetaDataFilter> metaDataFilter
//                           ,ApiMetaDataDao apiMetaDataDao
//                           ,StringMetaDataDao stringMetaDataDao
//                           ,ServiceTypeRegistryService serviceTypeRegistryService
    ) {
        this.traceDao = Objects.requireNonNull(traceDao, "traceDao");
//        this.sqlMetaDataDao = Objects.requireNonNull(sqlMetaDataDao, "sqlMetaDataDao");
//        this.metaDataFilter = Objects.requireNonNull(metaDataFilter, "metaDataFilter").orElse(null);
//        this.apiMetaDataDao = Objects.requireNonNull(apiMetaDataDao, "apiMetaDataDao");
//        this.stringMetaDataDao = Objects.requireNonNull(stringMetaDataDao, "stringMetaDataDao");
//        this.serviceTypeRegistryService = Objects.requireNonNull(serviceTypeRegistryService, "serviceTypeRegistryService");
    }

    @Override
    public List<List<SpanBo>> selectSpans(List<TransactionId> transactionIdList) {
        Objects.requireNonNull(transactionIdList, "transactionIdList");

        List<GetTraceInfo> getTraceInfoList = new ArrayList<>(transactionIdList.size());
        for (TransactionId transactionId : transactionIdList) {
            getTraceInfoList.add(new GetTraceInfo(transactionId));
        }
//        final List<List<SpanBo>> traceList = traceDao.selectAllSpans(transactionIdList);
        final List<List<SpanBo>> traceList = traceDao.selectSpans(getTraceInfoList);
        logger.info("finished heapSize: " + Runtime.getRuntime().totalMemory() + ", max : " + Runtime.getRuntime().maxMemory()  + ", free : " +  Runtime.getRuntime().freeMemory());
        return traceList;
    }

}

