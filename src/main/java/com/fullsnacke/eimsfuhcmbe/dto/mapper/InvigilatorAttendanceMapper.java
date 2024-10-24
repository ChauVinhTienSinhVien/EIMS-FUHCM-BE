package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.InvigilatorAttendanceRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.InvigilatorAttendanceResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.InvigilatorAttendance;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.enums.ExamSlotStatus;
import com.fullsnacke.eimsfuhcmbe.enums.InvigilatorAttendanceStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface InvigilatorAttendanceMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToInt")
    InvigilatorAttendance toEntity(InvigilatorAttendanceRequestDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "checkIn", source = "checkIn")
    @Mapping(target = "checkOut", source = "checkOut")
    @Mapping(target = "status", source = "status", qualifiedByName = "intToStatus")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "userToInt")
    @Mapping(target = "startAt", source = "invigilatorAssignment.invigilatorRegistration.examSlot.startAt", qualifiedByName = "zonedDateTimeToInstant")
    @Mapping(target = "endAt", source = "invigilatorAssignment.invigilatorRegistration.examSlot.endAt", qualifiedByName = "zonedDateTimeToInstant")
    @Mapping(target = "examSlotId", source = "invigilatorAssignment.invigilatorRegistration.examSlot.id")
    @Mapping(target = "invigilatorId", source = "invigilatorAssignment.invigilatorRegistration.invigilator.id")
    @Mapping(target = "invigilatorFuId", source = "invigilatorAssignment.invigilatorRegistration.invigilator.fuId")
    @Mapping(target = "invigilatorFirstName", source = "invigilatorAssignment.invigilatorRegistration.invigilator.firstName")
    @Mapping(target = "invigilatorLastName", source = "invigilatorAssignment.invigilatorRegistration.invigilator.lastName")
    @Mapping(target = "invigilatorEmail", source = "invigilatorAssignment.invigilatorRegistration.invigilator.email")
    @Mapping(target = "invigilatorPhone", source = "invigilatorAssignment.invigilatorRegistration.invigilator.phoneNumber")
    @Mapping(target = "examSlot", source = "invigilatorAssignment.invigilatorRegistration.examSlot")
    InvigilatorAttendanceResponseDTO toResponseDTO(InvigilatorAttendance entity);

    @Named("intToStatus")
    default InvigilatorAttendanceStatus intToStatus(Integer statusValue) {
        if (statusValue == null) {
            return null;
        }
        return switch (statusValue) {
            case 1 -> InvigilatorAttendanceStatus.PENDING;
            case 2 -> InvigilatorAttendanceStatus.APPROVED;
            case 3 -> InvigilatorAttendanceStatus.REJECTED;
            default -> throw new IllegalArgumentException("Unknown status value: " + statusValue);
        };
    }

    @Named("statusToInt")
    default Integer statusToInt(InvigilatorAttendanceStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PENDING -> 1;
            case APPROVED -> 2;
            case REJECTED -> 3;
            default -> throw new IllegalArgumentException("Unknown ExamSlotStatus: " + status);
        };
    }

    @Named("intToUser")
    default User intToUser(Integer userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("userToInt")
    default Integer userToInt(User user) {
        return user != null ? user.getId() : null;
    }

    @Named("zonedDateTimeToInstant")
    default Instant zonedDateTimeToInstant(ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? zonedDateTime.toInstant() : null;
    }

}
