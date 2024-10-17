package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.RoutineDTO;
import com.hyejin.ruti.entity.RoutineEntity;
import com.hyejin.ruti.entity.RoutineStateEntity;
import com.hyejin.ruti.repository.RoutineRepository;
import com.hyejin.ruti.repository.RoutineStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

    @Autowired
    private final RoutineRepository routineRepository;

    @Autowired
    private final RoutineStateRepository routineStateRepository;


    @Transactional
    public List<RoutineEntity> getRoutinesForToday(String userEmail) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayStr = today.format(formatter); // 현재 날짜를 "yyyy-MM-dd" 형식의 문자열로 변환

        List<RoutineEntity> routineEntities = routineRepository.findAllByUserEmailAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userEmail, todayStr, todayStr);

        return routineEntities;
    }


    public RoutineEntity addRoutine(RoutineDTO routineDTO, String userEmail) {
        // 루틴 엔티티 생성 및 저장
        RoutineEntity routineEntity = RoutineEntity.toRoutineEntity(routineDTO, userEmail);
        RoutineEntity savedRoutine = routineRepository.save(routineEntity);

        // 루틴이 등록된 기간 동안의 날짜별 상태 저장
        List<String> activeDays = Arrays.asList(routineDTO.getActiveDays().split(", "));
        LocalDate startDate = LocalDate.parse(routineDTO.getStartDate());
        LocalDate endDate = LocalDate.parse(routineDTO.getEndDate());

        // startDate부터 endDate까지 날짜별로 상태를 저장
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 요일이 활성화된 루틴에 포함되는지 확인
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
            if (activeDays.contains(dayOfWeek)) {
                // 날짜에 해당하는 루틴 상태를 생성 및 저장
                RoutineStateEntity statusEntity = new RoutineStateEntity();
                statusEntity.setRoutine(savedRoutine);
                statusEntity.setDate(date.toString());  // 날짜 저장
                statusEntity.setState("pending");  // 초기 상태 설정
                routineStateRepository.save(statusEntity);  // 상태 저장
            }
        }

        return savedRoutine;
    }

    /**
     * 루틴을 업데이트합니다.
     */
    public boolean updateRoutine(Long id, RoutineDTO routineDTO) {
        return routineRepository.findById(id).map(routineEntity -> {
            routineEntity.setTitle(routineDTO.getTitle());
            routineEntity.setStartDate(routineDTO.getStartDate());
            routineEntity.setEndDate(routineDTO.getEndDate());
            routineEntity.setTime(routineDTO.getTime());
            routineEntity.setActiveDays(routineDTO.getActiveDays());
            routineRepository.save(routineEntity);
            return true;
        }).orElse(false);
    }

    /**
     * 특정 날짜에 해당하는 루틴 상태를 업데이트합니다.
     */
    public boolean updateRoutineStateForDate(Long routineId, String date, String state) {
        Optional<RoutineEntity> routineEntityOptional = routineRepository.findById(routineId);
        if (routineEntityOptional.isPresent()) {
            RoutineEntity routineEntity = routineEntityOptional.get();
            Optional<RoutineStateEntity> routineStateEntityOptional =
                    routineStateRepository.findByRoutineAndDate(routineEntity, date);

            // 루틴 상태가 없으면 새로 생성하고, 있으면 업데이트
            RoutineStateEntity routineStatusEntity = routineStateEntityOptional.orElseGet(() -> {
                RoutineStateEntity newStatus = new RoutineStateEntity();
                newStatus.setRoutine(routineEntity);
                newStatus.setDate(date);
                return newStatus;
            });

            routineStatusEntity.setState(state);
            routineStateRepository.save(routineStatusEntity);
            return true;
        }
        return false;
    }

    /**
     * 루틴을 삭제합니다.
     */
    public boolean deleteRoutine(Long id) {
        Optional<RoutineEntity> routineEntityOptional = routineRepository.findById(id);
        if (routineEntityOptional.isPresent()) {
            routineRepository.delete(routineEntityOptional.get());
            return true;
        }
        return false;
    }

    /**
     * 루틴 ID로 루틴을 가져옵니다.
     */
    public RoutineEntity getRoutineById(Long id) {
        return routineRepository.findById(id).orElse(null);
    }
}