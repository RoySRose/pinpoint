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

package com.navercorp.pinpoint.api;

import com.navercorp.pinpoint.common.server.util.ServerBootLogger;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Roy Kim
 */
@SpringBootConfiguration
@EnableAutoConfiguration
//@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, TransactionAutoConfiguration.class, BatchAutoConfiguration.class})
//@ImportResource({ "classpath:applicationContext-api.xml","servlet-context-api.xml"})
@ImportResource({"classpath:applicationContext-api.xml"})
public class ApiApp {

    private static final ServerBootLogger logger = ServerBootLogger.getLogger(ApiApp.class);

    public static void main(String[] args) {
        try {
            ApiStarter starter = new ApiStarter(ApiApp.class, WebMvcConfig.class);
            starter.start(args);
        } catch (Exception exception) {
            logger.error("[ApiApp] could not launch app.", exception);
        }
    }
}
