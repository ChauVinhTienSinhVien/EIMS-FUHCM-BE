package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponseDTO {

    private Integer id;
    private String roomName;
    private Integer capacity;

}
