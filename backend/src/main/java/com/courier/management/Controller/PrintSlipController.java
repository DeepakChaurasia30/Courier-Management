package com.courier.management.Controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.courier.management.dto.PrintSlipDTO;
import com.courier.management.service.PrintSlipService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/slip")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PrintSlipController {

    private final PrintSlipService printSlipService;

    @PostMapping(value = "/getpdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> printSlip(@RequestBody List<PrintSlipDTO> dto) {

        try {

            byte[] pdf = printSlipService.generateSlip(dto);

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=awb_slip.pdf")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().build();
        }
    }
}