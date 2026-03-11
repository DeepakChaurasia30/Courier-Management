package com.courier.management.Controller;

import java.io.File;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.courier.management.dto.ClientDTO;
import com.courier.management.service.ClientService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("get/{id}")
    public ClientDTO getClient(@PathVariable Integer id) {
        return clientService.getClient(id);
    }

    @PutMapping(value = "update/{id}", consumes = "multipart/form-data")
    public ClientDTO updateClient(@PathVariable Integer id,
            @RequestPart("data") ClientDTO dto,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "upi", required = false) MultipartFile upi,
            @RequestPart(value = "signature", required = false) MultipartFile signature) {

        System.out.println(dto.getClientName());

        return clientService.updateClient(id, dto);
    }
}
