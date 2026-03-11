package com.courier.management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.courier.management.dto.InvoiceDTO;
import com.courier.management.dto.ResponseDTO;
import com.courier.management.enums.AnyStatus;
import com.courier.management.projection.InvProjection;
import com.courier.management.service.InvoiceServices;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin
@RestController
@RequestMapping("/inv")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceServices invoiceServices;

    @PostMapping("/add")
    public String genInv(@RequestBody InvoiceDTO dto) {
        // if (dto.getDisRate() == null) {
        // throw new RuntimeException(" found");

        // }
        invoiceServices.genInvoice(dto);
        return "Generated";
    }

    // @DeleteMapping("/delete")
    // public String deleteInv(@RequestParam String inv_no) {
    // invoiceServices.deleteInv(inv_no);
    // return "Entry deleted successfully";
    // }

    @PutMapping("/cancel")
    public String cancelInv(@RequestParam String inv_no) {
        invoiceServices.cancelInv(inv_no);
        return "Entry deleted successfully";
    }

    // @GetMapping("/getStatus")
    // public InvProjection getInvStatus(@RequestParam String inv_no) {
    // return invoiceServices.isInvStatus(inv_no);
    // }

    @GetMapping("/getInv/{invno}")
    public ResponseDTO getInv(@PathVariable String invno) {
        return invoiceServices.fetchInv(invno);
    }

    @GetMapping("/getpdf")
    public ResponseEntity<byte[]> generateInvoiceStream(@RequestParam String inv_no) throws Exception {

        byte[] pdf = invoiceServices.generateInvoice(inv_no);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=invoice_" + inv_no + ".pdf")
                .body(pdf);
    }

}
