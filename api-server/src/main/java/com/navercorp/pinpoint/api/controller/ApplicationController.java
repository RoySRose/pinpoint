package com.navercorp.pinpoint.api.controller;

import com.navercorp.pinpoint.api.bo.SpanBoResult;
import com.navercorp.pinpoint.api.service.SpanService;
import com.navercorp.pinpoint.api.service.TransactionService;
import com.navercorp.pinpoint.api.util.DateUtil;
import com.navercorp.pinpoint.apiserver.api.ApplicationApi;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.web.util.LimitUtils;
import com.navercorp.pinpoint.web.util.ListListUtils;
import com.navercorp.pinpoint.web.vo.GetTraceInfoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Roy Kim
 */

@Controller
public class ApplicationController implements ApplicationApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    private CommonService commonService;

//    @Autowired
//    private ScatterChartService scatter;

//    @Autowired
//    private TransactionInfoService transactionInfoService;

    @Autowired
    private TransactionService transactionService;

//    @Autowired
//    private LogConfiguration logConfiguration;

    @Autowired
    private SpanService spanService;


    private final int limit = 5000;

    private final GetTraceInfoParser getTraceInfoParser = new GetTraceInfoParser();

//    @Override
//    public ResponseEntity<Object> getApplicationAgentsList() {
//
//        List<Application> applicationList = commonService.selectAllApplicationNames();
//        logger.debug("/api/applications {}", applicationList);
//
//        return ResponseEntity.ok(new ApplicationGroup(applicationList));
//    }


    @RequestMapping(value = "/getTransanctionList", method = RequestMethod.GET, params = "fromDate")
    @ResponseBody
    public List<TransactionId> getTransanctionList(
            @RequestParam("application") String application
            , @RequestParam("fromDate") String from
            , @RequestParam("toDate") String to) {
        logger.info("GET /getTransanctionList params {application={}, from={}, to={}, limit={}}", application, from, to, limit);

        long fromDate = DateUtil.stringToLong(from);
        long toDate = DateUtil.stringToLong(to);
        logger.info("GET params {fromDate={}, toDate={}}", fromDate, toDate);

        List<TransactionId> transactionIdList = getTransactionList(application, fromDate, toDate);
        return transactionIdList;
    }

    @RequestMapping(value = "/getTransanctionList", method = RequestMethod.GET, params = "from")
    @ResponseBody
    public List<TransactionId> getTransanctionList(
            @RequestParam("application") String application
            , @RequestParam("from") long from
            , @RequestParam("to") long to) {
        System.out.println("Test1");
        logger.info("GET /getTransanctionList params {application={}, from={}, to={}, limit={}}", application, from, to, limit);

        List<TransactionId> transactionIdList = getTransactionList(application, from, to);
        return transactionIdList;
    }

    @RequestMapping(value = "/getTransanctionListInfo", method = RequestMethod.GET, params = "fromDate")
    @ResponseBody
    public List<SpanBoResult> getTransanctionListInfo(
            @RequestParam("application") String application
            , @RequestParam("fromDate") String from
            , @RequestParam("toDate") String to) {

        LimitUtils.checkRange(limit);

        logger.info("GET /getTransanctionListInfoString params {application={}, from={}, to={}, limit={}}", application, from, to, limit);

        long fromDate = DateUtil.stringToLong(from);
        long toDate = DateUtil.stringToLong(to);
        logger.info("GET params {fromDate={}, toDate={}}", fromDate, toDate);

        List<TransactionId> transactionIdList = getTransactionList(application, fromDate, toDate);
        return getTransanctionListSpanDetail(transactionIdList);
    }

    @RequestMapping(value = "/getTransanctionListInfo", method = RequestMethod.GET, params = "from")
    @ResponseBody
    public List<SpanBoResult> getTransanctionListInfo(
            @RequestParam("application") String application
            , @RequestParam("from") long from
            , @RequestParam("to") long to) {

        logger.info("GET /getTransanctionListInfoLong params {application={}, from={}, to={}, limit={}}", application, from, to, limit);

        List<TransactionId> transactionIdList = getTransactionList(application, from, to);
        return getTransanctionListSpanDetail(transactionIdList);
    }

    private List<TransactionId> getTransactionList(String application, long from, long to) {
        logger.info("start heapSize: " + Runtime.getRuntime().totalMemory() + ", max : " + Runtime.getRuntime().maxMemory() + ", free : " + Runtime.getRuntime().freeMemory());
        final long start = System.currentTimeMillis();

        List<TransactionId> result = transactionService.selectTransanctionList(application, from, to, limit);

        final long time = System.currentTimeMillis() - start;

        logger.info("getTransactionList execution time:{}ms ", time);
        return result;
    }

    private List<SpanBoResult> getTransanctionListSpanDetail(List<TransactionId> transactionIdList) {
        logger.info("transactionIdList size={} ", transactionIdList.size());
        logger.info("finished 2nd: " + Runtime.getRuntime().totalMemory() + ", max : " + Runtime.getRuntime().maxMemory() + ", free : " + Runtime.getRuntime().freeMemory());
        if (CollectionUtils.isEmpty(transactionIdList)) {
            return Collections.emptyList();
        }

        final long start = System.currentTimeMillis();

        List<List<SpanBo>> selectedSpans = spanService.selectSpans(transactionIdList);


        List<SpanBo> result = ListListUtils.toList(selectedSpans, transactionIdList.size());
        logger.info("getTransanctionListSpanDetail finished selectedSpans retrieved size={} ", result.size());
        final long time = System.currentTimeMillis() - start;
        logger.info("getTransanctionListSpanDetail execution time:{}ms ", time);

        List<SpanBoResult> spanBoResultList = transform(result);
        final long time2 = System.currentTimeMillis() - start;
        logger.info("transform execution time:{}ms ", time2);

        return spanBoResultList;
    }

    private List<SpanBoResult> transform(List<SpanBo> result) {

        List<SpanBoResult> list = new ArrayList<>();
        for(SpanBo spanBo : result){
            SpanBoResult spanBoResult = new SpanBoResult(spanBo);
            list.add(spanBoResult);
        }

        return list;
    }

}
