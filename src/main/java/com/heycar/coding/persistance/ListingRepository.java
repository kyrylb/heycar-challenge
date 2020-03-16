package com.heycar.coding.persistance;

import com.heycar.coding.model.Dealer;
import com.heycar.coding.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    Iterable<Listing> findAllByDealer(final Dealer dealer);

}

