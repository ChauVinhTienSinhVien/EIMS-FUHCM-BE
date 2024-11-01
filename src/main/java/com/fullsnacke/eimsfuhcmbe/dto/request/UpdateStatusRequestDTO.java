package com.fullsnacke.eimsfuhcmbe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UpdateStatusRequestDTO {
    private int requestId;
    private String note;
    String status;
}
