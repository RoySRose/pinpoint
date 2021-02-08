package com.navercorp.pinpoint.api.postprocess;

import com.navercorp.pinpoint.api.bo.AggregatedTransaction;
import com.navercorp.pinpoint.api.bo.SpanBoResult;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import org.apache.zookeeper.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TransactionPostProcessor implements PostProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<List<SpanBoResult>> spanList;
    private List<AggregatedTransaction> aggregatedTransactionList;

    //@TODO concurrent hashmap
    private HashMap<String, Long> label = new HashMap<>();

    //@TODO atomic integer
    private long counter = 0;

    public TransactionPostProcessor(List<List<SpanBoResult>> spanList) {
        this.spanList = spanList;
        this.aggregatedTransactionList = new ArrayList<>();
    }

    @Override
    public void postProcess() {
        logger.info("postprocessing start");
        long checkpoint = System.currentTimeMillis();

        logger.info("findroot start");
        findRoot();
        logger.info("findroot execution time:{}ms ", System.currentTimeMillis() - checkpoint);
        checkpoint = System.currentTimeMillis();


        logger.info("sort start");
        sortSpans();
        logger.info("sort execution time:{}ms ", System.currentTimeMillis() - checkpoint);
        checkpoint = System.currentTimeMillis();

//        if(logger.isDebugEnabled()){
            validateSorting();
//        }
        logger.info("validateSorting execution time:{}ms ", System.currentTimeMillis() - checkpoint);
        checkpoint = System.currentTimeMillis();


        logger.info("replaceRPC start");
        replaceRPC();
        logger.info("replaceRPC execution time:{}ms ", System.currentTimeMillis() - checkpoint);
        checkpoint = System.currentTimeMillis();


        logger.info("factorizeRPC start");
        factorizeRPC();
        logger.info("factorizeRPC execution time:{}ms ", System.currentTimeMillis() - checkpoint);
        checkpoint = System.currentTimeMillis();

        logger.info("aggregateTransaction start");
        aggregateTransaction();
        logger.info("aggregateTransaction execution time:{}ms ", System.currentTimeMillis() - checkpoint);

        logger.info("postprocessing finish");
    }



    private void validateSorting() {
        for(List<SpanBoResult> spanBoList : spanList) {
            if (spanBoList.get(0).isRoot() != true) {
                logger.info("first sorted span is not ROOT! {} ", spanBoList.get(0).getTransactionIdCreated());
                for (SpanBoResult spanBoResult : spanBoList) {
                    logger.info("spanBoResult {}", spanBoResult);
                }
            }
        }
    }

    private void aggregateTransaction() {
        for(List<SpanBoResult> spanBoList : spanList) {

            List<Long> labeledRpcList= new ArrayList<>();

            SpanBoResult rootSpan = spanBoList.get(0);

            //create rpc list
            for(SpanBoResult spanBoResult : spanBoList){
                labeledRpcList.add(spanBoResult.getLabeledRPC());
            }

            AggregatedTransaction  aggregatedTransaction = new AggregatedTransaction(
                    rootSpan.getTransactionIdCreated()
                    ,labeledRpcList
                    , rootSpan.getStartTime()
                    , rootSpan.getStartTime() + rootSpan.getElapsed()
                    , rootSpan.getElapsed());
            aggregatedTransactionList.add(aggregatedTransaction);
        }
    }

    private void factorizeRPC() {
        for(List<SpanBoResult> spanBoList : spanList) {
            for(SpanBoResult spanBoResult : spanBoList){
                String key = spanBoResult.getRpc();
                label.putIfAbsent(key, counter++);
                spanBoResult.setLabeledRPC(label.get(key));
            }
        }
    }

    private void replaceRPC() {
        final String pattern = "(?<=\\/)([0-9]+|(?:[0-9]{1,3}\\.){3}[0-9]{1,3})(?=(\\/|$))|((?<=\\/bad-members\\/).+?(?=\\/|$))|((?<=\\/event\\/).+?(?=\\/|$))|((?<=\\/contents\\/).+?(?=\\/|$))|((?<=\\/vodapi\\/statuses\\/v1\\/).+?(?=\\/|$))|((?<=\\/members\\/).+?(?=\\/|$))|((?<=\\/staffs\\/).+?(?=\\/|$))|((?<=\\/user-id\\/).+?(?=\\/|$))|((?<=\\/member\\/).+?(?=\\/|$))|((?<=\\/seller\\/).+?(?=\\/|$))|((?<=\\/playback-keys\\/v2\\/).+?(?=\\/|$))|((?<=\\/products\\/).+?(?=\\/|$))|((?<=\\/votes\\/).+?(?=\\/|$))|((?<=\\/users\\/).+?(?=\\/|$))";
        for(List<SpanBoResult> spanBoList : spanList) {
            for(SpanBoResult spanBoResult : spanBoList){
                spanBoResult.setRpc(spanBoResult.getRpc().replaceAll(pattern,""));
            }
        }
    }

    private void sortSpans() {
        for(List<SpanBoResult> spanBoList : spanList) {
            Collections.sort(spanBoList);
        }
    }

    private void findRoot() {
        final long start2 = System.currentTimeMillis();

        for (List<SpanBoResult> spanBoList : spanList) {
            boolean isRoot = false;
            for (SpanBoResult spanBoResult : spanBoList) {
                logger.debug("root? {}, {} ", spanBoResult.getSpanId(), spanBoResult.isRoot());
                if (spanBoResult.isRoot()) {
                    isRoot = true;
                    logger.debug("root is true at {}", spanBoResult.getSpanId());
                    break;
                }
            }
            if (!isRoot) {
                int elapsed = -1;
                SpanBoResult rootSpanBo = null;
                for (SpanBoResult spanBoResult : spanBoList) {
                    if (spanBoResult.getElapsed() > elapsed) {
                        logger.debug("root is true at {}", spanBoResult.getSpanId());
                        elapsed = spanBoResult.getElapsed();
                        rootSpanBo = spanBoResult;
                    } else if (spanBoResult.getElapsed() == elapsed) {
                        if (spanBoResult.getSpanId() == rootSpanBo.getParentSpanId()) {
                            rootSpanBo = spanBoResult;
                            logger.info("@@@ same elapsed switch parent spanid ={}, txid = {}", spanBoResult.getSpanId(), spanBoResult.getTransactionId().getAgentId() + "^" + spanBoResult.getTransactionId().getAgentStartTime() + "^" + spanBoResult.getTransactionId().getTransactionSequence());
                        }

                    }
                }

                rootSpanBo.setParentSpanId(-1L);
            }

        }

        final long time2 = System.currentTimeMillis() - start2;
        logger.info("postprocessing execution time:{}ms ", time2);
    }

    public List<AggregatedTransaction> getAggregatedTransactionList() {
        return aggregatedTransactionList;
    }

    @Override
    public String toString() {
        return "TransactionPostProcessor{" +
                "spanList=" + spanList +
                ", aggregatedTransactionList=" + aggregatedTransactionList +
                ", label=" + label +
                ", counter=" + counter +
                '}';
    }
}
