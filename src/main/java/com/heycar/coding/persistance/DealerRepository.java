package com.heycar.coding.persistance;

import com.heycar.coding.model.Dealer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealerRepository extends JpaRepository<Dealer, Long> {

    Optional<Dealer> findByIdentifier(final UUID dealerIdentifier);
}

