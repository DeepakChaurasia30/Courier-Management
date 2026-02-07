package com.courier.management.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.courier.management.dto.EntryDTO;
import com.courier.management.entity.Center;
import com.courier.management.entity.Client;
import com.courier.management.entity.Customer;
import com.courier.management.entity.Entry;
import com.courier.management.enums.AnyStatus;
import com.courier.management.mapper.EntryMapper;
import com.courier.management.repository.EntryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntryServices {

    private final EntryRepository entryRepository;
    private final EntryMapper entryMapper;
// Entry need this unique Referce so NEW required
    // private final Customer customer;
    // private final Client client;
    // private final Center center;

    // Validation for AWB no. ADD,UPDATE,INVOICED
    public AnyStatus isEntryStatus(String awb_no) {
        String awb = awb_no.trim().toUpperCase(); // be safer side

        Optional<Entry> optentry = entryRepository.findByAwbNo(awb);

        if (optentry.isEmpty()) {
            return AnyStatus.NEW;
        }

        Entry entry = optentry.get();
        if (entry.getInvoice() == null) {
            return AnyStatus.UPDATE;
        }

        return AnyStatus.NOT_UPDATABLE;

    }

    // Bussiness logic for NEW

    public String saveNewAWb(EntryDTO entryDTO) {
        Entry entry = entryMapper.dtoTOEntry(entryDTO);

        // tempoary fix 
                // Auto set entry date to today
        entry.setEntryDate(LocalDateTime.now());

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

    // Update Logic Starts here
    // update helper function
    public EntryDTO getSingalEntry(String awb_no) {
    
        String awb = awb_no.trim().toUpperCase(); // be safer side

        Entry entry = entryRepository.findByAwbNo1(awb);

        EntryDTO dto = entryMapper.EntryTODTO(entry);

        return dto;
    }

    public String updateAwb(Long id, EntryDTO dto) {

        Entry entry = entryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Iteam not found"));

        // entry.setAwbNo(dto.getAwbNo()); not logically correct
        entry.setAwbDate(dto.getAwbDate());
        entry.setWeight(dto.getWeight());
        entry.setCharge(dto.getCharge());
        entry.setSrvType(dto.getSrvType());

       if (dto.getCustomerId() != null) {
            Customer customer = new Customer();
            customer.setCustId(dto.getCustomerId());
            entry.setCustomer(customer);
        }

        if (dto.getDestid() != null) {
            Center center = new Center();
            center.setDestId(dto.getDestid());
            entry.setCenter(center);
        }

        entryRepository.save(entry);
        return "Add success";
    }


    // delete logic if Invoice==null
    public void deleteEntry(Long id) {

        entryRepository.deleteById(id);
    }

}
