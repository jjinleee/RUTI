package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.QADTO;
import com.hyejin.ruti.service.QAService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/qa")
@RequiredArgsConstructor
public class QAController {

    @Autowired
    private final QAService qaService;

    @PostMapping
    public ResponseEntity<?> submitQA(@RequestBody QADTO qaDTO){
        boolean isSubmitted= qaService.submitQA(qaDTO);

        if(isSubmitted){
            return ResponseEntity.ok(java.util.Collections.singletonMap("success",true));
        } else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("success",false));
        }
    }
}
