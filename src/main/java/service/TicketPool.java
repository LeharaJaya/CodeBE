package service;



import model.Ticket;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {

    private final List<String> tickets = new LinkedList<>();
    private  int maxTicketCapacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public TicketPool(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }
    public void setMaxTicketCapacity(int maxCapacity) {
        this.maxTicketCapacity = maxCapacity;
    }
    public int getSize() {
        return tickets.size(); // Assuming `tickets` is a collection like List or Queue
    }

    // Add tickets to the pool (Producer logic)
    public void addTickets(int count) {
        lock.lock();
        try {
            while (tickets.size() + count > maxTicketCapacity) {
                System.out.println("Ticket pool full. Vendor waiting...");
                notFull.await();
            }

            for (int i = 1; i <= count; i++) {
                String ticket = "Ticket-" + (tickets.size() + 1);
                tickets.add(ticket);
                System.out.println("Added: " + ticket);
            }

            notEmpty.signalAll(); // Notify consumers
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Vendor interrupted!");
        } finally {
            lock.unlock();
        }
    }

    // buy a ticket from the pool (Consumer logic)
    public Ticket buyTicket() {
        lock.lock();
        try {
            while (tickets.isEmpty()) {
                System.out.println("No tickets available. Customer waiting...");
                notEmpty.await();
            }

            String ticketId = tickets.remove(0); // Remove the first ticket
            System.out.println("Purchased: " + ticketId);

            notFull.signalAll(); // Notify producers

            // Create and return a Ticket object based on the ticket ID
            return new Ticket(Integer.parseInt(ticketId.split("-")[1]), "Event", new BigDecimal("1000.00"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Customer interrupted!");
            return null; // Return null in case of interruption
        } finally {
            lock.unlock();
        }
    }


    // Get the current tickets (for viewing)
    public List<String> getTickets() {
        lock.lock();
        try {
            return new LinkedList<>(tickets); // Return a copy to avoid modification
        } finally {
            lock.unlock();
        }
    }
}
