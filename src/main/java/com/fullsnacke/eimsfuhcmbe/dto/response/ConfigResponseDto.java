package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;

@Getter
@Setter
public class ConfigResponseDto {
    private Integer id;

    private String configType;

    private String value;

    private String unit;

    private String semester;
}