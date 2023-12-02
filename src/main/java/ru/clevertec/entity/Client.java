package ru.clevertec.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Data;

@Data
public class Client {
    private final List<Integer> intList;
    private final ScheduledExecutorService executor;
    private final List<Future<Response>> listFuture;
    private final Lock lock;
    private Integer accumulator;

    public Client(int initCapacity, int threadPoolSize) {
        this.intList = new ArrayList<>();
        for (int i = 0; i < initCapacity; i++) {
            intList.add(i + 1);
        }
        this.executor = Executors.newScheduledThreadPool(threadPoolSize);
        this.listFuture = new ArrayList<>();
        this.lock = new ReentrantLock();

    }

    public void sendRequestsBatch(Server server) {
        int iterations = intList.size();
        for (int i = 0; i < iterations; i++) {
            sendRequest(server);
        }
        executor.shutdown();
        accumulator = listFuture.parallelStream()
                .mapToInt(f -> {
                    try {
                        return f.get().getNumber();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sum();
    }

    private void sendRequest(Server server) {
        long delay = (long) (Math.random() * 400) + 100;
        ScheduledFuture<Response> future = executor.schedule(() -> {
            Integer removed;
            try {
                lock.lock();
                int index = (int) (Math.random() * intList.size());
                removed = intList.remove(index);
            } finally {
                lock.unlock();
            }
            Request request = new Request();
            request.setNumber(removed);
            return server.processRequest(request);
        }, delay, TimeUnit.MILLISECONDS);
        listFuture.add(future);
    }
}
