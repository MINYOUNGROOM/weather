package zerobase.weather;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WeatherApplicationTests {

	@Test
	void equalsTest() {
		Assertions.assertEquals(1,1);
	}


	@Test
	void nullTest() {
		Assertions.assertNull(null);
		//given
		//when
		//then
	}

		@Test
		void trueTest(){
			Assertions.assertTrue(1==1);
		    //given
		    //when
		    //then
		}
	}

