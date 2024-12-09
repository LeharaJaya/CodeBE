package com.example.ticketingsystem;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.Customer;
import service.TicketPool;
import service.Vendor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
@RestController
@RequestMapping("/api/tickets")
public class TicketingSystemApplication {

    private final TicketPool ticketPool = new TicketPool(1000); // Max capacity is 10 tickets

    public static void main(String[] args) {
        SpringApplication.run(TicketingSystemApplication.class, args);
    }

    // Vendor adds tickets
    @PostMapping("/vendor")
    public ResponseEntity<String> addTickets(@RequestParam int count) {
        new Thread(() -> ticketPool.addTickets(count)).start();
        return ResponseEntity.ok("Vendor is adding tickets...");
    }

    // Customer purchases tickets
    @PostMapping("/customer")
    public ResponseEntity<String> purchaseTicket() {
        new Thread(() -> ticketPool.buyTicket()).start();
        return ResponseEntity.ok("Customer is trying to purchase a ticket...");
    }

    // View current tickets
    @GetMapping
    public ResponseEntity<List<String>> viewTickets() {
        return ResponseEntity.ok(ticketPool.getTickets());
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

