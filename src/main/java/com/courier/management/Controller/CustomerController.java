package com.courier.management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courier.management.dto.CustomerDTO;
import com.courier.management.entity.Customer;
import com.courier.management.projection.CustProjection;
import com.courier.management.service.CustServices;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/cust")
@RequiredArgsConstructor
public class CustomerController {

    private final CustServices customerService;

    // Fetch all customers for a client
    @GetMapping("/getcust")
    public List<CustProjection> getCust(@RequestParam Integer id) {
        return customerService.getAllCust(id);
    }

    // Add new customer using DTO
    @PostMapping("/add")
    public ResponseEntity<Customer> addCustomer(@RequestBody CustomerDTO dto) {
        return customerService.addCustomer(dto);
    }

    // Update existing customer using DTO
    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id,
                                                   @RequestBody CustomerDTO dto) {
        return customerService.updateCustomer(id, dto);
    }

    // Delete customer
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        return customerService.deleteCustomer(id);
    }

    // Get customer by ID
    @GetMapping("/getent/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }
}