package com.heycar.coding.controller;

import com.heycar.coding.converter.CreateUpdateListingDtoToListingConverter;
import com.heycar.coding.converter.ListingToListingDtoConverter;
import com.heycar.coding.model.Dealer;
import com.heycar.coding.model.Listing;
import com.heycar.coding.service.DealerService;
import com.heycar.coding.service.ListingService;
import dto.CreateUpdateListingDto;
import dto.ListingDto;
import dto.ListingsDto;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ListingController {
    public static final String UPLOAD_CSV_BY_DEALER_IDENTIFIER = "/upload_csv/{dealer_id}";
    public static final String UPLOAD_JSON_BY_DEALER_IDENTIFIER = "/vehicle_listings/{dealer_id}";
    public static final String SEARCH_LISTINGS = "/search";

    private final ListingService listingService;
    private final DealerService dealerService;
    private final ListingToListingDtoConverter listingToListingDtoConverter;
    private final CreateUpdateListingDtoToListingConverter createUpdateListingDtoToListingConverter;

    @ApiOperation(value = "uploadCsvListings", notes = "NOTE: Swagger has a bug in generating UI for WebFlux multipart/form-data (FilePart) upload at the moment.\n" +
                                                       "Please use curl command instead, i.g.: curl -X POST \"http://localhost:8080/upload_csv/1a3e01eb-ffd8-4c08-acfe-8165524f1d8b\" -H \"accept: */*\" -H \"Content-Type: multipart/form-data\" -F \"file=@heycar.csv;type=text/csv\"")
    @PostMapping(path = UPLOAD_CSV_BY_DEALER_IDENTIFIER, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadCsvListings(@PathVariable("dealer_id") final UUID dealerIdentifier, @RequestPart("file") FilePart file) {
        final List<Listing> listings = listingService.parseUploadedCsvFile(file);
        createOrUpdateListings(dealerIdentifier, listings.stream());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = UPLOAD_JSON_BY_DEALER_IDENTIFIER, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity uploadJsonListings(@PathVariable("dealer_id") final UUID dealerIdentifier,
                                             @Valid @RequestBody final List<CreateUpdateListingDto> createUpdateListingDtos) {
        createOrUpdateListings(dealerIdentifier, createUpdateListingDtos.stream().map(createUpdateListingDtoToListingConverter::convert));
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = SEARCH_LISTINGS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListingsDto getListings(@RequestParam(value = "make", required = false) final String make,
                                   @RequestParam(value = "model", required = false) final String model,
                                   @RequestParam(value = "year", required = false) final Integer year,
                                   @RequestParam(value = "color", required = false) final String color) {
        // The stream is non-IO based and backed by ArrayList, that's why we don't need to close it explicitly
        // @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html">Stream JavaDocs</a>
        final Stream<Listing> listings = StreamSupport.stream(listingService.findListingsByProperties(make, model, year, color).spliterator(), true);
        final List <ListingDto> listingDtos =  listings.map(listingToListingDtoConverter::convert).collect(Collectors.toList());

        return ListingsDto.builder().total(listingDtos.size()).listings(listingDtos).build();
    }

    private void createOrUpdateListings(final UUID dealerIdentifier, final Stream<Listing> listings) {
        // FIXME: For simplification only !!! Normally we have to have an endpoint for registering dealers and return identifying UUID on successful
        //  registration to use in dealerService.findDealerByIdentifier(...)
        final Dealer dealer = dealerService.getOrCreateDealer(dealerIdentifier);
        // If we didn't do simplification above, we would always call this to find existing listings if dealer is present
        final Map<String, Long> codeToIdMap = listingService.mapListingCodeToId(dealer);

        listingService.createOrUpdateListings(listings.map(listing -> {
            listing.setId(codeToIdMap.get(listing.getCode()));
            listing.setDealer(dealer);
            return listing;
        }).collect(Collectors.toList()));
    }
}
