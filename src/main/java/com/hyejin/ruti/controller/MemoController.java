package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.MemoDTO;
import com.hyejin.ruti.service.MemoService;
import com.hyejin.ruti.entity.MemoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @GetMapping("/add")
    public String saveMemoForm(Model model) {
        model.addAttribute("memoDTO", new MemoDTO());
        return "add Memo";
    }


    @PostMapping("/add")
    public MemoEntity save(@RequestBody MemoDTO memoDTO) {
        System.out.println("memo DTO = " + memoDTO);
        return memoService.saveMemo(memoDTO);
    }
}