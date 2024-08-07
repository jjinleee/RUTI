package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.RoutineDTO;
import com.hyejin.ruti.entity.RoutineEntity;
import com.hyejin.ruti.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoutineService {

    @Autowired
    private final RoutineRepository routineRepository;

    public List<RoutineEntity> getRoutines(String userEmail) {
        return routineRepository.findByUserEmail(userEmail);
    }

    public RoutineEntity addRoutine(RoutineDTO routineDTO, String userEmail) {
        RoutineEntity routineEntity = RoutineEntity.toRoutineEntity(routineDTO, userEmail);
        return routineRepository.save(routineEntity);
    }

    public boolean updateRoutine(Long id, RoutineDTO routineDTO) {
        Optional<RoutineEntity> routineEntityOptional = routineRepository.findById(id);
        if (routineEntityOptional.isPresent()) {
            RoutineEntity routineEntity = routineEntityOptional.get();
            routineEntity.setTitle(routineDTO.getTitle());
            routineEntity.setTime(routineDTO.getTime());
            routineEntity.setDuration(routineDTO.getDuration());
            routineEntity.setStatus(routineDTO.getStatus());
            routineEntity.setFrequency(routineDTO.getFrequency());
            routineRepository.save(routineEntity);
            return true;
        }
        return false;
    }

    public boolean deleteRoutine(Long id) {
        Optional<RoutineEntity> routineEntityOptional = routineRepository.findById(id);
        if (routineEntityOptional.isPresent()) {
            routineRepository.delete(routineEntityOptional.get());
            return true;
        }
        return false;
    }
}