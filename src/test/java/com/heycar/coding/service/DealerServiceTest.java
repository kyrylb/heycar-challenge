package com.heycar.coding.service;

import com.heycar.coding.model.Dealer;
import com.heycar.coding.persistance.DealerRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealerServiceTest {

    @Mock
    private DealerRepository dealerRepository;

    @InjectMocks
    private DealerService underTest;

    private UUID dealerIdentifier;

    @BeforeEach
    void setUp() {
        dealerIdentifier = UUID.randomUUID();
    }

    @Test
    void shouldGetDealerIfExists() {
        // given
        when(dealerRepository.findByIdentifier(dealerIdentifier)).thenReturn(Optional.ofNullable(Dealer.builder()
                                                                                                       .id(-1L)
                                                                                                       .identifier(dealerIdentifier)
                                                                                                       .createdAt(now())
                                                                                                       .updatedAt(now())
                                                                                                       .build()));
        // when
        var actual = underTest.getOrCreateDealer(dealerIdentifier);
        // then
        assertThat(actual).extracting(Dealer::getIdentifier).isEqualTo(dealerIdentifier);
    }

    @Test
    void shouldCreateDealerIfNotExists() {
        // given
        when(dealerRepository.findByIdentifier(dealerIdentifier)).thenReturn(Optional.empty());
        when(dealerRepository.save(Mockito.any(Dealer.class))).thenReturn(Dealer.builder()
                                                                                .id(-1L)
                                                                                .identifier(dealerIdentifier)
                                                                                .createdAt(now())
                                                                                .updatedAt(now())
                                                                                .build());
        // when
        var actual = underTest.getOrCreateDealer(dealerIdentifier);
        // then
        assertThat(actual).extracting(Dealer::getIdentifier).isEqualTo(dealerIdentifier);
    }
}