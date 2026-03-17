package com.courier.management.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.courier.management.dto.EntryDTO;
import com.courier.management.dto.EntryMemoDTO;
import com.courier.management.entity.Center;
import com.courier.management.entity.Client;
import com.courier.management.entity.Customer;
import com.courier.management.entity.Entry;
import com.courier.management.enums.AnyStatus;
import com.courier.management.mapper.EntryMapper;
import com.courier.management.projection.EntryProjection;
import com.courier.management.projection.EntryTableProjection;
import com.courier.management.repository.EntryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntryServices {

    private final EntryRepository entryRepository;
    private final EntryMapper entryMapper;

    /*
     * ---------------------------------------------
     * CHECK ENTRY STATUS (NEW / UPDATE / INVOICED)
     * ---------------------------------------------
     */

    public AnyStatus isEntryStatus(String awbNo) {

        if (awbNo == null || awbNo.isBlank()) {
            throw new RuntimeException("AWB number cannot be empty");
        }

        String awb = awbNo.trim().toUpperCase();

        Optional<Entry> optEntry = entryRepository.findByAwbNo(awb);

        if (optEntry.isEmpty()) {
            return AnyStatus.NEW;
        }

        Entry entry = optEntry.get();

        if (entry.getInvoice() == null) {
            return AnyStatus.UPDATE;
        }

        return AnyStatus.NOT_UPDATABLE;
    }

    /*
     * ---------------------------------------------
     * SAVE NEW AWB
     * ---------------------------------------------
     */

    public String saveNewAWb(EntryDTO dto) {

        validateEntryDTO(dto);

        Entry entry = entryMapper.dtoToEntry(dto);

        entry.setAwbNo(dto.getAwbNo().trim().toUpperCase());
        entry.setPtype(dto.getPtype().toUpperCase());
        entry.setEntryDate(LocalDateTime.now());

        setCustomer(dto, entry);
        setClient(dto, entry);
        setCenter(dto, entry);

        entryRepository.save(entry);

        return "Entry saved successfully";
    }

    /*
     * ---------------------------------------------
     * GET SINGLE ENTRY
     * ---------------------------------------------
     */

    public EntryDTO getSingalEntry(String awbNo) {

        if (awbNo == null || awbNo.isBlank()) {
            throw new RuntimeException("AWB number cannot be empty");
        }

        String awb = awbNo.trim().toUpperCase();

        Entry entry = entryRepository.findByAwbNo1(awb);

        if (entry == null) {
            throw new RuntimeException("Entry not found for AWB: " + awb);
        }

        return entryMapper.entryToDTO(entry);
    }

    public List<EntryTableProjection> getTopRecords() {

        List<EntryTableProjection> list = entryRepository.findTop50ByOrderByEntryDateDesc();

        if (list.isEmpty()) {
            throw new RuntimeException("No valid Record Found for Table");
        }

        return list;
    }

    /*
     * ---------------------------------------------
     * UPDATE ENTRY
     * ---------------------------------------------
     */

    public String updateAwb(Long id, EntryDTO dto) {

        if (id == null) {
            throw new RuntimeException("Entry ID is required");
        }

        validateEntryDTO(dto);

        Entry entry = entryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        entry.setAwbDate(dto.getAwbDate());
        entry.setWeight(dto.getWeight());
        entry.setPtype(dto.getPtype().toUpperCase());
        entry.setNoPcs(dto.getNoPcs());
        entry.setCharge(dto.getCharge());
        entry.setSrvType(dto.getSrvType());
        entry.setPinCode(dto.getPinCode());
        // Introduce new fileds
        entry.setCourierName(dto.getCourierName());
        entry.setDimension(dto.getDimension());
        entry.setVolWeight(dto.getVolWeight());
        entry.setRemark(dto.getRemark());

        setCustomer(dto, entry);
        setCenter(dto, entry);

        entryRepository.save(entry);

        return "Entry updated successfully";
    }

    // Fetch data to display daily tracking

    public List<EntryTableProjection> getEntryBeetweenDates(Integer clientid, LocalDate startDate, LocalDate endDate) {
        List<EntryTableProjection> list = entryRepository.findByClientidAndAwbDateBetween(clientid, startDate, endDate);

        if (list.isEmpty()) {
            throw new RuntimeException("No Valid Records Found");
        }
        return list;
    }

    /*
     * ---------------------------------------------
     * DELETE ENTRY
     * ---------------------------------------------
     */

    public void deleteEntry(Long id) {

        if (id == null) {
            throw new RuntimeException("Entry ID required");
        }

        Entry entry = entryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (entry.getInvoice() != null) {
            throw new RuntimeException("Cannot delete invoiced entry");
        }

        entryRepository.delete(entry);
    }

    /*
     * ---------------------------------------------
     * Memo METHOD
     * ---------------------------------------------
     */

    public EntryMemoDTO getSummary(Long custID) {
        EntryMemoDTO getSumandCount = entryRepository.getSummary(custID);
        if (getSumandCount.equals(null)) {
            throw new RuntimeException("No Valid Record Founds");
        }
        return getSumandCount;
    }

    /*
     * ---------------------------------------------
     * VALIDATION METHOD
     * ---------------------------------------------
     */

    private void validateEntryDTO(EntryDTO dto) {

        if (dto == null) {
            throw new RuntimeException("Entry data missing");
        }

        if (dto.getAwbNo() == null || dto.getAwbNo().isBlank()) {
            throw new RuntimeException("AWB number is required");
        }

        if (dto.getAwbDate() == null) {
            throw new RuntimeException("AWB date is required");
        }

        if (dto.getWeight() == null) {
            throw new RuntimeException("Weight is required");
        }

        if (dto.getCharge() == null) {
            throw new RuntimeException("Charge is required");
        }

        if (dto.getSrvType() == null || dto.getSrvType().isBlank()) {
            throw new RuntimeException("Service type is required");
        }

        if (dto.getPinCode() == null || dto.getPinCode().isBlank()) {
            throw new RuntimeException("Pincode is required");
        }

        if (dto.getNoPcs() == null || dto.getNoPcs() <= 0) {
            throw new RuntimeException("Number of pieces must be greater than 0");
        }

        if (dto.getPtype() == null || dto.getPtype().isBlank()) {
            throw new RuntimeException("Parcel type (DX / ND) is required");
        }

        if (dto.getCustomerId() == null) {
            throw new RuntimeException("Customer ID is required");
        }

        if (dto.getClientId() == null) {
            throw new RuntimeException("Client ID is required");
        }

        if (dto.getDestid() == null) {
            throw new RuntimeException("Destination center is required");
        }
    }

    /*
     * ---------------------------------------------
     * HELPER METHODS
     * ---------------------------------------------
     */

    private void setCustomer(EntryDTO dto, Entry entry) {

        Customer customer = new Customer();
        customer.setCustId(dto.getCustomerId());
        entry.setCustomer(customer);
    }

    private void setClient(EntryDTO dto, Entry entry) {

        Client client = new Client();
        client.setClientId(dto.getClientId());
        entry.setClient(client);
    }

    private void setCenter(EntryDTO dto, Entry entry) {

        Center center = new Center();
        center.setDestId(dto.getDestid());
        entry.setCenter(center);
    }
}