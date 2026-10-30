package service;

public class TransactionBuffer {

    private final Runnable[] buffer;
    private int count = 0;

    public TransactionBuffer(int capacity) {
        buffer = new Runnable[capacity];
    }

    public synchronized void produce(Runnable task)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[count++] = task;

        notify();
    }

    public synchronized Runnable consume()
            throws InterruptedException {

        while (count == 0) {
            wait();
        }

        Runnable task = buffer[0];

        for (int i = 1; i < count; i++) {
            buffer[i - 1] = buffer[i];
        }

        buffer[--count] = null;

        notify();

        return task;
    }
}