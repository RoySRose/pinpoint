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

import com.navercorp.pinpoint.api.bo.AggregatedTransaction;
import com.navercorp.pinpoint.api.bo.SpanBoResult;
import com.navercorp.pinpoint.api.postprocess.TransactionPostProcessor;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.util.ListListUtils;
import com.navercorp.pinpoint.web.vo.GetTraceInfo;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Roy Kim
 */
@Service
public class SpanServiceImpl implements SpanService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TraceDao traceDao;

    public SpanServiceImpl(@Qualifier("hbaseTraceDaoFactory") TraceDao traceDao
    ) {
        this.traceDao = Objects.requireNonNull(traceDao, "traceDao");
    }

    @Override
    public List<SpanBoResult> selectSpans(List<TransactionId> transactionIdList) {
        Objects.requireNonNull(transactionIdList, "transactionIdList");

        final long start = System.currentTimeMillis();


        List<GetTraceInfo> getTraceInfoList = new ArrayList<>(transactionIdList.size());
        for (TransactionId transactionId : transactionIdList) {
            getTraceInfoList.add(new GetTraceInfo(transactionId));
        }
//        final List<List<SpanBo>> traceList = traceDao.selectAllSpans(transactionIdList);
        final List<List<SpanBo>> traceList = traceDao.selectSpans(getTraceInfoList);

        final long time = System.currentTimeMillis() - start;
        logger.info("getTransanctionListSpanDetail execution time:{}ms ", time);
        logger.info("finished heapSize: " + Runtime.getRuntime().totalMemory() + ", max : " + Runtime.getRuntime().maxMemory()  + ", free : " +  Runtime.getRuntime().freeMemory());

        List<List<SpanBoResult>> spanBoResultDoubleList = transform(traceList);

        writeToCsv(spanBoResultDoubleList);

        List<SpanBoResult> spanBoResultList = ListListUtils.toList(spanBoResultDoubleList, transactionIdList.size());
        logger.info("txidlist size : " + transactionIdList.size() + " finished2 heapSize: " + Runtime.getRuntime().totalMemory() + ", max : " + Runtime.getRuntime().maxMemory()  + ", free : " +  Runtime.getRuntime().freeMemory());

        return spanBoResultList;
    }

    private void writeToCsv(List<List<SpanBoResult>> spanBoResultDoubleList) {
        TransactionPostProcessor transactionPostProcessor = new TransactionPostProcessor(spanBoResultDoubleList);
        transactionPostProcessor.postProcess();


        final String CSV_LOCATION = "/Users/user/Desktop/machinelearning/aiops/pinpoint/data/aggregated.csv";
        FileWriter writer = null;
        try {
            writer = new FileWriter(CSV_LOCATION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
//                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withApplyQuotesToAll(false)
                .withEscapechar(CSVWriter.NO_ESCAPE_CHARACTER)
                .withSeparator(',').build();

        List<AggregatedTransaction> aggregatedTransactions = transactionPostProcessor.getAggregatedTransactionList();
        logger.info("aggregatedTransactions size : {} ", aggregatedTransactions.size());

        try {
            beanToCsv.write(aggregatedTransactions);
            writer.flush();
            writer.close();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SpanBo> selectOneSpan(List<TransactionId> transactionIdList) {
        Objects.requireNonNull(transactionIdList, "transactionIdList");

        final long start = System.currentTimeMillis();


        List<GetTraceInfo> getTraceInfoList = new ArrayList<>(transactionIdList.size());
        for (TransactionId transactionId : transactionIdList) {
            getTraceInfoList.add(new GetTraceInfo(transactionId));
        }
        final List<List<SpanBo>> traceList = traceDao.selectSpans(getTraceInfoList);

        List<List<SpanBoResult>> spanBoResultDoubleList = transform(traceList);

        writeToCsv(spanBoResultDoubleList);

        final long time = System.currentTimeMillis() - start;
        logger.info("getTransanctionListSpanDetail execution time:{}ms ", time);
        logger.info("finished heapSize: " + Runtime.getRuntime().totalMemory() + ", max : " + Runtime.getRuntime().maxMemory()  + ", free : " +  Runtime.getRuntime().freeMemory());

//        List<List<SpanBoResult>> spanBoResultDoubleList = transform(traceList);
//
//        TransactionPostProcessor transactionPostProcessor = new TransactionPostProcessor(spanBoResultDoubleList);
//        transactionPostProcessor.postProcess();

        List<SpanBo> spanBoList = ListListUtils.toList(traceList, transactionIdList.size());
//        List<SpanBoResult> spanBoResultList = transform(spanBoList);

        return spanBoList;
    }

    private List<List<SpanBoResult>> transform(List<List<SpanBo>> source) {

        List<List<SpanBoResult>> result = new ArrayList<>(source.size());
        for(List<SpanBo> spanBoList : source){

            List<SpanBoResult> spanBoResultList = new ArrayList<>(spanBoList.size());
            for(SpanBo spanBo : spanBoList){
                SpanBoResult spanBoResult = new SpanBoResult(spanBo);
                spanBoResultList.add(spanBoResult);
            }
            result.add(spanBoResultList);
        }

        return result;
    }

}

