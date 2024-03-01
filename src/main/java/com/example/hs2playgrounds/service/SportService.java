package com.example.hs2playgrounds.service;

import com.example.hs2playgrounds.controller.exceptions.not_found.NotFoundException;
import com.example.hs2playgrounds.controller.exceptions.not_found.PlaygroundNotFoundException;
import com.example.hs2playgrounds.controller.exceptions.not_found.SportNotFoundException;
import com.example.hs2playgrounds.model.dto.SportDTO;
import com.example.hs2playgrounds.model.entity.Sport;
import com.example.hs2playgrounds.repository.SportRepository;
import com.example.hs2playgrounds.util.GeneralService;
import com.example.hs2playgrounds.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SportService {

    private final SportRepository sportRepository;
    private final Mapper<Sport, SportDTO> mapper = new SportMapper();

    public Mono<SportDTO> updateSport(long id, SportDTO dto) {
        Sport updated = mapper.dtoToEntity(dto);
        updated.setSportId(id);
        return getEntityById(id)
                .flatMap(it -> save(updated))
                .map(mapper::entityToDto);
    }

    private Mono<Sport> save(Sport sport) {
        return Mono.fromCallable(() -> sportRepository.save(sport))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Sport> getEntityById(long id) {
        return Mono.fromCallable(() -> sportRepository.findById(id).orElseThrow(() -> getNotFoundIdException(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Void> deleteById(long id) {
        return Mono.fromCallable(() -> {
                    sportRepository.deleteById(id);
                    return 1;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Flux<SportDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.just(pageable)
                .flatMap(pg -> Mono
                        .fromCallable(() -> sportRepository.findAll(pg).getContent())
                        .subscribeOn(Schedulers.boundedElastic()))
                .flatMapMany(Flux::fromIterable)
                .map(mapper::entityToDto);
    }

    public Mono<SportDTO> findById(long id) {
        return Mono.just(id)
                .flatMap(this::getEntityById)
                .map(mapper::entityToDto);
    }

    public Mono<SportDTO> create(SportDTO dto) {
        return Mono.just(dto)
                .map(mapper::dtoToEntity)
                .flatMap(this::save)
                .map(mapper::entityToDto);
    }

    public Mono<Void> delete(long id) {
        return Mono.just(id)
                .flatMap(this::getEntityById)
                .flatMap(it -> deleteById(id));
    }

    protected List<Sport> findAllByIds(List<Long> ids) {
        return sportRepository.findAllById(ids);
    }

    protected NotFoundException getNotFoundIdException(long id) {
        return new SportNotFoundException("id = " + id);
    }

    static class SportMapper implements Mapper<Sport, SportDTO> {

        @Override
        public SportDTO entityToDto(Sport entity) {
            return new SportDTO(entity.getSportId(), entity.getSportName());
        }

        @Override
        public Sport dtoToEntity(SportDTO dto) {
            return new Sport(null, dto.getSportType());
        }
    }

}
