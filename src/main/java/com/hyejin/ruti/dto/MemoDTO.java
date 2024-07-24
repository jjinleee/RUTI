package com.hyejin.ruti.dto;

import com.hyejin.ruti.entity.MemoEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MemoDTO {
    private Long id;
    private String memoContent;

    public static MemoDTO toMemoDTO(MemoEntity memoEntity) {
        MemoDTO memoDTO = new MemoDTO();
        memoDTO.setId(memoEntity.getId());
        memoDTO.setMemoContent(memoEntity.getMemoContent());
        return memoDTO;
    }
}
