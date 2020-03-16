package com.heycar.coding.controller;

import com.heycar.coding.model.Dealer;
import com.heycar.coding.model.Listing;
import com.heycar.coding.persistance.DealerRepository;
import com.heycar.coding.persistance.ListingRepository;
import dto.CreateUpdateListingDto;
import dto.ListingDto;
import dto.ListingsDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import static com.heycar.coding.controller.ListingController.SEARCH_LISTINGS;
import static com.heycar.coding.controller.ListingController.UPLOAD_CSV_BY_DEALER_IDENTIFIER;
import static com.heycar.coding.controller.ListingController.UPLOAD_JSON_BY_DEALER_IDENTIFIER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebTestClient
class ListingControllerIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private DealerRepository dealerRepository;

    private Dealer dealer;

    @BeforeEach
    public void setUp() {
        listingRepository.deleteAll();
        dealerRepository.deleteAll();
        dealer = dealerRepository.save(Dealer.builder().identifier(UUID.randomUUID()).build());
    }

    @ParameterizedTest
    @CsvSource({
            "make, mercedes",
            "color, black",
            "model, a 180",
            "year, 2014"
    })
    void shouldSearchListingsByParameters(final String parameterName, final String parameterValue) {
        // given
        listingRepository.saveAll(Arrays.asList(Listing.builder()
                                                       .dealer(dealer)
                                                       .code("5")
                                                       .make("mercedes")
                                                       .model("a 180")
                                                       .powerInPs(124)
                                                       .kW(91)
                                                       .year(2014)
                                                       .color("black")
                                                       .price(15950)
                                                       .build(),
                                                Listing.builder()
                                                       .dealer(dealer)
                                                       .code("a")
                                                       .make("renault")
                                                       .model("megane")
                                                       .powerInPs(0)
                                                       .kW(132)
                                                       .year(2015)
                                                       .color("red")
                                                       .price(13990)
                                                       .build()));
        final ListingsDto expectedResponse = ListingsDto.builder()
                                                        .total(1)
                                                        .listings(Collections.singletonList(ListingDto.builder()
                                                                                                      .code("5")
                                                                                                      .dealerId(dealer.getIdentifier())
                                                                                                      .make("mercedes")
                                                                                                      .model("a 180")
                                                                                                      .powerInPs(124)
                                                                                                      .kiloWatt(91)
                                                                                                      .year(2014)
                                                                                                      .color("black")
                                                                                                      .price(15950)
                                                                                                      .build()))
                                                        .build();

        // when
        final WebTestClient.ResponseSpec responseSpec = client.get()
                                                              .uri(uriBuilder -> uriBuilder.path(SEARCH_LISTINGS)
                                                                                           .queryParam(parameterName, parameterValue)
                                                                                           .build())
                                                              .accept(MediaType.APPLICATION_JSON)
                                                              .exchange();
        // then
        responseSpec.expectStatus().is2xxSuccessful().expectBody(ListingsDto.class).isEqualTo(expectedResponse);
    }

    @Test
    void shouldCreateListingsByJson() {
        // when
        client.post()
                                 .uri(uriBuilder -> uriBuilder.path(UPLOAD_JSON_BY_DEALER_IDENTIFIER)
                                                              .build(dealer.getIdentifier()))
                                 .body(BodyInserters.fromValue(Collections.singletonList(createListingDtoRequest("5", "black"))))
                                 .exchange()
                                 .expectStatus()
                                 .is2xxSuccessful()
                                 .expectBody()
                                 .isEmpty();
        // then
        checkListingExists(1, ListingDto.builder()
                                     .code("5")
                                     .dealerId(dealer.getIdentifier())
                                     .make("mercedes")
                                     .model("a 180")
                                     .powerInPs(124)
                                     .kiloWatt(91)
                                     .year(2014)
                                     .color("black")
                                     .price(15950)
                                     .build());
    }

    @Test
    void shouldUpdateListingsByJson() {
        // given
        final String listingCode = "5";
        Dealer anotherDealer = dealerRepository.save(Dealer.builder().identifier(UUID.randomUUID()).build());
        listingRepository.saveAll(Arrays.asList(Listing.builder()
                                                       .dealer(dealer)
                                                       .code(listingCode)
                                                       .make("mercedes")
                                                       .model("a 180")
                                                       .powerInPs(124)
                                                       .kW(91)
                                                       .year(2014)
                                                       .color("black")
                                                       .price(15950)
                                                       .build(),
                                                Listing.builder()
                                                       .dealer(anotherDealer)
                                                       .code("a")
                                                       .make("renault")
                                                       .model("megane")
                                                       .powerInPs(0)
                                                       .kW(132)
                                                       .year(2015)
                                                       .color("red")
                                                       .price(13990)
                                                       .build()));

        // when
        client.post()
              .uri(uriBuilder -> uriBuilder.path(UPLOAD_JSON_BY_DEALER_IDENTIFIER).build(dealer.getIdentifier()))
              .body(BodyInserters.fromValue(Collections.singletonList(createListingDtoRequest(listingCode, "pale-blue"))))
              .exchange()
              .expectStatus()
              .is2xxSuccessful()
              .expectBody()
              .isEmpty();

        // then
        checkListingExists(2,
                           ListingDto.builder()
                                     .code(listingCode)
                                     .dealerId(dealer.getIdentifier())
                                     .make("mercedes")
                                     .model("a 180")
                                     .powerInPs(124)
                                     .kiloWatt(91)
                                     .year(2014)
                                     .color("pale-blue")
                                     .price(15950)
                                     .build());
    }

    @Test
    void shouldCreateListingsByCsv() {
        // when
        var bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new ClassPathResource("listings-test.csv"));
        client.post()
              .uri(uriBuilder -> uriBuilder.path(UPLOAD_CSV_BY_DEALER_IDENTIFIER)
                                           .build(dealer.getIdentifier()))
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
              .exchange()
              .expectStatus()
              .is2xxSuccessful()
              .expectBody()
              .isEmpty();

        // then
        checkListingExists(1, ListingDto.builder()
                                     .code("1")
                                     .dealerId(dealer.getIdentifier())
                                     .make("mercedes")
                                     .model("a 180")
                                     .powerInPs(123)
                                     .kiloWatt(90)
                                     .year(2014)
                                     .color("black")
                                     .price(15950)
                                     .build());
    }

    @Test
    void shouldUpdateListingsByCsv() {
        // given
        final String listingCode = "1";
        Dealer anotherDealer = dealerRepository.save(Dealer.builder().identifier(UUID.randomUUID()).build());
        listingRepository.saveAll(Arrays.asList(Listing.builder()
                                                       .dealer(dealer)
                                                       .code(listingCode)
                                                       .make("mercedes")
                                                       .model("a 180")
                                                       .powerInPs(123)
                                                       .kW(90)
                                                       .year(2014)
                                                       .color("pale-blue")
                                                       .price(15950)
                                                       .build(),
                                                Listing.builder()
                                                       .dealer(anotherDealer)
                                                       .code("a")
                                                       .make("renault")
                                                       .model("megane")
                                                       .powerInPs(0)
                                                       .kW(132)
                                                       .year(2015)
                                                       .color("red")
                                                       .price(13990)
                                                       .build()));

        // when
        var bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new ClassPathResource("listings-test.csv"));
        client.post()
              .uri(uriBuilder -> uriBuilder.path(UPLOAD_CSV_BY_DEALER_IDENTIFIER)
                                           .build(dealer.getIdentifier()))
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
              .exchange()
              .expectStatus()
              .is2xxSuccessful()
              .expectBody()
              .isEmpty();

        // then
        checkListingExists(2,
                           ListingDto.builder()
                                     .code("1")
                                     .dealerId(dealer.getIdentifier())
                                     .make("mercedes")
                                     .model("a 180")
                                     .powerInPs(123)
                                     .kiloWatt(90)
                                     .year(2014)
                                     .color("black")
                                     .price(15950)
                                     .build());
    }

    private CreateUpdateListingDto createListingDtoRequest(final String code, final String color){
        return CreateUpdateListingDto.builder()
                                     .code(code)
                                     .make("mercedes")
                                     .model("a 180")
                                     .kiloWatt(91)
                                     .year(2014)
                                     .color(color)
                                     .price(15950)
                                     .build();
    }

    private void checkListingExists(final int total, final ListingDto listingDto){
        final WebTestClient.ResponseSpec responseSpec = client.get()
                                                              .uri(uriBuilder -> uriBuilder.path(SEARCH_LISTINGS).build())
                                                              .accept(MediaType.APPLICATION_JSON)
                                                              .exchange();
        responseSpec.expectStatus().is2xxSuccessful().expectBody(ListingsDto.class).consumeWith(listingsDto -> {
            assertThat(listingsDto.getResponseBody().getTotal()).isEqualTo(total);
            assertThat(listingsDto.getResponseBody().getListings()).contains(listingDto);
        });
    }
}