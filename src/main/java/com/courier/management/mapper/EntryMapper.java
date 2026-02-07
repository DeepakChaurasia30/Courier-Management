package com.courier.management.mapper;

import org.springframework.stereotype.Component;

import com.courier.management.dto.EntryDTO;
import com.courier.management.entity.Entry;

@Component
public class EntryMapper {

    public Entry dtoTOEntry(EntryDTO dto) {
        Entry entry = new Entry();

        // entry.setId(dto.getId()); auto-gen field
        entry.setAwbNo(dto.getAwbNo());
        entry.setAwbDate(dto.getAwbDate());
        entry.setWeight(dto.getWeight());
        entry.setCharge(dto.getCharge());
        entry.setSrvType(dto.getSrvType());
        entry.setEntryDate(dto.getEntryDate());

        // handle cust, dest,client at Service Logic

        return entry;
    }

    public EntryDTO EntryTODTO(Entry entry) {
        EntryDTO entryDTO = new EntryDTO();
        entryDTO.setId(entry.getId());
        entryDTO.setAwbNo(entry.getAwbNo());
        entryDTO.setAwbDate(entry.getAwbDate());
        entryDTO.setWeight(entry.getWeight());
        entryDTO.setCharge(entry.getCharge());
        entryDTO.setSrvType(entry.getSrvType());
        entryDTO.setEntryDate(entry.getEntryDate());
        entryDTO.setDestid(entry.getDestid());
        entryDTO.setClientId(entry.getClientid());
        entryDTO.setCustomerId(entry.getCustid());


        return entryDTO;

    }

}
