package com.courier.management.service;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final CustomerRepository customerRepository;
    private final ClientRepository clientRepository;
    private final StateRepository stateRepository;
    private final FileStorageService fileStorageService;

    // Fetch all customers for a client
    public List<CustProjection> getAllCust(Integer clientId) {
        return customerRepository.findByClientId(clientId);
    }

    // Add new customer using DTO
    public ResponseEntity<Customer> addCustomer(CustomerDTO dto, MultipartFile image) {
        try {

            Client client = clientRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            State state = stateRepository.findById(dto.getCustStateCode())
                    .orElseThrow(() -> new RuntimeException("State not found"));

            if (!dto.getIsGst() || dto.getCustGst().isBlank()) {
                dto.setCustGst(null);
            }

            Customer customer = new Customer();

            customer.setCustCode(dto.getCustCode().toUpperCase());
            customer.setCustName(dto.getCustName().toUpperCase());
            customer.setContPerson(dto.getContPerson().toUpperCase());
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

            // SAVE IMAGE
            if (image != null && !image.isEmpty()) {
                String path = fileStorageService.saveImage(image);
                customer.setImagePath(path);
            }

            Customer saved = customerRepository.save(customer);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
            // return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Update customer using DTO
    public ResponseEntity<Customer> updateCustomer(Long id, CustomerDTO dto, MultipartFile newImageFile) {

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

            // ----------------------------
            // Handle Image Update
            // ----------------------------
            if (newImageFile != null && !newImageFile.isEmpty()) {
                String oldImagePath = existing.getImagePath();
                if (oldImagePath != null && !oldImagePath.isBlank()) {
                    // Extract old filename from URL path
                    String oldFileName = oldImagePath.substring(oldImagePath.lastIndexOf("/") + 1);

                    // Path to the old file
                    Path oldFilePath = Paths.get(uploadDir, "customer", oldFileName);
                    File oldFile = oldFilePath.toFile();

                    if (oldFile.exists()) {
                        // Generate new name: custCode_old_yyyyMMddHHmmss.ext
                        String ext = "";
                        int dotIndex = oldFileName.lastIndexOf(".");
                        if (dotIndex >= 0) {
                            ext = oldFileName.substring(dotIndex);
                        }
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yy_HH-mm"));
                        String renamed = existing.getCustCode() + "_old_" + timestamp + ext;

                        // Directory for old images
                        Path oldDir = Paths.get(uploadDir, "customer", "old");
                        if (!Files.exists(oldDir)) {
                            Files.createDirectories(oldDir); // make sure directory exists
                        }

                        File renamedFile = oldDir.resolve(renamed).toFile();

                        boolean renamedSuccessfully = oldFile.renameTo(renamedFile);
                        if (!renamedSuccessfully) {
                            // Fallback: copy & delete if rename fails
                            Files.copy(oldFile.toPath(), renamedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            oldFile.delete();
                        }
                    }
                }

                // Save new file normally
                String newFilePath = fileStorageService.saveImage(newImageFile);
                existing.setImagePath(newFilePath); // store URL path
            }

            // ----------------------------
            // Map other DTO fields
            // ----------------------------
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