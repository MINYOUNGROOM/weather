package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {

        // 1. open weather map 에서 데이터 받아오기
        String weatherData = getWeatherString();

        // 2.  받아온 날씨 데이터 파싱하기
        Map<String, Object> parserWeather = parseWeather(weatherData);

        // 3. db 저장하기
        Diary nowDiary = new Diary();
        nowDiary.setWeather(parserWeather.get("main").toString());
        nowDiary.setIcon(parserWeather.get("icon").toString());
        nowDiary.setTemperature((Double) parserWeather.get("temp"));
        nowDiary.setText(text);
        nowDiary.setDate(date);
        diaryRepository.save(nowDiary);
    }


    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            URL url = new URL(apiUrl);

            // Http 요청을 받을 수 있는 커넥션을 오픈
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 응답코드를 받아서
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            // 응답 코드를 BufferedReader 안에 넣어둠
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }


    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (
                ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject subData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", subData.get("main"));
        resultMap.put("icon", subData.get("icon"));

        return resultMap;
    }


    // 일기 조회
    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    // 기간 조회
    @Transactional(readOnly = true)
    public List<Diary> readBetweenDiary(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate,endDate);
    }

    // 삭제
    public void deleteByDate(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    // 수정
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.findFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }


}
