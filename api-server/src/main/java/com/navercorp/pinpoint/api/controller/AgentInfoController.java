package com.navercorp.pinpoint.api.controller;

import com.navercorp.pinpoint.web.service.AgentInfoService;
import com.navercorp.pinpoint.web.vo.AgentInfoFilter;
import com.navercorp.pinpoint.web.vo.ApplicationAgentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Roy Kim
 */

@Controller
public class AgentInfoController {

    private static final int CODE_SUCCESS = 0;
    private static final int CODE_FAIL = -1;

    @Autowired
    private AgentInfoService agentInfoService;

//    @Autowired
//    private AgentEventService agentEventService;

    @RequestMapping(value = "/getAgentList", method = RequestMethod.GET)
    @ResponseBody
    public ApplicationAgentsList getAgentList() {
        System.out.println("TESTSETSET");
        long timestamp = System.currentTimeMillis();
        return getAgentList(timestamp);
    }

    //    @PreAuthorize("hasPermission(#applicationName, 'application', 'inspector')")
    @RequestMapping(value = "/getAgentList", method = RequestMethod.GET, params = {"!application", "timestamp"})
    @ResponseBody
    public ApplicationAgentsList getAgentList(
            @RequestParam("timestamp") long timestamp) {
        return this.agentInfoService.getAllApplicationAgentsList(AgentInfoFilter::accept, timestamp);
    }
//
//    @RequestMapping(value = "/getAgentList", method = RequestMethod.GET, params = {"application"})
//    @ResponseBody
//    public ApplicationAgentsList getAgentList(@RequestParam("application") String applicationName) {
//        long timestamp = System.currentTimeMillis();
//        return getAgentList(applicationName, timestamp);
//    }

}
