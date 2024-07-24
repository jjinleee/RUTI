package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.MemoDTO;
import com.hyejin.ruti.entity.MemoEntity;
import com.hyejin.ruti.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;

    private MemoEntity convertToEntity(MemoDTO memoDTO) {
        MemoEntity memo = new MemoEntity();
        memo.setMemoWriter(memoDTO.getMemoWriter());
        memo.setMemoContent(memoDTO.getMemoContent());
        return memo;
    }

    public MemoEntity saveMemo(MemoDTO memoDTO) {
        MemoEntity memo = convertToEntity(memoDTO);
        return memoRepository.save(memo);
    }
}