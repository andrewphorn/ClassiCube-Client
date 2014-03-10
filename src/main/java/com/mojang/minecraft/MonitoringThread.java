package com.mojang.minecraft;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MonitoringThread extends Thread {

    static class ThreadTime {

        private long id;
        private long last;
        private long current;

        public ThreadTime(long id) {
            this.id = id;
        }

        public long getCurrent() {
            return current;
        }

        public long getId() {
            return id;
        }

        public long getLast() {
            return last;
        }

        public void setCurrent(long current) {
            this.current = current;
        }

        public void setLast(long last) {
            this.last = last;
        }
    }

    private long refreshInterval;

    private boolean stopped;
    private Map<Long, ThreadTime> threadTimeMap = new HashMap<>();
    private ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    private OperatingSystemMXBean opBean = ManagementFactory.getOperatingSystemMXBean();
    private Runtime runtime;

    public long maxMemory, totalMemory, freeMemory;

    public MonitoringThread(long refreshInterval) {
        this.refreshInterval = refreshInterval;

        setName("MonitoringThread");

        runtime = Runtime.getRuntime();
        start();
    }

    public double getAvarageUsagePerCPU() {
        return getTotalUsage() / opBean.getAvailableProcessors();
    }

    public double getTotalUsage() {
        Collection<ThreadTime> values;
        synchronized (threadTimeMap) {
            values = new HashSet<>(threadTimeMap.values());
        }

        double usage = 0D;
        for (ThreadTime threadTime : values) {
            synchronized (threadTime) {
                usage += (threadTime.getCurrent() - threadTime.getLast())
                        / (refreshInterval * 10000);
            }
        }
        return usage;
    }

    public double getUsageByThread(Thread t) {
        ThreadTime info;
        synchronized (threadTimeMap) {
            info = threadTimeMap.get(t.getId());
        }

        double usage = 0D;
        if (info != null) {
            synchronized (info) {
                usage = (info.getCurrent() - info.getLast()) / (refreshInterval * 10000);
            }
        }
        return usage;
    }

    private void mapNewThreads(long[] allThreadIds) {
        for (long id : allThreadIds) {
            synchronized (threadTimeMap) {
                if (!threadTimeMap.containsKey(id)) {
                    threadTimeMap.put(id, new ThreadTime(id));
                }
            }
        }
    }

    private void removeDeadThreads(Set<Long> mappedIds, long[] allThreadIds) {
        outer: for (long id1 : mappedIds) {
            for (long id2 : allThreadIds) {
                if (id1 == id2) {
                    continue outer;
                }
            }
            synchronized (threadTimeMap) {
                threadTimeMap.remove(id1);
            }
        }
    }

    @Override
    public void run() {
        while (!stopped) {
            Set<Long> mappedIds;
            synchronized (threadTimeMap) {
                mappedIds = new HashSet<>(threadTimeMap.keySet());
            }

            long[] allThreadIds = threadBean.getAllThreadIds();

            if (mappedIds != null) {
                removeDeadThreads(mappedIds, allThreadIds);
            }

            if (allThreadIds != null) {
                mapNewThreads(allThreadIds);
            }

            Collection<ThreadTime> values;
            synchronized (threadTimeMap) {
                values = new HashSet<>(threadTimeMap.values());
            }

            for (ThreadTime threadTime : values) {
                synchronized (threadTime) {
                    threadTime.setCurrent(threadBean.getThreadCpuTime(threadTime.getId()));
                }
            }
            maxMemory = runtime.maxMemory();
            totalMemory = runtime.totalMemory();
            freeMemory = runtime.freeMemory();

            try {
                Thread.sleep(refreshInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (ThreadTime threadTime : values) {
                synchronized (threadTime) {
                    threadTime.setLast(threadTime.getCurrent());
                }
            }
        }
    }

    public void stopMonitor() {
        stopped = true;
    }
}