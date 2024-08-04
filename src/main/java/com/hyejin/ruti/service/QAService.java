package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.QADTO;
import com.hyejin.ruti.entity.QAEntity;
import com.hyejin.ruti.repository.QARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QAService {
    @Autowired
    private QARepository qaRepository;

    public boolean submitQA(QADTO qaDTO){
        try{
            QAEntity qaEntity=new QAEntity();
            qaEntity.setCategory(qaDTO.getCategory());
            qaEntity.setContent(qaDTO.getContent());
            qaRepository.save(qaEntity);
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
