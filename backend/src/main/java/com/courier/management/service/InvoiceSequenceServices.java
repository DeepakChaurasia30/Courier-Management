package com.courier.management.service;

import java.util.Optional;

import com.courier.management.entity.Client;
import com.courier.management.repository.ClientRepository;
import org.springframework.stereotype.Service;

import com.courier.management.entity.InvoiceSequence;
import com.courier.management.repository.InvoiceSequenceRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class InvoiceSequenceServices {

     private final InvoiceSequenceRepository invoiceSequenceRepository;
     private final ClientRepository clientRepository;
    public Integer getInvSeq(String fY,Integer clientId,Boolean isGst){
        Optional<InvoiceSequence> sequence;
        if(fY!=null && clientId!=null)
        {
            sequence = invoiceSequenceRepository.findByClient_ClientIdAndFy(clientId,fY);

            if(sequence.isPresent())
            {
                if(isGst)
                {
                    return sequence.get().getGstLast()+1;
                }
                  
                return sequence.get().getNgLast()+1;
            }

            InvoiceSequence invoiceSequence = new InvoiceSequence();
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(()-> new RuntimeException("Client no found from Sequence"));
            invoiceSequence.setFy(fY);
            invoiceSequence.setClient(client);

            invoiceSequenceRepository.save(invoiceSequence);
            // invoiceSequence.setGstLast(0);
            // invoiceSequence.setNgLast(0);
            
        }
        
           return 1;
    }
    
}
