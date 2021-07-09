package com.navercorp.pinpoint.collector.sender;

import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.common.server.bo.SpanChunkBo;

import org.springframework.stereotype.Service;

@Service
public class EmptySender implements Sender {

    @Override
    public void send(SpanBo spanBo) {
        return;
    }

    @Override
    public void send(SpanChunkBo spanChunkBo) {
        return;
    }
}
