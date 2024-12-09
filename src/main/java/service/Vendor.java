package service;

import model.Ticket;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int totalTickets;
    private final int ticketReleaseRate;

    public Vendor(TicketPool ticketPool, int totalTickets, int ticketReleaseRate) {
        this.ticketPool = ticketPool;
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
    }

    @Override
    public void run() {
        for (int i = 1; i <= totalTickets; i++) {
            Ticket ticket = new Ticket(i, "Event " + i, new BigDecimal("1000.00"));
            ticketPool.addTickets(1);
            try {
                Thread.sleep(ticketReleaseRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Vendor thread interrupted.", e);
            }
        }
    }

    public static void startVendors(TicketPool ticketPool, int vendorCount, int totalTickets, int ticketReleaseRate) {
        ExecutorService executor = Executors.newFixedThreadPool(vendorCount);
        for (int i = 0; i < vendorCount; i++) {
            executor.submit(new Vendor(ticketPool, totalTickets / vendorCount, ticketReleaseRate));
        }
        executor.shutdown();
    }
}
