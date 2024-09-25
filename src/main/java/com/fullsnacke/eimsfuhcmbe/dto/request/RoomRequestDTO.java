package com.fullsnacke.eimsfuhcmbe.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomRequestDTO {

    @NotNull(message = "Room name is required")
    private String roomName;

    @NotNull(message = "Capacity is required")
    private Integer capacity;

}
