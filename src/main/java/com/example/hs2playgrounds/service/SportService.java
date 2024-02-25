package com.example.hs2playgrounds.service;

import com.example.hs2playgrounds.controller.exceptions.not_found.NotFoundException;
import com.example.hs2playgrounds.controller.exceptions.not_found.SportNotFoundException;
import com.example.hs2playgrounds.model.dto.SportDTO;
import com.example.hs2playgrounds.model.entity.Sport;
import com.example.hs2playgrounds.repository.SportRepository;
import com.example.hs2playgrounds.util.GeneralService;
import com.example.hs2playgrounds.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SportService extends GeneralService<Sport, SportDTO> {

    private final SportRepository sportRepository;
    private final Mapper<Sport, SportDTO> mapper = new SportMapper();

    @Transactional
    public SportDTO updateSport(long id, SportDTO dto) {
        getEntityById(id);
        Sport updated = mapper.dtoToEntity(dto);
        updated.setSportId(id);
        sportRepository.save(updated);
        return mapper.entityToDto(updated);
    }

    protected List<Sport> findAllByIds(List<Long> ids) {
        return sportRepository.findAllById(ids);
    }

    @Override
    protected NotFoundException getNotFoundIdException(long id) {
        return new SportNotFoundException("id = " + id);
    }

    @Override
    protected Mapper<Sport, SportDTO> getMapper() {
        return mapper;
    }

    @Override
    protected JpaRepository<Sport, Long> getRepository() {
        return sportRepository;
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
