package org.pitest.voices.g2p.core.expansions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class NumberExpanderTest {

    NumberExpander underTest = new NumberExpander();

    @Test
    void leavesNormalTextAlone() {
        assertThat(underTest.expand("This text. Should. Not. Change%. At all.")).isEqualTo("This text. Should. Not. Change%. At all.");
    }

    @ParameterizedTest
    @CsvSource({
            "6, six",
            "7, seven",
            "8, eight",
            "9, nine",
            "10, ten",
            "11, eleven",
            "12, twelve",
            "13, thirteen",
            "14, fourteen",
            "15, fifteen",
            "16, sixteen",
            "17, seventeen",
            "18, eighteen",
            "19, nineteen",
            "20, twenty",
            "21, twenty one",
            "22, twenty two",
            "23, twenty three",
            "24, twenty four",
            "25, twenty five",
            "26, twenty six",
            "27, twenty seven",
            "28, twenty eight",
            "29, twenty nine",
            "30, thirty",
            "31, thirty one",
            "40, forty",
            "50, fifty",
            "60, sixty",
            "70, seventy",
            "80, eighty",
            "90, ninety",
            "100, one hundred",
            "1003, one thousand three",
            "123, one hundred twenty three",
            "987654321, nine hundred eighty seven million six hundred fifty four thousand three hundred twenty one"

    })
    void expandsSimpleNumbers(String text, String expected) {
        assertMatches(text, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "1000000000000, 1000000000000",
            "1000000000000.00, 1000000000000.00",
            "1000000000000th, 1000000000000th",
    })
    void noErrorForIntegerOverflow(String text, String expected) {
        assertMatches(text, expected);
    }


    @ParameterizedTest
    @CsvSource({
            "£3, three pounds",
            "£10, ten pounds",
            "£12.27, twelve pounds and twenty seven pence",
            "£1234.56, one thousand two hundred thirty four pounds and fifty six pence"
    })
    void expandsCurrency(String text, String expected) {
        assertMatches(text, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "1901, nineteen oh one",
            "1905, nineteen oh five",
            "2000, twenty hundred",
            "2005, twenty oh five",
            "2015, twenty fifteen",
            "1800, eighteen oh zero" // really?
    })
    void expandsYears(String year, String expected) {
        assertMatches(year, expected);
    }


    @ParameterizedTest
    @CsvSource({
            "12:34 pm, twelve thirty four p m",
            "1:30 AM, one thirty a m",
            "13:45, one forty five"
    })
    void expandsTime(String text, String expected) {
        assertMatches(text, expected);
    }


    @ParameterizedTest
    @CsvSource({
            "1st, first",
            "2nd, second",
            "3rd, third",
            "4th, fourth",
            "5th, fifth",
            "6th, sixth",
            "7th, seventh",
            "8th, eighth",
            "9th, ninth",
            "10th, tenth",
            "11th, eleventh",
            "12th, twelfth",
            "13th, thirteenth",
            "14th, fourteenth",
            "15th, fifteenth",
            "16th, sixteenth",
            "17th, seventeenth",
            "18th, eighteenth",
            "19th, nineteenth",
            "20th, twentieth",
            "21st, twenty first",
            "22nd, twenty second",
            "23rd, twenty third",
            "24th, twenty fourth",
            "25th, twenty fifth",
            "26th, twenty sixth",
            "27th, twenty seventh",
            "28th, twenty eighth",
            "29th, twenty ninth",
            "30th, thirtieth",
            "40th, fortieth",
            "50th, fiftieth",
            "60th, sixtieth",
            "66th, sixty sixth",
            "70th, seventieth",
            "80th, eightieth",
            "90th, ninetieth",
            "100th, hundredth",
            "111th, one hundred eleventh",
            "122nd, one hundred twenty second",
            "11111th, eleven thousand one hundred eleventh",

    })
    void expandsOrdinals(String text, String expected) {
        assertMatches(text, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "1.23, one point two three",
            "17.98, seventeen point nine eight",
            "11.45, eleven point four five",
            "12.67 times, twelve point six seven times"
    })
    void expandsDecimals(String text, String expected) {
        assertMatches(text, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "50%, fifty percent",
            "50.75%, fifty point seven five percent",
            "12.98%, twelve point nine eight percent",
            "50% of the time, fifty percent of the time"
    })
    void expandsPercentages(String text, String expected) {
        assertMatches(text, expected);
    }


    private void assertMatches(String text, String expected) {
        assertThat(underTest.expand(text).trim()).isEqualTo(expected);
    }

}