package service;

public class ProducerConsumerDemo {

    public static void main(String[] args) {
        TransactionBuffer buffer = new TransactionBuffer(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    final int id = i;

                    buffer.produce(() -> System.out.println(
                            "Processing transaction " + id
                    ));

                    System.out.println("Produced transaction " + id);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    Runnable task = buffer.consume();
                    task.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("All transactions processed.");
    }
}