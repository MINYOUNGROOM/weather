package zerobase.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Diary API", description = "일기 관련 API")
public class DiaryController {

    @Autowired
    private final DiaryService diaryService;

    // 다이어리 작성
    @Operation(summary = "날씨 일기 작성 API",description = "날씨에 따른 일기 작성이 가능합니다.")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

    // 다이어리 조회
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    // 다이어리 기간 조회
    @GetMapping("/read/diaries")
    List<Diary> readBetweenDiary(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return diaryService.readBetweenDiary(startDate, endDate);
    }

    // 다이어리 삭제
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        diaryService.deleteByDate(date);
    }

    // 다이어리 수정
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text) {
        diaryService.updateDiary(date,text);
    }
}
