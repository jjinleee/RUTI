package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.MemoDTO;
import com.hyejin.ruti.entity.MemoEntity;
import com.hyejin.ruti.entity.UserEntity;
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
        UserEntity user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MemoEntity memo = new MemoEntity();
        memo.setMemoWriter(user.getNickname()); // 사용자 닉네임 설정
        memo.setMemoContent(memoDTO.getMemoContent());
        return memoRepository.save(memo);
    }

    public List<MemoDTO> findAllByUser(String userEmail) {
        UserEntity user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<MemoEntity> memoEntityList = memoRepository.findByMemoWriter(user.getNickname());
        List<MemoDTO> memoDTOList = new ArrayList<>();
        for (MemoEntity memoEntity : memoEntityList) {
            memoDTOList.add(MemoDTO.toMemoDTO(memoEntity));
        }
        return memoDTOList;
    }

    public void deleteMemo(Long id) {
        memoRepository.findById(id).ifPresent(memoRepository::delete);
    }

    public List<MemoDTO> searchByContent(String keyword, String userEmail) {
        UserEntity user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<MemoEntity> memoEntityList = memoRepository.findByMemoContentContainingAndMemoWriter(keyword, user.getNickname());
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
