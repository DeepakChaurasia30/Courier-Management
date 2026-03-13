package com.courier.management.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.courier.management.entity.InvoiceSequence;
import com.courier.management.repository.InvoiceSequenceRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class InvoiceSequenceServices {

     private final InvoiceSequenceRepository invoiceSequenceRepository;
    public Integer getInvSeq(String fY,Boolean isGst){
        Optional<InvoiceSequence> sequence;
        if(fY!=null)
        {
            sequence = invoiceSequenceRepository.findByFy(fY);

            if(sequence.isPresent())
            {
                if(isGst)
                {
                    return sequence.get().getGstLast()+1;
                }
                  
                return sequence.get().getNgLast()+1;
            }

            InvoiceSequence invoiceSequence = new InvoiceSequence();
            invoiceSequence.setFy(fY);

            invoiceSequenceRepository.save(invoiceSequence);
            // invoiceSequence.setGstLast(0);
            // invoiceSequence.setNgLast(0);
            
        }
        
           return 1;
    }
    
}
