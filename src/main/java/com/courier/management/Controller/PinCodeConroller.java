package com.courier.management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courier.management.dto.DestHandDTO;
import com.courier.management.dto.PinCodeDTO;
import com.courier.management.projection.PincodeSearchProjection;
import com.courier.management.service.DestServices;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/center")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PinCodeConroller {

    // No need @Autowired
    private final DestServices destservice;

    @GetMapping("/getpindet")
    public DestHandDTO getMethodName(@RequestParam String id) {
        return destservice.getByPincode(id);
    }

    @GetMapping("/getmpin")
    public List<PincodeSearchProjection> getMPin(@RequestParam String param) {
        return destservice.findMatchPin(param);
    }

}
