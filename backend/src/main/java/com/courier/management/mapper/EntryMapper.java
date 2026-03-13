package com.courier.management.mapper;

import org.springframework.stereotype.Component;

import com.courier.management.dto.EntryDTO;
import com.courier.management.entity.Entry;
import com.courier.management.entity.Invoice;

@Component
public class EntryMapper {

    public Entry dtoToEntry(EntryDTO dto) {

        if (dto == null) {
            throw new RuntimeException("EntryDTO cannot be null");
        }

        Entry entry = new Entry();

        // entry.setId(dto.getId()); // auto generated

        if (dto.getAwbNo() != null) {
            entry.setAwbNo(dto.getAwbNo().trim().toUpperCase());
        }

        entry.setAwbDate(dto.getAwbDate());
        entry.setWeight(dto.getWeight());
        entry.setCharge(dto.getCharge());
        entry.setSrvType(dto.getSrvType());
        entry.setPinCode(dto.getPinCode());
        entry.setNoPcs(dto.getNoPcs());
        entry.setPType(dto.getPType());

        // Relations handled in service layer

        return entry;
    }

    public EntryDTO entryToDTO(Entry entry) {

        if (entry == null) {
            throw new RuntimeException("Entry cannot be null");
        }

        EntryDTO dto = new EntryDTO();

        dto.setId(entry.getId());
        dto.setAwbNo(entry.getAwbNo());
        dto.setAwbDate(entry.getAwbDate());
        dto.setWeight(entry.getWeight());
        dto.setCharge(entry.getCharge());
        dto.setSrvType(entry.getSrvType());
        dto.setEntryDate(entry.getEntryDate());
        dto.setPinCode(entry.getPinCode());
        dto.setNoPcs(entry.getNoPcs());
        dto.setPType(entry.getPType());

        // Relations safe mapping
        dto.setDestid(entry.getDestid());
        dto.setClientId(entry.getClientid());
        dto.setCustomerId(entry.getCustid());

        if (entry.getInvoice() != null) {
            Invoice inv = entry.getInvoice();
            dto.setInvoiceId(inv.getInvNo());
        }

        return dto;
    }
}