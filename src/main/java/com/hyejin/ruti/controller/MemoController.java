package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.MemoDTO;
import com.hyejin.ruti.entity.MemoEntity;
import com.hyejin.ruti.service.MemoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping("/add")
    public MemoEntity save(@RequestBody MemoDTO memoDTO, HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            throw new IllegalStateException("User not logged in");
        }
        return memoService.saveMemo(memoDTO, userEmail);
    }

    @GetMapping("/")
    public List<MemoDTO> findAll(HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            throw new IllegalStateException("User not logged in");
        }
        return memoService.findAllByUser(userEmail);
    }

    @GetMapping("/search")
    public List<MemoDTO> search(@RequestParam("keyword") String keyword, HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            throw new IllegalStateException("User not logged in");
        }
        return memoService.searchByContent(keyword, userEmail);
    }

    @PutMapping("/update/{id}")
    public MemoEntity update(@PathVariable Long id, @RequestBody MemoDTO memoDTO) {
        return memoService.updateMemo(id, memoDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        memoService.deleteMemo(id);
    }
}

