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
    public List<RoutineDTO> getRoutinesForToday(String userEmail) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayStr = today.format(formatter); // 현재 날짜를 "yyyy-MM-dd" 형식의 문자열로 변환

        System.out.println("오늘 날짜: " + todayStr);
        System.out.println("사용자 이메일: " + userEmail);

        List<RoutineEntity> routineEntities = routineRepository.findAllByUserEmailAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userEmail, todayStr, todayStr);

        return routineEntities.stream()
                .map(RoutineDTO::fromEntity)
                .collect(Collectors.toList());
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

    @Transactional
    public boolean updateRoutine(Long id, RoutineDTO routineDTO) {
        return routineRepository.findById(id).map(routineEntity -> {
            // 루틴 엔티티 업데이트
            routineEntity.setTitle(routineDTO.getTitle());
            routineEntity.setStartDate(routineDTO.getStartDate());
            routineEntity.setEndDate(routineDTO.getEndDate());
            routineEntity.setTime(routineDTO.getTime());
            routineEntity.setActiveDays(routineDTO.getActiveDays());
            routineRepository.save(routineEntity);

            // 새로운 날짜 범위와 활성 요일을 설정
            LocalDate newStartDate = LocalDate.parse(routineDTO.getStartDate());
            LocalDate newEndDate = LocalDate.parse(routineDTO.getEndDate());
            List<String> newActiveDays = Arrays.asList(routineDTO.getActiveDays().split(", "));

            // 범위 밖의 상태 삭제
            routineStateRepository.deleteByRoutineAndOutsideDateRange(
                    routineEntity,
                    newStartDate.toString(),
                    newEndDate.toString(),
                    newActiveDays
            );

            // 새로운 범위에 맞는 상태 정보 추가
            for (LocalDate date = newStartDate; !date.isAfter(newEndDate); date = date.plusDays(1)) {
                String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

                if (newActiveDays.contains(dayOfWeek)) {
                    Optional<RoutineStateEntity> existingStateOpt = routineStateRepository.findByRoutineAndDate(routineEntity, date.toString());

                    // 기존 상태가 없으면 새로운 상태 추가
                    if (!existingStateOpt.isPresent()) {
                        RoutineStateEntity newState = new RoutineStateEntity();
                        newState.setRoutine(routineEntity);
                        newState.setDate(date.toString());
                        newState.setState("pending");
                        routineStateRepository.save(newState);
                    }
                }
            }

            return true;
        }).orElse(false);
    }


    public boolean updateRoutineStateForDate(Long routineId, String date, String state) {
        System.out.println("Updating state for Routine ID: " + routineId + ", Date: " + date + ", New State: " + state);

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


    public boolean deleteRoutine(Long id) {
        Optional<RoutineEntity> routineEntityOptional = routineRepository.findById(id);
        if (routineEntityOptional.isPresent()) {
            routineRepository.delete(routineEntityOptional.get());
            return true;
        }
        return false;
    }


    public RoutineEntity getRoutineById(Long id) {
        return routineRepository.findById(id).orElse(null);
    }
}