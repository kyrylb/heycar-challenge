package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
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
public class CreateUpdateListingDto {
    @NotBlank
    private String code;
    @NotBlank
    private String make;
    @NotBlank
    private String model;
    @Positive
    @JsonProperty("kW")
    private Integer kiloWatt;
    @Positive
    private Integer year;
    @NotBlank
    private String color;
    @Positive
    private Integer price;
}
