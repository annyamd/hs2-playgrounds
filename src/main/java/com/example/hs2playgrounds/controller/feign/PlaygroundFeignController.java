package com.example.hs2playgrounds.controller.feign;

import com.example.hs2playgrounds.model.dto.PlaygroundDTO;
import com.example.hs2playgrounds.service.PlaygroundService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.hs2playgrounds.util.ValidationMessages.MSG_ID_NEGATIVE;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/feign/playgrounds")
public class PlaygroundFeignController {

    private final PlaygroundService playgroundService;

    @GetMapping(value = "/{playgroundId}")
    public ResponseEntity<?> getPlaygroundById(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long playgroundId
    ) {
        PlaygroundDTO pg = playgroundService.getPlayground(playgroundId);
        return ResponseEntity.ok().body(pg);
    }

}
