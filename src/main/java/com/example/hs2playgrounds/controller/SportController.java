package com.example.hs2playgrounds.controller;

import com.example.hs2playgrounds.model.dto.SportDTO;
import com.example.hs2playgrounds.service.SportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.hs2playgrounds.util.ValidationMessages.*;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/sports")
public class SportController {

    private final SportService sportService;

    @GetMapping(value = "/")
    public ResponseEntity<?> getAllSports(
            @RequestParam(value = "page", defaultValue = "0") @Min(value = 0, message = MSG_PAGE_NEGATIVE) int page,
            @RequestParam(value = "size", defaultValue = "5") @Min(value = 0, message = MSG_SIZE_NEGATIVE) @Max(value = 50, message = MSG_SIZE_TOO_BIG) int size
    ) {
        List<SportDTO> sportDTOs = sportService.findAll(page, size);
        return ResponseEntity.ok().body(sportDTOs);
    }

    @GetMapping(value="/{sportId}")
    public ResponseEntity<?> getSportById(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long sportId
    ) {
        SportDTO sport = sportService.findById(sportId);
        return ResponseEntity.ok().body(sport);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> createSport(
            @Valid @RequestBody SportDTO sportDto
    ) {
        SportDTO created = sportService.create(sportDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{sportId}")
    public ResponseEntity<?> updateSport(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long sportId,
            @Valid @RequestBody SportDTO sportDto
    ) {
        SportDTO updated = sportService.updateSport(sportId, sportDto);
        return ResponseEntity.ok().body(updated);
    }

    @DeleteMapping(value = "/{sportId}")
    public ResponseEntity<?> deleteSport(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long sportId
    ) {
        sportService.delete(sportId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
