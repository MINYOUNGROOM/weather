package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
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
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }
    // 날씨 정보 저장 스케줄링
    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // 매일 매달 새벽 1시 0분 0초 마다 동작
    public void saveWeatherDate() {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");
        // db에서 이미 저장된 캐싱 해놓은 날씨 데이터 가져오기 (OPEN API 에서 가져오기 or DB에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);

        // 3. db 저장하기
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
        logger.info("end to create diary");
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);

        if (dateWeatherListFromDB.isEmpty()) {
            // 만약 디비에 캐싱된 날씨 정보가 없다면 !!
            // 새로 api에서 정보 가져오기
            // 정책 상,, , 현재 날씨를 가져오도록 하거나 ,, 날씨 없이 일기를 쓰도록 ,,
            return getWeatherFromApi();
        }else {
            return dateWeatherListFromDB.get(0);
        }
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

    // 스케줄링 날씨 데이터를 가져오는 함수
    private DateWeather getWeatherFromApi() {

        // 1. open weather map 에서 데이터 받아오기
        String weatherData = getWeatherString();
        // 2.  받아온 날씨 데이터 파싱하기
        Map<String, Object> parserWeather = parseWeather(weatherData);

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now()); // 현재 시점의 날짜를 대입
        dateWeather.setWeather(parserWeather.get("main").toString());
        dateWeather.setIcon(parserWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parserWeather.get("temp"));
        return dateWeather;

    }

    // 일기 조회
    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
//        if(date.isAfter(LocalDate.ofYearDay(3050, 1))){
//            throw new InvalidDate();
//        }
        logger.debug("read to diary");
        return diaryRepository.findAllByDate(date);
    }

    // 기간 조회
    @Transactional(readOnly = true)
    public List<Diary> readBetweenDiary(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
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
