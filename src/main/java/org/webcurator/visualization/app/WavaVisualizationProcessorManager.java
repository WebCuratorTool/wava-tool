package org.webcurator.visualization.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.visualization.VisualizationAbstractProcessor;
import org.webcurator.core.visualization.VisualizationDirectoryManager;
import org.webcurator.core.visualization.VisualizationProcessorManager;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WavaVisualizationProcessorManager extends VisualizationProcessorManager {
    protected static final Logger log = LoggerFactory.getLogger(WavaVisualizationProcessorManager.class);
    private final Map<String, WavaVisualizationProcessorManager.ProcessorHandler> queued_processors = new Hashtable<>();
    private final ExecutorService thread_pool;

    public WavaVisualizationProcessorManager(VisualizationDirectoryManager visualizationDirectoryManager,
                                             int maxConcurrencyModThreads) {
        super(visualizationDirectoryManager, null, maxConcurrencyModThreads);
        this.thread_pool = Executors.newFixedThreadPool(maxConcurrencyModThreads);
    }


    public void startTask(VisualizationAbstractProcessor processor) throws IOException {
        if (queued_processors.containsKey(processor.getKey())) {
            log.debug("Processor is in the queue: {}", processor.getKey());
            return;
        }

        WavaVisualizationProcessorManager.ProcessorHandler handler = new WavaVisualizationProcessorManager.ProcessorHandler(processor);
        //Cache the current running
        queued_processors.put(processor.getKey(), handler);
        handler.future = thread_pool.submit(processor);
    }

    public VisualizationAbstractProcessor getProcessor(String key) {
        WavaVisualizationProcessorManager.ProcessorHandler handler = queued_processors.get(key);
        if (handler == null) {
            return null;
        } else {
            return handler.processor;
        }
    }

    public void finalise(VisualizationAbstractProcessor processor) {
        //Remove existing process firstly to avoid deadlock
        queued_processors.remove(processor.getKey());
    }


    static class ProcessorHandler implements Callable<Boolean> {
        public long startTime = System.currentTimeMillis();
        public VisualizationAbstractProcessor processor;
        public Future<Boolean> future;

        public ProcessorHandler(VisualizationAbstractProcessor processor) {
            this.processor = processor;
        }

        public long getRunningDuration() {
            return System.currentTimeMillis() - startTime;
        }

        @Override
        public Boolean call() throws Exception {
            return this.processor.process();
        }
    }
}
