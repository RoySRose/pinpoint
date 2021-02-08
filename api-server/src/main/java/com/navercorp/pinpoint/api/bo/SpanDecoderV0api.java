/*
 * Copyright 2019 NAVER Corp.
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

package com.navercorp.pinpoint.api.bo;

import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.server.bo.AnnotationBo;
import com.navercorp.pinpoint.common.server.bo.AnnotationTranscoder;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.common.server.bo.SpanEventBo;
import com.navercorp.pinpoint.common.server.bo.filter.SequenceSpanEventFilter;
import com.navercorp.pinpoint.common.server.bo.filter.SpanEventFilter;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.SpanDecoderV0;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.SpanDecodingContext;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.bitfield.SpanBitFiled;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.bitfield.SpanEventBitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roy Kim
 */
@Component
public class SpanDecoderV0api extends SpanDecoderV0 {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final SequenceSpanEventFilter SEQUENCE_SPAN_EVENT_FILTER = new SequenceSpanEventFilter(SequenceSpanEventFilter.MAX_SEQUENCE);

    private static final AnnotationTranscoder transcoder = new AnnotationTranscoder();


    @Override
    public void readSpanValue(Buffer buffer, SpanBo span, SpanDecodingContext decodingContext) {

        final byte version = buffer.readByte();

        span.setVersion(version);

        final SpanBitFiled bitFiled = new SpanBitFiled(buffer.readByte());

        final short serviceType = buffer.readShort();
        span.setServiceType(serviceType);

        switch (bitFiled.getApplicationServiceTypeEncodingStrategy()) {
            case PREV_EQUALS:
                span.setApplicationServiceType(serviceType);
                break;
            case RAW:
                span.setApplicationServiceType(buffer.readShort());
                break;
            default:
                throw new IllegalStateException("applicationServiceType");
        }

        if (!bitFiled.isRoot()) {
            span.setParentSpanId(buffer.readLong());
        } else {
            span.setParentSpanId(-1);
        }

        final long startTimeDelta = buffer.readVLong();
        final long startTime = span.getCollectorAcceptTime() - startTimeDelta;
        span.setStartTime(startTime);
        span.setElapsed(buffer.readVInt());

        span.setRpc(buffer.readPrefixedString());

        span.setEndPoint(buffer.readPrefixedString());
        span.setRemoteAddr(buffer.readPrefixedString());
        span.setApiId(buffer.readSVInt());

        if (bitFiled.isSetErrorCode()) {
            span.setErrCode(buffer.readInt());
        }
        if (bitFiled.isSetHasException()) {
            int exceptionId = buffer.readSVInt();
            String exceptionMessage = buffer.readPrefixedString();
            span.setExceptionInfo(exceptionId, exceptionMessage);
        }

        if (bitFiled.isSetFlag()) {
            span.setFlag(buffer.readShort());
        }

        if (bitFiled.isSetLoggingTransactionInfo()) {
            span.setLoggingTransactionInfo(buffer.readByte());
        }

        span.setAcceptorHost(buffer.readPrefixedString());


        if (bitFiled.isSetAnnotation()) {
            List<AnnotationBo> annotationBoList = readAnnotationList(buffer, decodingContext);
            span.setAnnotationBoList(annotationBoList);
        }

        List<SpanEventBo> spanEventBoList = readSpanEvent(buffer, decodingContext, SEQUENCE_SPAN_EVENT_FILTER);
        span.addSpanEventBoList(spanEventBoList);
    }

    private List<SpanEventBo> readSpanEvent(Buffer buffer, SpanDecodingContext decodingContext, SpanEventFilter spanEventFilter) {
        final int spanEventSize = buffer.readVInt();
        if (spanEventSize <= 0) {
            return new ArrayList<>();
        }
        final List<SpanEventBo> spanEventBoList = new ArrayList<>();
        SpanEventBo prev = null;
        for (int i = 0; i < spanEventSize; i++) {
            SpanEventBo spanEvent;
            if (i == 0) {
                spanEvent = readFirstSpanEvent(buffer, decodingContext);
            } else {
                spanEvent = readNextSpanEvent(buffer, prev, decodingContext);
            }
            prev = spanEvent;
//            boolean accept = spanEventFilter.filter(spanEvent);
//            if (accept) {
//                spanEventBoList.add(spanEvent);
//            }
        }

        return spanEventBoList;
    }

    private SpanEventBo readNextSpanEvent(final Buffer buffer, final SpanEventBo prev, SpanDecodingContext decodingContext) {
        final SpanEventBo spanEventBo = new SpanEventBo();

        final SpanEventBitField bitField = new SpanEventBitField(buffer.readShort());

        switch (bitField.getStartElapsedEncodingStrategy()) {
            case PREV_DELTA:
                buffer.readVInt();
                break;
            case PREV_EQUALS:
                break;
            default:
                throw new IllegalStateException("unsupported SequenceEncodingStrategy");
        }
        buffer.readVInt();

        switch (bitField.getSequenceEncodingStrategy()) {
            case PREV_DELTA:
                buffer.readVInt();
                break;
            case PREV_ADD1:
                break;
            default:
                throw new IllegalStateException("unsupported SequenceEncodingStrategy");
        }

        switch (bitField.getDepthEncodingStrategy()) {
            case RAW:
                buffer.readSVInt();
                break;
            case PREV_EQUALS:
                break;
            default:
                throw new IllegalStateException("unsupported DepthEncodingStrategy");
        }

        switch (bitField.getServiceTypeEncodingStrategy()) {
            case RAW:
                buffer.readShort();
                break;
            case PREV_EQUALS:
                break;
            default:
                throw new IllegalStateException("unsupported ServiceTypeEncodingStrategy");
        }


        buffer.readSVInt();

        if (bitField.isSetRpc()) {
            buffer.readPrefixedString();
        }

        if (bitField.isSetEndPoint()) {
            buffer.readPrefixedString();
        }
        if (bitField.isSetDestinationId()) {
            buffer.readPrefixedString();
        }

        if (bitField.isSetNextSpanId()) {
            buffer.readLong();
        }


        if (bitField.isSetHasException()) {
            buffer.readSVInt();
            buffer.readPrefixedString();
        }

        if (bitField.isSetAnnotation()) {
            readAnnotationList(buffer, decodingContext);
        }

        if (bitField.isSetNextAsyncId()) {
            buffer.readSVInt();
        }

        if (bitField.isSetAsyncId()) {
            buffer.readInt();
            buffer.readVInt();
        }

        return spanEventBo;
    }

    private SpanEventBo readFirstSpanEvent(Buffer buffer, SpanDecodingContext decodingContext) {
        final SpanEventBitField bitField = new SpanEventBitField(buffer.readByte());

        final SpanEventBo firstSpanEvent = new SpanEventBo();
        buffer.readVInt();
        buffer.readVInt();

        buffer.readShort();
        buffer.readSVInt();
        buffer.readShort();

        if (bitField.isSetRpc()) {
            buffer.readPrefixedString();
        }

        if (bitField.isSetEndPoint()) {
            buffer.readPrefixedString();
        }
        if (bitField.isSetDestinationId()) {
            buffer.readPrefixedString();
        }

        buffer.readSVInt();

        if (bitField.isSetNextSpanId()) {
            buffer.readLong();
        }

        if (bitField.isSetHasException()) {
            buffer.readSVInt();
            buffer.readPrefixedString();
        }

        if (bitField.isSetAnnotation()) {
            List<AnnotationBo> annotationBoList = readAnnotationList(buffer, decodingContext);
            firstSpanEvent.setAnnotationBoList(annotationBoList);
        }

        if (bitField.isSetNextAsyncId()) {
            buffer.readSVInt();
        }

//        if (bitField.isSetAsyncId()) {
//            firstSpanEvent.setAsyncId(buffer.readInt());
//            firstSpanEvent.setAsyncSequence((short) buffer.readVInt());
//        }
        return firstSpanEvent;
    }

    private List<AnnotationBo> readAnnotationList(Buffer buffer, SpanDecodingContext decodingContext) {
        int annotationListSize = buffer.readVInt();
        List<AnnotationBo> annotationBoList = new ArrayList<AnnotationBo>(annotationListSize);

//        AnnotationBo prev = decodingContext.getPrevFirstAnnotationBo();
        AnnotationBo prev = null;
        for (int i = 0; i < annotationListSize; i++) {
            AnnotationBo current;
            if (i == 0) {
                current = readFirstAnnotationBo(buffer);
                // save first annotation for delta bitfield
//                decodingContext.setPrevFirstAnnotationBo(current);
            } else {
                current = readDeltaAnnotationBo(buffer, prev);
            }

            prev = current;
            annotationBoList.add(current);
        }
        return annotationBoList;
    }

    private AnnotationBo readFirstAnnotationBo(Buffer buffer) {
        final int key = buffer.readSVInt();
        byte valueType = buffer.readByte();
        byte[] valueBytes = buffer.readPrefixedBytes();
        Object value = transcoder.decode(valueType, valueBytes);

        AnnotationBo current = new AnnotationBo(key, value);
        return current;
    }

    private AnnotationBo readDeltaAnnotationBo(Buffer buffer, AnnotationBo prev) {
        final int prevKey = prev.getKey();
        int key = buffer.readSVInt() + prevKey;

        byte valueType = buffer.readByte();
        byte[] valueBytes = buffer.readPrefixedBytes();
        Object value = transcoder.decode(valueType, valueBytes);

        AnnotationBo annotation = new AnnotationBo(key, value);
        return annotation;
    }

    public void next(SpanDecodingContext decodingContext) {
        decodingContext.next();
    }
}
