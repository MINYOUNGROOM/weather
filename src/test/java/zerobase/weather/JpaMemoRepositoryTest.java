package zerobase.weather;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JpaMemoRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        //given
        Memo newMemo = new Memo(1, "this is jpa memo");

        //when
        jpaMemoRepository.save(newMemo);

        //then
        List<Memo> memoList = jpaMemoRepository.findAll();
        Assertions.assertTrue(memoList.size() > 0);
    }


//    @Test
//    void findByIdTest(){
//        //given
//        Memo newMemo = new Memo(11, "jpa");
//
//        //when
//        jpaMemoRepository.save(newMemo);
//        //then
//        Optional<Memo> isPresent = jpaMemoRepository.findById(11);
//        Assertions.assertEquals(isPresent.get().getText(), "jpa");
//    }
}
