package com.navercorp.pinpoint.api.controller;

import com.navercorp.pinpoint.api.service.SpanService;
import com.navercorp.pinpoint.api.service.TransactionService;
import com.navercorp.pinpoint.api.util.DateUtil;
import com.navercorp.pinpoint.apiserver.api.ApplicationApi;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.web.config.LogConfiguration;
import com.navercorp.pinpoint.web.service.CommonService;
import com.navercorp.pinpoint.web.service.ScatterChartService;
import com.navercorp.pinpoint.web.service.TransactionInfoService;
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

import java.util.Collections;
import java.util.List;

/**
 * @author Roy Kim
 */

@Controller
public class ApplicationController implements ApplicationApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonService commonService;

    @Autowired
    private ScatterChartService scatter;

    @Autowired
    private TransactionInfoService transactionInfoService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LogConfiguration logConfiguration;

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
        logger.debug("GET /getTransanctionList params {application={}, from={}, to={}, limit={}}", application, from, to, limit);

        long fromDate = DateUtil.stringToLong(from);
        long toDate = DateUtil.stringToLong(to);
        logger.debug("GET params {fromDate={}, toDate={}}", fromDate, toDate);

        List<TransactionId> transactionIdList = retrieveTransactionList(application, fromDate, toDate);
        return transactionIdList;
    }

    @RequestMapping(value = "/getTransanctionList", method = RequestMethod.GET, params = "from")
    @ResponseBody
    public List<TransactionId> getTransanctionList(
            @RequestParam("application") String application
            , @RequestParam("from") long from
            , @RequestParam("to") long to) {
        System.out.println("Test1");
        logger.debug("GET /getTransanctionList params {application={}, from={}, to={}, limit={}}", application, from, to, limit);

        List<TransactionId> transactionIdList = retrieveTransactionList(application, from, to);
        return transactionIdList;
    }

    @RequestMapping(value = "/getTransanctionListInfo", method = RequestMethod.GET, params = "fromDate")
    @ResponseBody
    public List<SpanBo> getTransanctionListInfo(
            @RequestParam("application") String application
            , @RequestParam("fromDate") String from
            , @RequestParam("toDate") String to) {

        LimitUtils.checkRange(limit);

        logger.debug("GET /getTransanctionListInfoString params {application={}, from={}, to={}, limit={}}", application, from, to, limit);

        long fromDate = DateUtil.stringToLong(from);
        long toDate = DateUtil.stringToLong(to);
        logger.debug("GET params {fromDate={}, toDate={}}", fromDate, toDate);

        List<TransactionId> transactionIdList = retrieveTransactionList(application, fromDate, toDate);
        return getTransanctionListSpanDetail(transactionIdList);
    }

    @RequestMapping(value = "/getTransanctionListInfo", method = RequestMethod.GET, params = "from")
    @ResponseBody
    public List<SpanBo> getTransanctionListInfo(
            @RequestParam("application") String application
            , @RequestParam("from") long from
            , @RequestParam("to") long to) {

        logger.debug("GET /getTransanctionListInfoLong params {application={}, from={}, to={}, limit={}}", application, from, to, limit);

        List<TransactionId> transactionIdList = retrieveTransactionList(application, from, to);
        return getTransanctionListSpanDetail(transactionIdList);
    }

    private List<TransactionId> retrieveTransactionList(String application, long from, long to) {

        return transactionService.selectTransanctionList(application, from, to, limit);
    }

    private List<SpanBo> getTransanctionListSpanDetail(List<TransactionId> transactionIdList) {
        logger.debug("transactionIdList size={} ", transactionIdList.size());

        if (CollectionUtils.isEmpty(transactionIdList)) {
            return Collections.emptyList();
        }

        List<List<SpanBo>> selectedSpans = spanService.selectSpans(transactionIdList);

        logger.debug("getTransanctionListSpanDetail finished retrieved size={} ", transactionIdList.size());
        return ListListUtils.toList(selectedSpans, transactionIdList.size());

//        return null;
    }

}
