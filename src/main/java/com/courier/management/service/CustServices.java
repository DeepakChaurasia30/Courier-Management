package com.courier.management.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courier.management.dto.CustomerDTO;
import com.courier.management.entity.Client;
import com.courier.management.entity.Customer;
import com.courier.management.entity.State;
import com.courier.management.projection.CustProjection;
import com.courier.management.repository.ClientRepository;
import com.courier.management.repository.CustomerRepository;
import com.courier.management.repository.StateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustServices {

    private final CustomerRepository customerRepository;
    private final ClientRepository clientRepository;
    private final StateRepository stateRepository;

    // Fetch all customers for a client
    public List<CustProjection> getAllCust(Integer clientId) {
        return customerRepository.findByClientId(clientId);
    }

    // Add new customer using DTO
    public ResponseEntity<Customer> addCustomer(CustomerDTO dto) {
        try {
            // Fetch client
            Client client = clientRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            // Fetch state
            State state = stateRepository.findById(dto.getCustStateCode())
                    .orElseThrow(() -> new RuntimeException("State not found"));

            if (!dto.getIsGst() || dto.getCustGst().isBlank()) {
                dto.setCustGst(null);
            }

            // Map DTO -> Entity
            Customer customer = new Customer();
            customer.setCustCode(dto.getCustCode());
            customer.setCustName(dto.getCustName());
            customer.setContPerson(dto.getContPerson());
            customer.setContNo(dto.getContNo());
            customer.setCustMail(dto.getCustMail());
            customer.setCustGst(dto.getCustGst());
            customer.setCustAdd(dto.getCustAdd());
            customer.setCustPin(dto.getCustPin());
            customer.setIsGst(dto.getIsGst() != null ? dto.getIsGst() : false);
            customer.setFuelRate(dto.getFuelRate() != null ? dto.getFuelRate() : BigDecimal.ZERO);
            customer.setDiscountRate(dto.getDiscountRate() != null ? dto.getDiscountRate() : BigDecimal.ZERO);
            customer.setClient(client);
            customer.setState(state);
            customer.setCustCreate(LocalDateTime.now());

            Customer saved = customerRepository.save(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Update customer using DTO
    public ResponseEntity<Customer> updateCustomer(Long id, CustomerDTO dto) {
        Optional<Customer> existingOpt = customerRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            Customer existing = existingOpt.get();

            // Fetch client & state
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            State state = stateRepository.findById(dto.getCustStateCode())
                    .orElseThrow(() -> new RuntimeException("State not found"));

            if (!dto.getIsGst() || dto.getCustGst().isBlank()) {
                dto.setCustGst(null);
            }

            // Map DTO -> existing entity
            existing.setCustCode(dto.getCustCode());
            existing.setCustName(dto.getCustName());
            existing.setContPerson(dto.getContPerson());
            existing.setContNo(dto.getContNo());
            existing.setCustMail(dto.getCustMail());
            existing.setCustGst(dto.getCustGst());
            existing.setCustAdd(dto.getCustAdd());
            existing.setCustPin(dto.getCustPin());
            existing.setIsGst(dto.getIsGst() != null ? dto.getIsGst() : false);
            existing.setFuelRate(dto.getFuelRate() != null ? dto.getFuelRate() : BigDecimal.ZERO);
            existing.setDiscountRate(dto.getDiscountRate() != null ? dto.getDiscountRate() : BigDecimal.ZERO);
            existing.setClient(client);
            existing.setState(state);

            Customer saved = customerRepository.save(existing);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Delete customer
    public ResponseEntity<String> deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    // Get customer by ID
    @Transactional(readOnly = true)
    public ResponseEntity<Customer> getCustomerById(Long id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        return customerOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}