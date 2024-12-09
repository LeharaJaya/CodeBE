package service;


import model.Ticket;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int customerRetrievalRate;
    private final int quantity;

    public Customer(TicketPool ticketPool, int customerRetrievalRate, int quantity) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.quantity = quantity;
    }

    @Override
    public void run() {
        for (int i = 0; i < quantity; i++) {
            Ticket ticket = ticketPool.buyTicket();
            System.out.println(Thread.currentThread().getName() + " purchased " + ticket);
            try {
                Thread.sleep(customerRetrievalRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Customer thread interrupted.", e);
            }
        }
    }

    public static void startCustomers(TicketPool ticketPool, int customerCount, int customerRetrievalRate, int quantity) {
        ExecutorService executor = Executors.newFixedThreadPool(customerCount);
        for (int i = 0; i < customerCount; i++) {
            executor.submit(new Customer(ticketPool, customerRetrievalRate, quantity));
        }
        executor.shutdown();
    }
}
