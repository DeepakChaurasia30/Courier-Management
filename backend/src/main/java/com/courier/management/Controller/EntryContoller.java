package com.courier.management.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courier.management.dto.EntryDTO;
import com.courier.management.dto.EntryMemoDTO;
import com.courier.management.entity.Entry;
import com.courier.management.enums.AnyStatus;
import com.courier.management.projection.EntryProjection;
import com.courier.management.projection.EntryTableProjection;
import com.courier.management.service.EntryServices;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
public class EntryContoller {

    private final EntryServices entryServices;

    @GetMapping("/getstatus")
    public AnyStatus getEntryStatus(@RequestParam String awb) {
        return entryServices.isEntryStatus(awb);
    }
    @GetMapping("/gettable")
    public List<EntryTableProjection> getTableRecords() {
        return entryServices.getTopRecords();
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

    @GetMapping("/summary")
    public EntryMemoDTO getSummary(@RequestParam Long custID) {
       return entryServices.getSummary(custID);
    }
    

}
