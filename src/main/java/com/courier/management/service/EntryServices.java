package com.courier.management.service;

import java.util.Optional;
import org.springframework.stereotype.Service;

import com.courier.management.entity.Entry;
import com.courier.management.enums.EntryStatus;
import com.courier.management.repository.EntryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntryServices {
    
    private final EntryRepository entryRepository;


    // Validation for AWB no. ADD,UPDATE,INVOICED
    public EntryStatus isEntryStatus(String awb_no)
    {

        Optional<Entry> optentry = entryRepository.findByAwbNo(awb_no);

        if(optentry.isEmpty())
        {
            return EntryStatus.NEW;
        }

        Entry entry = optentry.get();
        if(entry.getInvoice() == null)
        {
            return EntryStatus.UPDATE;
        }


        return EntryStatus.NOT_UPDATABLE;

    }


    
}
