package com.heycar.coding.service;

import com.heycar.coding.model.Dealer;
import com.heycar.coding.model.Listing;
import com.heycar.coding.persistance.ListingRepository;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private ListingService underTest;

    @Test
    void shouldInvokeFindAllWithMakeProperty() {
        // given
        var argument = ArgumentCaptor.forClass(Example.class);

        // when
        underTest.findListingsByProperties("mercedes", null, null, null);
        // then
        verify(listingRepository).findAll(argument.capture());
        assertThat(argument.getValue().getProbe()).extracting("make").isEqualTo("mercedes");
    }

    @Test
    void shouldInvokeFindAllWithModelProperty() {
        // given
        var argument = ArgumentCaptor.forClass(Example.class);

        // when
        underTest.findListingsByProperties(null, "a 180", null, null);
        // then
        verify(listingRepository).findAll(argument.capture());
        assertThat(argument.getValue().getProbe()).extracting("model").isEqualTo("a 180");
    }

    @Test
    void shouldMapListingCodeToId() {
        // given
        when(listingRepository.findAllByDealer(any(Dealer.class))).thenReturn(Arrays.asList(createListing("a", -1L),
                                                                                            createListing("b", -2L)));
        // when
        var codeToId = underTest.mapListingCodeToId(new Dealer());
        assertThat(codeToId).isNotEmpty().hasSize(2);
        assertThat(codeToId).containsKeys("a", "b");
        assertThat(codeToId).containsValues(-1L, -2L);
    }

    private Listing createListing(String code, Long id) {
        return Listing.builder().code(code).id(id).build();
    }
}