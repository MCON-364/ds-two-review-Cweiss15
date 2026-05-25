package edu.touro.mcon364.finalreview.orderflowhandoff.homework;

import edu.touro.mcon364.finalreview.model.SensorReading;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Homework 2 — Sensor reading processor.
 *
 * A monitoring system receives readings from sensors over time. One part of the
 * program submits readings as they arrive. Another part of the program processes
 * those readings using one or more background workers.
 *
 * This class is responsible for coordinating that handoff and for keeping a
 * summary of the readings that were actually processed.
 *
 * The important question is not only "How do we calculate the stats?" It is also:
 * "What happens when readings are being submitted and processed by different
 * threads at the same time?"
 *
 * Requirements:
 * - submit(reading) accepts one new sensor reading for later processing.
 * - start(workerCount) starts workerCount background workers.
 * - workerCount must be greater than 0.
 * - Workers should process submitted readings until the processor is stopped and
 *   all already-submitted readings have been handled.
 * - stop() tells the processor to stop accepting/processing future work and waits
 *   until the workers finish the remaining work.
 * - getTotalProcessed() returns how many readings have been processed so far.
 * - getStats() returns summary statistics for the processed reading values:
 *   count, minimum, maximum, sum, and average.
 * - Public reporting methods must not expose mutable internal state.
 *
 * Before coding, think about:
 * - Which object or objects represent work waiting to be processed?
 * - Which object or objects represent work that has already been processed?
 * - Which state can be accessed by more than one thread?
 * - How will workers know when to keep working and when to stop?
 * - What should happen if getStats() is called while workers are still running?
 * - Is it better to store all processed readings and calculate stats later, or
 *   update numeric summary state as each reading is processed?
 * - If several workers update the same stats, how will those updates stay correct?
 */
public class SensorProcessor {
    private ExecutorService pool;
    private final BlockingQueue<SensorReading> sensorReadings = new LinkedBlockingQueue<>();
    private volatile boolean running = true;
    private AtomicInteger processed = new AtomicInteger(0);
    private final DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
    private final Object statsLock = new Object();
    /**
     * Accept one sensor reading for processing.
     *
     * @param reading the reading to process later
     */
    public void submit(SensorReading reading) {
        sensorReadings.add(reading);
    }

    /**
     * Start background workers that process submitted readings.
     *
     * @param workerCount number of worker threads to start
     * @throws IllegalArgumentException if workerCount is not positive
     */
    public void start(int workerCount) {
        if (workerCount > 0) {
            pool = Executors.newFixedThreadPool(workerCount);
            for (int i = 0; i < workerCount; i++) {
                pool.submit(this::workerLoop);
            }
        }
        else {
            throw new IllegalArgumentException("workerCount must be greater than 0");
        }
        // TODO: validate workerCount
        // TODO: start the requested number of workers
    }

    /**
     * Logic run by each worker.
     *
     * This method is private because callers should not run worker logic directly.
     * The worker should repeatedly look for work, process it when available, and
     * eventually exit when the processor is stopping and no work remains.
     */
    private void workerLoop() {
        try {
            while (!sensorReadings.isEmpty() || running) {
                SensorReading sensorReading = sensorReadings.poll();
                if (sensorReading != null) {
                    processed.incrementAndGet();
                    synchronized (statsLock) {
                        stats.accept(sensorReading.value());
                    }
                }
            }
        } catch (Exception ex) {
        }

    }

    /**
     * Stop the processor and wait for workers to finish.
     *
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public void stop() throws InterruptedException {
        running = false;
        if(pool != null) {
            pool.shutdown();
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Return the number of readings processed so far.
     */
    public int getTotalProcessed() {
        return processed.get();
    }

    /**
     * Return summary statistics for the processed reading values.
     *
     * If no readings have been processed yet, return an empty
     * DoubleSummaryStatistics object.
     */
    public DoubleSummaryStatistics getStats() {
        synchronized (statsLock) {
            return new DoubleSummaryStatistics(
                    stats.getCount(), stats.getMin(), stats.getMax(), stats.getSum()
            );
        }
        // TODO: calculate or return the current statistics safely
    }
}
