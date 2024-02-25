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

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaygroundService {

    private final PlaygroundRepository playgroundRepository;
    private final SportService sportService;
    private final PlaygroundMapper pgMapper = new PlaygroundMapper();
    private final AvailabilityMapper availabilityMapper = new AvailabilityMapper();

    public List<PlaygroundDTO> findAllPlaygrounds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Playground> playgroundPage = playgroundRepository.findAll(pageable);
        return playgroundPage.getContent()
                .stream()
                .map(pgMapper::entityToDto)
                .toList();
    }

    public PlaygroundDTO getPlayground(Long id) {
        Playground pg = playgroundRepository.findById(id)
                .orElseThrow(() -> new PlaygroundNotFoundException("id = " + id));
        return pgMapper.entityToDto(pg);
    }

    public PlaygroundDTO createPlayground(PlaygroundDTO playgroundDTO) {
        Playground playground = pgMapper.dtoToEntity(playgroundDTO);
        playground.setId(null);
        playgroundRepository.save(playground);
        return pgMapper.entityToDto(playground);
    }

    @Transactional
    public PlaygroundDTO updatePlayground(long id, PlaygroundDTO dto) {
        Playground found = playgroundRepository.findById(id)
                .orElseThrow(() -> new PlaygroundNotFoundException("id = " + id));
        Playground updated = pgMapper.dtoToEntity(dto);
        updated.setId(id);
        updated.setPlaygroundAvailability(found.getPlaygroundAvailability());
//        updated.setBookingList(found.getBookingList());
//
//        if (!updated.getPlaygroundAvailability().getIsAvailable()) {
//            updated.setBookingList(null);
//        }

        checkTime(found, updated);

        playgroundRepository.save(updated);
        return pgMapper.entityToDto(updated);
    }

    private void checkTime(Playground old, Playground updated) {
        LocalTime oldStart = old.getPlaygroundAvailability().getAvailableFrom();
        LocalTime oldEnd = old.getPlaygroundAvailability().getAvailableTo();
        LocalTime newStart = updated.getPlaygroundAvailability().getAvailableFrom();
        LocalTime newEnd = updated.getPlaygroundAvailability().getAvailableTo();

//        if (oldStart != newStart || oldEnd != newEnd) {
//            if (updated.getBookingList() != null && !updated.getBookingList().isEmpty()) {
//                throw new InvalidAvailabilityException(
//                        "Booking records exists for this playground. Switch isAvailable to false to remove all records.");
//            }
//        }
    }


    public void deletePlayground(long id) {
        playgroundRepository.findById(id)
                .orElseThrow(() -> new PlaygroundNotFoundException("id = " + id));
        playgroundRepository.deleteById(id);
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
