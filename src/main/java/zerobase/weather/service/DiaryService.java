package zerobase.weather.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;


    public void createDiary(LocalDate date, String text) {
        // 1. open weather map 에서 데이터 받아오기
        getWeatherString();

        // 2.  받아온 날씨 데이터 파싱하기


        // 3. db 저장하기

    }

    private String getWeatherString(){
       String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        System.out.println(apiUrl);
        return "";
    }
}
