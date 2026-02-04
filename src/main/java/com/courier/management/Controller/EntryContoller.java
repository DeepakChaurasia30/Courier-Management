package com.courier.management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courier.management.enums.EntryStatus;
import com.courier.management.service.EntryServices;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class EntryContoller {

    private final EntryServices entryServices;

    @GetMapping("/entries")
    public EntryStatus getMethodName(@RequestParam String awb) {
        return entryServices.isEntryStatus(awb);
    }
    
    
}
