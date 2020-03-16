package com.heycar.coding.utils;

import com.heycar.coding.model.Listing;

public class CsvListingParser {

    public static Listing parse(final String csvString) {
        String[] values = csvString.split(",");
        Listing result = null;
        try {
            if (values.length != 6) {
                throw new IllegalArgumentException("Line [" + csvString + "] is not valid");
            }

            String[] makeModel = values[1].split("/");
            if (makeModel.length != 2) {
                throw new IllegalArgumentException("Field [" + values[1] + "] is not valid");
            }

            int powerInPs;
            int kW;
            try {
                powerInPs = Integer.parseInt(values[2]);
                kW = convertHorsePowerToKiloWatt(powerInPs);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Line [" + csvString + "] contains invalid 'power-in-ps' field");
            }

            int year;
            try {
                year = Integer.parseInt(values[3]);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Line [" + csvString + "] contains invalid 'year' field");
            }

            int price;
            try {
                price = Integer.parseInt(values[5]);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Line [" + csvString + "] contains invalid 'price' field");
            }

            result = Listing.builder()
                            .code(values[0])
                            .make(makeModel[0])
                            .model(makeModel[1])
                            .powerInPs(powerInPs)
                            .kW(kW)
                            .year(year)
                            .color(values[4])
                            .price(price)
                            .build();

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();  // just log it somewhere for now
        }

        return result;
    }

    private static int convertHorsePowerToKiloWatt(int powerInPs) {
        return (int) Math.round(powerInPs * 0.7355);   // 1 kW = 0.7355 hp
    }
}
