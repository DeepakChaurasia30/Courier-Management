package com.courier.management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.courier.management.dto.InvoiceDTO;
import com.courier.management.enums.AnyStatus;
import com.courier.management.service.InvoiceServices;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/inv")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceServices invoiceServices;

    @PostMapping("/add")
    public String getInv(@RequestBody InvoiceDTO dto) {
        invoiceServices.genInvoice(dto);
        return "Generated";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteInv(@PathVariable String id) {
        invoiceServices.deleteInv(id);
        return "Entry deleted successfully";
    }

    @GetMapping("/getStatus")
    public AnyStatus getInvStatus(@RequestParam String inv_no) {
        return invoiceServices.isInvStatus(inv_no);
    }

}
