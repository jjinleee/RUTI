package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.MemoDTO;
import com.hyejin.ruti.entity.MemoEntity;
import com.hyejin.ruti.repository.MemoRepository;
import com.hyejin.ruti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    public MemoEntity saveMemo(MemoDTO memoDTO, String userEmail) {
        userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 메모 내용이 비어 있는지 확인
        if (memoDTO.getMemoContent() == null || memoDTO.getMemoContent().isEmpty()) {
            throw new IllegalArgumentException("Memo content is required");
        }

        MemoEntity memo = new MemoEntity();
        memo.setUserEmail(userEmail); // userEmail을 직접 설정
        memo.setMemoContent(memoDTO.getMemoContent());
        return memoRepository.save(memo);
    }

    public List<MemoDTO> findAllByUser(String userEmail) {
        // userEmail을 사용해 메모를 조회
        List<MemoEntity> memoEntityList = memoRepository.findByUserEmail(userEmail);
        List<MemoDTO> memoDTOList = new ArrayList<>();
        for (MemoEntity memoEntity : memoEntityList) {
            memoDTOList.add(MemoDTO.toMemoDTO(memoEntity));
        }
        return memoDTOList;
    }

    public void deleteMemo(Long id) {
        memoRepository.deleteById(id);
    }

    public List<MemoDTO> searchByContent(String keyword, String userEmail) {
        // userEmail을 사용해 메모를 검색
        List<MemoEntity> memoEntityList = memoRepository.findByMemoContentContainingAndUserEmail(keyword, userEmail);
        List<MemoDTO> memoDTOList = new ArrayList<>();
        for (MemoEntity memoEntity : memoEntityList) {
            memoDTOList.add(MemoDTO.toMemoDTO(memoEntity));
        }
        return memoDTOList;
    }

    public MemoEntity updateMemo(Long id, MemoDTO memoDTO) {
        return memoRepository.findById(id).map(memo -> {
            memo.setMemoContent(memoDTO.getMemoContent());
            return memoRepository.save(memo);
        }).orElseThrow(() -> new IllegalArgumentException("Memo not found with id: " + id));
    }
}