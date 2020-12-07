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

package com.navercorp.pinpoint.api.mapper;

import com.navercorp.pinpoint.api.vo.TransactionIdInfo;
import com.navercorp.pinpoint.common.hbase.HbaseColumnFamily;
import com.navercorp.pinpoint.common.hbase.HbaseTableConstants;
import com.navercorp.pinpoint.common.hbase.RowMapper;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.util.BytesUtils;
import com.navercorp.pinpoint.common.util.TimeUtils;
import com.navercorp.pinpoint.web.mapper.TransactionIdMapper;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Roy Kim
 */
@Component
public class TransanctionIdListMapper implements RowMapper<List<TransactionIdInfo>> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<TransactionIdInfo> mapRow(Result result, int rowNum) {
        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        Cell[] rawCells = result.rawCells();
        List<TransactionIdInfo> transactionIdInfoList = new ArrayList<>(rawCells.length);
        for (Cell cell : rawCells) {

            TransactionIdInfo transactionIdInfo = createTransactionIdInfo(cell);
            transactionIdInfoList.add(transactionIdInfo);

            logger.debug("found transactionIdInfo {}", transactionIdInfo);
        }

        return transactionIdInfoList;
    }

    static TransactionIdInfo createTransactionIdInfo(Cell cell) {

//        final Buffer valueBuffer = new OffsetFixedBuffer(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
//        int elapsed = valueBuffer.readVInt();
//        int exceptionCode = valueBuffer.readSVInt();
//        String agentId = valueBuffer.readPrefixedString();

        final int acceptTimeOffset = cell.getRowOffset() + HbaseTableConstants.APPLICATION_NAME_MAX_LEN + HbaseColumnFamily.APPLICATION_TRACE_INDEX_TRACE.ROW_DISTRIBUTE_SIZE;
        long reverseAcceptedTime = BytesUtils.bytesToLong(cell.getRowArray(), acceptTimeOffset);
        long acceptedTime = TimeUtils.recoveryTimeMillis(reverseAcceptedTime);

        TransactionId transactionId = TransactionIdMapper.parseVarTransactionId(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
        return new TransactionIdInfo(transactionId, acceptedTime);
    }

}
