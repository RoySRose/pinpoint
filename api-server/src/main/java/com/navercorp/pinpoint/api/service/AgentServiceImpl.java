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

import com.navercorp.pinpoint.rpc.util.ListUtils;
import com.navercorp.pinpoint.web.service.AgentInfoService;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Roy Kim
 */
@Service
public class AgentServiceImpl implements AgentService {

    private static final long DEFAULT_FUTURE_TIMEOUT = 3000;

    private long timeDiffMs;

    private final AgentInfoService agentInfoService;

//    private final ClusterManager clusterManager;


    public AgentServiceImpl(AgentInfoService agentInfoService) {
        this.agentInfoService = Objects.requireNonNull(agentInfoService, "agentInfoService");
//        this.clusterManager = Objects.requireNonNull(clusterManager, "clusterManager");
    }

    @Value("${web.activethread.activeAgent.duration.days:7}")
    private void setTimeDiffMs(int durationDays) {
        this.timeDiffMs = TimeUnit.MILLISECONDS.convert(durationDays, TimeUnit.DAYS);
    }

    @Override
    public AgentInfo getAgentInfo(String applicationName, String agentId) {
        long currentTime = System.currentTimeMillis();

        Set<AgentInfo> agentInfos = agentInfoService.getAgentsByApplicationName(applicationName, currentTime);
        for (AgentInfo agentInfo : agentInfos) {
            if (agentInfo == null) {
                continue;
            }
            if (!agentInfo.getApplicationName().equals(applicationName)) {
                continue;
            }
            if (!agentInfo.getAgentId().equals(agentId)) {
                continue;
            }

            return agentInfo;
        }

        return null;
    }

    @Override
    public AgentInfo getAgentInfo(String applicationName, String agentId, long startTimeStamp) {
        return getAgentInfo(applicationName, agentId, startTimeStamp, false);
    }

    @Override
    public AgentInfo getAgentInfo(String applicationName, String agentId, long startTimeStamp, boolean checkDB) {
        if (checkDB) {
            long currentTime = System.currentTimeMillis();

            Set<AgentInfo> agentInfos = agentInfoService.getAgentsByApplicationName(applicationName, currentTime);
            for (AgentInfo agentInfo : agentInfos) {
                if (agentInfo == null) {
                    continue;
                }
                if (!agentInfo.getApplicationName().equals(applicationName)) {
                    continue;
                }
                if (!agentInfo.getAgentId().equals(agentId)) {
                    continue;
                }
                if (agentInfo.getStartTimestamp() != startTimeStamp) {
                    continue;
                }

                return agentInfo;
            }
            return null;
        } else {
            AgentInfo agentInfo = new AgentInfo();
            agentInfo.setApplicationName(applicationName);
            agentInfo.setAgentId(agentId);
            agentInfo.setStartTimestamp(startTimeStamp);
            return agentInfo;
        }
    }

    @Override
    public List<AgentInfo> getRecentAgentInfoList(String applicationName) {
        return this.getRecentAgentInfoList(applicationName, this.timeDiffMs);
    }

    @Override
    public List<AgentInfo> getRecentAgentInfoList(String applicationName, long timeDiff) {
        List<AgentInfo> agentInfoList = new ArrayList<>();

        long currentTime = System.currentTimeMillis();

        Set<AgentInfo> agentInfos = agentInfoService.getRecentAgentsByApplicationName(applicationName, currentTime, timeDiff);
        for (AgentInfo agentInfo : agentInfos) {
            ListUtils.addIfValueNotNull(agentInfoList, agentInfo);
        }
        return agentInfoList;
    }

}
