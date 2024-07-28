package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.MemoDTO;
import com.hyejin.ruti.entity.MemoEntity;
import com.hyejin.ruti.service.MemoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping("/add")
    public ResponseEntity<?> save(@RequestBody MemoDTO memoDTO, HttpSession session) {
        try {
            String userEmail = (String) session.getAttribute("loginEmail");
            if (userEmail == null) {
                return ResponseEntity.status(401).body("User not logged in");
            }
            MemoEntity memoEntity = memoService.saveMemo(memoDTO, userEmail);
            return ResponseEntity.ok(memoEntity);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> findAll(HttpSession session) {
        try {
            String userEmail = (String) session.getAttribute("loginEmail");
            if (userEmail == null) {
                return ResponseEntity.status(401).body("User not logged in");
            }
            List<MemoDTO> memos = memoService.findAllByUser(userEmail);
            return ResponseEntity.ok(memos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("keyword") String keyword, HttpSession session) {
        try {
            String userEmail = (String) session.getAttribute("loginEmail");
            if (userEmail == null) {
                return ResponseEntity.status(401).body("User not logged in");
            }
            List<MemoDTO> memos = memoService.searchByContent(keyword, userEmail);
            return ResponseEntity.ok(memos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody MemoDTO memoDTO) {
        try {
            MemoEntity updatedMemo = memoService.updateMemo(id, memoDTO);
            return ResponseEntity.ok(updatedMemo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Memo not found with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMemo(@PathVariable("id") Long id) {
        try {
            memoService.deleteMemo(id);
            return ResponseEntity.ok("Memo deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Memo not found with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }
}
