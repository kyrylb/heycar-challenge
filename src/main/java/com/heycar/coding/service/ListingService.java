package com.heycar.coding.service;

import com.heycar.coding.model.Dealer;
import com.heycar.coding.model.Listing;
import com.heycar.coding.persistance.ListingRepository;
import com.heycar.coding.utils.CsvListingParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Example;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;

    public Iterable<Listing> findListingsByProperties(final String make, final String model, final Integer year, final String color) {
        Listing example = Listing.builder()
                                 .make(make)
                                 .model(model)
                                 .year(year)
                                 .color(color)
                                 .build();
        return listingRepository.findAll(Example.of(example));
    }

    public List<Listing> createOrUpdateListings(final List<Listing> listings) {
        return listingRepository.saveAll(listings);
    }

    public Map<String, Long> mapListingCodeToId(final Dealer dealer) {
        final Map<String, Long> codeToIdMap = new HashMap<>();
        for (Listing listing : listingRepository.findAllByDealer(dealer)) {
            codeToIdMap.put(listing.getCode(), listing.getId());
        }
        return codeToIdMap;
    }

    public List<Listing> parseUploadedCsvFile(final FilePart file) {
        final List<Listing> listings = new ArrayList<>();
        try (PipedOutputStream osPipe = new PipedOutputStream(); BufferedReader br = new BufferedReader(new InputStreamReader(new PipedInputStream(osPipe)))) {
            DataBufferUtils.write(file.content(), osPipe).subscribe(DataBufferUtils.releaseConsumer());

            br.readLine();  // skip header line
            while (br.ready()) {
                Listing listing = CsvListingParser.parse(br.readLine());
                if (listing != null) {
                    listings.add(listing);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listings;
    }

}
