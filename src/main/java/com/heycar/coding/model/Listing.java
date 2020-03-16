package com.heycar.coding.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "listings")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="dealer_id")
    private Dealer dealer;

    @Column(name = "code")
    private String code;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Column(name = "power_in_ps")
    private Integer powerInPs;

    @Column(name = "kw")
    private Integer kW;

    @Column(name = "year_manufactured")
    private Integer year;

    @Column(name = "color")
    private String color;

    @Column(name = "price")
    private Integer price;   // don't need BigDecimal here, as we keep the price in highest common unit, i.e. no cents

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
