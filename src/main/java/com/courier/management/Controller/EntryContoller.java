package com.courier.management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courier.management.dto.EntryDTO;
import com.courier.management.enums.EntryStatus;
import com.courier.management.service.EntryServices;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class EntryContoller {

    private final EntryServices entryServices;

    @GetMapping("/getStatus")
    public EntryStatus getEntryStatus(@RequestParam String awb) {
        return entryServices.isEntryStatus(awb);
    }

    @PostMapping("/add")
    public String addEntryNew(@RequestBody EntryDTO dto) {
        
        
        return entryServices.saveNewAWb(dto);
    }
    
    
    
}
