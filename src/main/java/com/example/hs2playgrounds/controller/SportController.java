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
import reactor.core.publisher.Mono;

import static com.example.hs2playgrounds.util.ValidationMessages.*;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/sports")
public class SportController {

    private final SportService sportService;

    @GetMapping(value = "/")
    public Mono<ResponseEntity<?>> getAllSports(
            @RequestParam(value = "page", defaultValue = "0") @Min(value = 0, message = MSG_PAGE_NEGATIVE) int page,
            @RequestParam(value = "size", defaultValue = "5") @Min(value = 0, message = MSG_SIZE_NEGATIVE) @Max(value = 50, message = MSG_SIZE_TOO_BIG) int size
    ) {
        return sportService.findAll(page, size)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping(value="/{sportId}")
    public Mono<ResponseEntity<?>> getSportById(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long sportId
    ) {
        return sportService.findById(sportId)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/")
    public Mono<ResponseEntity<?>> createSport(
            @Valid @RequestBody SportDTO sportDto
    ) {
        return sportService.create(sportDto)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @PutMapping(value = "/{sportId}")
    public Mono<ResponseEntity<?>> updateSport(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long sportId,
            @Valid @RequestBody SportDTO sportDto
    ) {
        return sportService.updateSport(sportId, sportDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/{sportId}")
    public Mono<ResponseEntity<?>> deleteSport(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long sportId
    ) {
        return sportService.delete(sportId)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
    }
}
