package ru.clevertec.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Data;

@Data
public class Server {
    private final List<Integer> processed;
    private final Lock lock = new ReentrantLock();

    public Server() {
        this.processed = new ArrayList<>();
    }

    public Response processRequest(Request request) {
        delay();
        Integer number = request.getNumber();
        Response response = new Response();
        try {
            lock.lock();
            processed.add(number);
            response.setNumber(processed.size());
        } finally {
            lock.unlock();
        }
        return response;
    }

    private void delay() {
        long time = (long) (Math.random() * 900) + 100;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
