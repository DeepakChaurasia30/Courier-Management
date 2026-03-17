package com.courier.management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

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
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<Customer> addCustomer(
            @RequestPart("data") CustomerDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return customerService.addCustomer(dto, image);
    }

    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @RequestPart("data") CustomerDTO dto,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return customerService.updateCustomer(id, dto, image);
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