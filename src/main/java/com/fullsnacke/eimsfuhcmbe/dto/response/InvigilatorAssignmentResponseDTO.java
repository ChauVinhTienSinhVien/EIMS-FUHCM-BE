package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvigilatorAssignmentResponseDTO {

    int assignmentId;

    String status;

    String fuId;

    String firstName;

    String lastName;

    @Email
    String email;

    String phoneNumber;

    String department;

    Boolean gender;

    Instant createdAt;
}
