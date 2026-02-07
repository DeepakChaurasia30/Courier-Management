package com.courier.management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courier.management.dto.EntryDTO;
import com.courier.management.enums.AnyStatus;
import com.courier.management.service.EntryServices;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/entry")
@RequiredArgsConstructor
public class EntryContoller {

    private final EntryServices entryServices;

    @GetMapping("/getStatus")
    public AnyStatus getEntryStatus(@RequestParam String awb) {
        return entryServices.isEntryStatus(awb);
    }

    @GetMapping("/getawb")
    public EntryDTO getSingalEntry(@RequestParam String awb) {
        return entryServices.getSingalEntry(awb);
    }

    @PostMapping("/add")
    public String addEntryNew(@RequestBody EntryDTO dto) {
        return entryServices.saveNewAWb(dto);
    }

    // Fix me we can use Patch
    @PutMapping("/update/{id}")
    public String updateEntry(@PathVariable Long id, @RequestBody EntryDTO dto) {
        entryServices.updateAwb(id, dto);
        return "Updated";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Long id) {

        entryServices.deleteEntry(id);

        return "Entry deleted successfully";
    }

}
