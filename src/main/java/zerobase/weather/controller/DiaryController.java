package zerobase.weather.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
public class DiaryController {

    @Autowired
    private final DiaryService diaryService;


    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,@RequestBody String text) {
        diaryService.createDiary(date, text);

    }
}
