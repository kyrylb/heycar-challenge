package com.heycar.coding.converter;

import com.heycar.coding.model.Listing;
import dto.CreateUpdateListingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CreateUpdateListingDtoToListingConverterTest {

    private CreateUpdateListingDtoToListingConverter underTest;

    @BeforeEach
    void setUp() {
        underTest = new CreateUpdateListingDtoToListingConverter();
    }

    @Test
    void shouldConvert() {
        var actual = underTest.convert(CreateUpdateListingDto.builder()
                                                             .code("a")
                                                             .make("renault")
                                                             .model("megane")
                                                             .kiloWatt(132)
                                                             .year(2014)
                                                             .color("red")
                                                             .price(13990)
                                                             .build());
        var expected = Listing.builder()
                              .code("a")
                              .make("renault")
                              .model("megane")
                              .powerInPs(179)
                              .kW(132)
                              .year(2014)
                              .color("red")
                              .price(13990)
                              .build();

        assertThat(actual).isEqualTo(expected);
    }
}