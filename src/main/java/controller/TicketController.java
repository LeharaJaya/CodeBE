package controller;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import service.Customer;
import service.TicketPool;
import service.Vendor;

@RestController
public class TicketController {
    private final TicketPool ticketPool;

    public TicketController(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    @GetMapping("/start-system")
    public String startSystem() {
        int totalTickets = 100;
        int ticketReleaseRate = 1000; // 1 second
        int customerRetrievalRate = 2000; // 2 seconds
        int maxCapacity = 50;


// Initialize the TicketPool
        ticketPool.setMaxTicketCapacity(maxCapacity);

        // Start Vendors
        Vendor.startVendors(ticketPool, 5, totalTickets, ticketReleaseRate);

        // Start Customers
        Customer.startCustomers(ticketPool, 5, customerRetrievalRate, 10);

        return "Ticketing system started!";
    }

    @GetMapping("/status")
    public String getStatus() {
        return "Current Ticket Pool Size: " + ticketPool.getSize();
    }
}
