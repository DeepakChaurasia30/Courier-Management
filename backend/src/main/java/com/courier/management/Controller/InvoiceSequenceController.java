package com.courier.management.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.courier.management.service.InvoiceSequenceServices;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/invseq")
@RequiredArgsConstructor
public class InvoiceSequenceController {

    private final InvoiceSequenceServices invoiceSequenceServices;

    @GetMapping("/getseq")
    public Integer getClient(@RequestParam String fy,
            @RequestParam Boolean id) {
        return invoiceSequenceServices.getInvSeq(fy, id);
    }

}
