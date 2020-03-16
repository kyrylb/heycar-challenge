package com.heycar.coding.service;

import com.heycar.coding.model.Dealer;
import com.heycar.coding.model.Listing;
import com.heycar.coding.persistance.DealerRepository;
import com.heycar.coding.persistance.ListingRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class DealerService {

    private final DealerRepository dealerRepository;

    public Optional<Dealer> findDealerByIdentifier(final UUID dealerIdentifier) {
        return dealerRepository.findByIdentifier(dealerIdentifier);
    }

    public Dealer createDealer(final UUID dealerIdentifier) {
        return dealerRepository.save(Dealer.builder().identifier(dealerIdentifier).build());
    }

    /*
        FIXME: This method should not exist. We create a Dealer here if it doesn't exist for simplification only !!!
    */
    public Dealer getOrCreateDealer(final UUID dealerIdentifier) {
        return findDealerByIdentifier(dealerIdentifier).orElseGet(() -> createDealer(dealerIdentifier));
    }
}
