package com.heycar.coding.converter;

import com.heycar.coding.model.Listing;
import dto.ListingDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ListingToListingDtoConverter implements Converter<Listing, ListingDto> {

    @Override
    public ListingDto convert(final Listing listing) {
        return ListingDto.builder()
                         .code(listing.getCode())
                         .dealerId(listing.getDealer().getIdentifier())
                         .make(listing.getMake())
                         .model(listing.getModel())
                         .kiloWatt(listing.getKW())
                         .powerInPs(listing.getPowerInPs())
                         .year(listing.getYear())
                         .color(listing.getColor())
                         .price(listing.getPrice())
                         .build();
    }
}
