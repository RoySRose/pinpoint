package com.navercorp.pinpoint.collector.sender;

import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.common.server.bo.SpanChunkBo;

public interface Sender {

    void send(SpanBo spanBo);

    void send(SpanChunkBo spanChunkBo);
}
