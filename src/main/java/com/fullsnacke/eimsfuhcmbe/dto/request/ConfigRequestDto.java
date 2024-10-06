package com.fullsnacke.eimsfuhcmbe.dto.request;
import lombok.*;

@Getter
@Setter
public class ConfigRequestDto {
    private Integer id;

    private String configType;

    private String value;

    private String unit;

    private Integer semester;
}
