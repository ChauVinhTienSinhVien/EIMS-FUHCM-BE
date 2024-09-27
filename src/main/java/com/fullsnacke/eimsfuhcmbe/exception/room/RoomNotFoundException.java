package com.fullsnacke.eimsfuhcmbe.exception.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomNotFoundException extends RuntimeException {

    private String message;

}
