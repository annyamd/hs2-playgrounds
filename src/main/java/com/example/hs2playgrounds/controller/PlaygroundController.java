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
import reactor.core.publisher.Mono;

import static com.example.hs2playgrounds.util.ValidationMessages.*;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/playgrounds")
public class PlaygroundController {

    private final PlaygroundService playgroundService;

    @GetMapping(value = "/")
    public Mono<ResponseEntity<?>> getAllPlaygrounds(
            @RequestParam(value = "page", defaultValue = "0") @Min(value = 0, message = MSG_PAGE_NEGATIVE) int page,
            @RequestParam(value = "size", defaultValue = "5") @Min(value = 0, message = MSG_SIZE_NEGATIVE) @Max(value = 50, message = MSG_SIZE_TOO_BIG) int size
    ) {
        return playgroundService.findAllPlaygrounds(page, size)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/{playgroundId}")
    public Mono<ResponseEntity<?>> getPlaygroundById(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long playgroundId
    ) {
        return playgroundService.getPlayground(playgroundId)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/")
    public Mono<ResponseEntity<?>> createPlayground(@Valid @RequestBody PlaygroundDTO playground) {
        return playgroundService.createPlayground(playground)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @PutMapping("/{playgroundId}")
    public Mono<ResponseEntity<?>> updatePlayground(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long playgroundId,
            @Valid @RequestBody PlaygroundDTO playground
    ) {
        return playgroundService.updatePlayground(playgroundId, playground)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{playgroundId}")
    public Mono<ResponseEntity<?>> deletePlayground(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long playgroundId
    ) {
        return playgroundService.deletePlayground(playgroundId)
                        .then(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
    }

}
