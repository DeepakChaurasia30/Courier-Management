package com.courier.management.helper;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.courier.management.entity.Entry;
import com.courier.management.repository.EntryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvoiceHelper {
        
        private final EntryRepository entryRepository;
        
        public List<Entry> getAwbData(Long custId,
        Integer clientId,
        LocalDate startDate,
        LocalDate endDate)
    {
       List<Entry> list = entryRepository.findByCustidAndClientidAndAwbDateBetweenAndInvoiceIsNull(custId, clientId, startDate, endDate);

       return list;
    }
    
}
