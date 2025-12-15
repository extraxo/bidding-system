package BiddingSystem.BiddingSystemRepo;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Calculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class TestCalculator {

    @Test
    @DisplayName("1 + 1 = 2")
    void addsTwoNumbers() {
        Calculator calculator = new Calculator();
        assertEquals(2, calculator.add(1, 1), "1 + 1 should equal 2");
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource(textBlock = """
        0, 1, 1
        1, 2, 3
        49, 51, 100
        1, 100, 101
        """)
    void add(Integer first, Integer second, Integer expectedResult) {
        Calculator calculator = new Calculator();
        assertEquals(expectedResult, calculator.add(first != null ? first : 0, second != null ? second : 0),
                () -> first + " + " + second + " should equal " + expectedResult);
    }
}