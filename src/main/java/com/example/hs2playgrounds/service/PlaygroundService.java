package com.example.hs2playgrounds.service;

import com.example.hs2playgrounds.controller.exceptions.invalid.InvalidAvailabilityException;
import com.example.hs2playgrounds.controller.exceptions.not_found.PlaygroundNotFoundException;
import com.example.hs2playgrounds.model.dto.PlaygroundAvailabilityDTO;
import com.example.hs2playgrounds.model.dto.PlaygroundDTO;
import com.example.hs2playgrounds.model.entity.*;
import com.example.hs2playgrounds.repository.PlaygroundRepository;
import com.example.hs2playgrounds.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaygroundService {

    private final PlaygroundRepository playgroundRepository;
    private final SportService sportService;
    private final PlaygroundMapper pgMapper = new PlaygroundMapper();
    private final AvailabilityMapper availabilityMapper = new AvailabilityMapper();

    public Flux<PlaygroundDTO> findAllPlaygrounds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.just(pageable)
                .flatMap(pg -> Mono
                        .fromCallable(() -> playgroundRepository.findAll(pg))
                        .subscribeOn(Schedulers.boundedElastic()))
                .flatMapMany(Flux::fromIterable)
                .map(pgMapper::entityToDto);
    }

    public Mono<PlaygroundDTO> getPlayground(Long id) {
        return Mono.just(id)
                .flatMap(this::getEntityById)
                .map(pgMapper::entityToDto);
    }

    public Mono<PlaygroundDTO> createPlayground(PlaygroundDTO playgroundDTO) {
        Playground playground = pgMapper.dtoToEntity(playgroundDTO);
        playground.setId(null);
        return Mono.just(playground)
                .flatMap(this::save)
                .map(pgMapper::entityToDto);
    }

    @Transactional
    public Mono<PlaygroundDTO> updatePlayground(long id, PlaygroundDTO dto) {
        return Mono.just(id)
                .flatMap(this::getEntityById)
                .map(it -> {
                    Playground updated = pgMapper.dtoToEntity(dto);
                    updated.setId(id);
                    updated.setPlaygroundAvailability(it.getPlaygroundAvailability());
                    return updated;
                })
                .flatMap(this::save)
                .map(pgMapper::entityToDto);
    }

    public Mono<Void> deletePlayground(long id) {
        return Mono.just(id)
                .flatMap(this::deleteById);
    }

    private Mono<Playground> save(Playground pg) {
        return Mono.fromCallable(() -> playgroundRepository.save(pg))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Playground> getEntityById(long id) {
        return Mono.fromCallable(() -> playgroundRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(pg -> {
                    if (pg.isEmpty()) {
                        return Mono.error(new PlaygroundNotFoundException("id = " + id));
                    }
                    return Mono.just(pg.get());
                });
    }

    private Mono<Void> deleteById(long id) {
        return Mono.fromCallable(() -> {
                    playgroundRepository.deleteById(id);
                    return 1;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    static class AvailabilityMapper implements Mapper<PlaygroundAvailability, PlaygroundAvailabilityDTO> {

        @Override
        public PlaygroundAvailabilityDTO entityToDto(PlaygroundAvailability entity) {
            return new PlaygroundAvailabilityDTO(
                    entity.getId(),
                    entity.getIsAvailable(),
                    entity.getAvailableFrom(),
                    entity.getAvailableTo(),
                    entity.getCapacity()
            );
        }

        @Override
        public PlaygroundAvailability dtoToEntity(PlaygroundAvailabilityDTO dto) {
            if (dto.getIsAvailable() && (dto.getAvailableFrom() == null || dto.getAvailableTo() == null)) {
                throw new InvalidAvailabilityException("time not chosen");
            }
            return new PlaygroundAvailability(
                    null,
                    dto.getIsAvailable(),
                    dto.getAvailableFrom(),
                    dto.getAvailableTo(),
                    dto.getCapacity(),
                    null);
        }
    }


    class PlaygroundMapper implements Mapper<Playground, PlaygroundDTO> {

        @Override
        public PlaygroundDTO entityToDto(Playground entity) {
            PlaygroundDTO dto = new PlaygroundDTO();
            dto.setPlaygroundId(entity.getId());
            dto.setPlaygroundName(entity.getPlaygroundName());
            dto.setLocation(entity.getLocation());
            dto.setLatitude(entity.getLatitude());
            dto.setLongitude(entity.getLongitude());
            dto.setPlaygroundAvailability(availabilityMapper.entityToDto(entity.getPlaygroundAvailability()));
            dto.setSportsId(entity.getSports()
                    .stream()
                    .map(Sport::getSportId)
                    .collect(Collectors.toList()));
            return dto;
        }

        @Override
        public Playground dtoToEntity(PlaygroundDTO dto) {
            Playground pg = new Playground();
            pg.setId(dto.getPlaygroundId());
            pg.setPlaygroundName(dto.getPlaygroundName());
            pg.setLocation(dto.getLocation());
            pg.setLatitude(dto.getLatitude());
            pg.setLongitude(dto.getLongitude());
            pg.setPlaygroundAvailability(availabilityMapper.dtoToEntity(dto.getPlaygroundAvailability()));
            List<Sport> sports = sportService.findAllByIds(dto.getSportsId());
            pg.setSports(sports);
            return pg;
        }
    }
}
