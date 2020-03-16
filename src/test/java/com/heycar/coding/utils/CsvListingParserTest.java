package com.heycar.coding.utils;

import com.heycar.coding.model.Listing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CsvListingParserTest {

    @Test
    void shouldParseCorrectCsvString() {
        // given
        var csvString = "1,mercedes/a 180,123,2014,black,15950";
        // when
        var actual = CsvListingParser.parse(csvString);
        // then
        assertThat(actual).isEqualTo(Listing.builder()
                                            .code("1")
                                            .make("mercedes")
                                            .model("a 180")
                                            .powerInPs(123)
                                            .kW(90)
                                            .year(2014)
                                            .color("black")
                                            .price(15950)
                                            .build());
    }

    @Test
    void shouldSkipHeaderCsvString() {
        // given
        var csvString = "code,make/model,power-in-ps,year,color,price";
        // when & then
        assertThat(CsvListingParser.parse(csvString)).isNull();
    }

    @Test
    void shouldSkipIncompleteCsvString() {
        // given
        var csvString = "4,skoda/octavia,86,2018,16990";
        // when & then
        assertThat(CsvListingParser.parse(csvString)).isNull();
    }

    @Test
    void shouldSkipTooMuchFieldCsvString() {
        // given
        var csvString = "1,1,mercedes/a 180,123,2014,black,15950";
        // when & then
        assertThat(CsvListingParser.parse(csvString)).isNull();
    }

    @Test
    void shouldSkipIncorrectMakeModelFieldCsvString() {
        // given
        var csvString = "1,mercedes,a 180,123,2014,black,15950";
        // when & then
        assertThat(CsvListingParser.parse(csvString)).isNull();
    }
}