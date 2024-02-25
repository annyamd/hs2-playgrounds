package com.example.hs2playgrounds.controller;

import com.example.hs2playgrounds.model.dto.PlaygroundDTO;
import com.example.hs2playgrounds.service.PlaygroundService;
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
@RequestMapping(path = "/playgrounds")
public class PlaygroundController {

    private final PlaygroundService playgroundService;

    @GetMapping(value = "/")
    public ResponseEntity<?> getAllPlaygrounds(
            @RequestParam(value = "page", defaultValue = "0") @Min(value = 0, message = MSG_PAGE_NEGATIVE) int page,
            @RequestParam(value = "size", defaultValue = "5") @Min(value = 0, message = MSG_SIZE_NEGATIVE) @Max(value = 50, message = MSG_SIZE_TOO_BIG) int size
    ) {
        List<PlaygroundDTO> playgroundDTOs = playgroundService.findAllPlaygrounds(page, size);
        return ResponseEntity.ok().body(playgroundDTOs);
    }

    @GetMapping(value = "/{playgroundId}")
    public ResponseEntity<?> getPlaygroundById(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long playgroundId
    ) {
        PlaygroundDTO pg = playgroundService.getPlayground(playgroundId);
        return ResponseEntity.ok().body(pg);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> createPlayground(@Valid @RequestBody PlaygroundDTO playground) {
        PlaygroundDTO created = playgroundService.createPlayground(playground);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{playgroundId}")
    public ResponseEntity<?> updatePlayground(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long playgroundId,
            @Valid @RequestBody PlaygroundDTO playground
    ) {
        PlaygroundDTO updated = playgroundService.updatePlayground(playgroundId, playground);
        return ResponseEntity.ok().body(updated);
    }

    @DeleteMapping("/{playgroundId}")
    public ResponseEntity<?> deletePlayground(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long playgroundId
    ) {
        playgroundService.deletePlayground(playgroundId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
