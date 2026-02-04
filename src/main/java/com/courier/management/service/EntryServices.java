package com.courier.management.service;

import java.util.Optional;
import org.springframework.stereotype.Service;

import com.courier.management.dto.EntryDTO;
import com.courier.management.entity.Center;
import com.courier.management.entity.Client;
import com.courier.management.entity.Customer;
import com.courier.management.entity.Entry;
import com.courier.management.enums.EntryStatus;
import com.courier.management.mapper.EntryMapper;
import com.courier.management.repository.EntryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntryServices {

    private final EntryRepository entryRepository;
    private final EntryMapper entryMapper;

    // Validation for AWB no. ADD,UPDATE,INVOICED
    public EntryStatus isEntryStatus(String awb_no) {
        String awb = awb_no.trim().toUpperCase(); // be safer side

        Optional<Entry> optentry = entryRepository.findByAwbNo(awb);

        if (optentry.isEmpty()) {
            return EntryStatus.NEW;
        }

        Entry entry = optentry.get();
        if (entry.getInvoice() == null) {
            return EntryStatus.UPDATE;
        }

        return EntryStatus.NOT_UPDATABLE;

    }

    // Bussiness logic for NEW

    public String saveNewAWb(EntryDTO entryDTO) {
        Entry entry = entryMapper.dtoTOEntry(entryDTO);

        if (entryDTO.getCustomerId() != null) {
            Customer customer = new Customer();
            customer.setCustId(entryDTO.getCustomerId());
            entry.setCustomer(customer);
        }

        if (entryDTO.getClientId() != null) {
            Client client = new Client();
            client.setClientId(entryDTO.getClientId());
            entry.setClient(client);
        }

        if (entryDTO.getDestid() != null) {
            Center center = new Center();
            center.setDestId(entryDTO.getDestid());
            entry.setCenter(center);
        }

        entryRepository.save(entry);
        return "Add success";
    }

}
