package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.DateWeather;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {

    /**
     * 그 날의 날씨 값을 가져오는 메소드
     * @param localDate
     * @return DateWeather
     */
    List<DateWeather> findAllByDate(LocalDate localDate);
}
