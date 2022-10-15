package com.astrodust.testing;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UnitTestingApplicationTests {

    Calculator calculator = new Calculator();

    @Test
    void addTwoNumbers() {
        // given
        int x = 10, y = 20;

        // when
        int res = calculator.add(x, y);

        // then
        int expected = 30;
        assertThat(res).isEqualTo(expected);
    }

    class Calculator{
        int add(int x, int y){
            return x+y;
        }
    }

}
