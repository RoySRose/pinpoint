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

package com.navercorp.pinpoint.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Roy Kim
 */

public class DateUtil {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private DateUtil() {
    }

    public static long stringToLong(String date) {
        long convertedDate = 0;
        try {
            convertedDate = SDF.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }
}
