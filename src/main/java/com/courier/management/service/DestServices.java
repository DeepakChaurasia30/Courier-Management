package com.courier.management.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.courier.management.dto.DestHandDTO;
import com.courier.management.entity.Center;
import com.courier.management.entity.PinCode;
import com.courier.management.entity.State;
import com.courier.management.projection.PincodeSearchProjection;
import com.courier.management.repository.PinCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestServices {

    private final PinCodeRepository pinrepo;

    public DestHandDTO getByPincode(String pincode) {
        PinCode pin = pinrepo.findById(pincode)
                .orElseThrow(() -> new RuntimeException("Pincode not found"));
        Center center = pin.getCenter();
        State state = center.getState();

        DestHandDTO ddto = new DestHandDTO();

        ddto.setPinCode(pincode);

        // center info
        ddto.setDestId(center.getDestId());
        ddto.setCenterName(center.getDestName());

        // state info
        ddto.setStateCode(state.getStateCode());
        ddto.setStateName(state.getStateName());
        ddto.setZone(state.getZone());

        return ddto;

    }
    
    // implement dual search feature easy for search -23-02-26
    // core to send pin suggestion on frontend 

    // public List<PinCodeDTO> findMatchPin(String prefix) {
    //     List<PinCode> pin = pinrepo.findByPincodeStartingWith(prefix);

    //     List<PinCodeDTO> ll = new ArrayList<PinCodeDTO>();
    //     for (PinCode p : pin) {
    //         PinCodeDTO dto = new PinCodeDTO();

    //         Center center = p.getCenter();

    //         dto.setDestId(center.getDestId());
    //         dto.setPincode(p.getPincode());
    //         dto.setDestName(center.getDestName());
    //         ll.add(dto);

    //     }

    //     return ll;
    // }

    public List<PincodeSearchProjection> findMatchPin(String keyword)
    {
        List<PincodeSearchProjection> ll = pinrepo.searchByPinOrName(keyword);

        return ll;
    }

}
