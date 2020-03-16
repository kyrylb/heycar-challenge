package dto;

import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingDto {
    private String code;
    private UUID dealerId;
    private String make;
    private String model;
    private Integer kiloWatt;
    private Integer powerInPs;
    private Integer year;
    private String color;
    private Integer price;
}
