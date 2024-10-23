package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvigilatorAttendanceListResponseDTO {
    List<InvigilatorAttendanceResponseDTO> invigilatorAttendanceList;
    private double hourlyRate;
    private double totalHours;
    private double preCalculatedInvigilatorFree;

    public void setTotalHours() {
        double totalHours = 0;
        if(!invigilatorAttendanceList.isEmpty()){
            for (InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO : invigilatorAttendanceList) {
                if(invigilatorAttendanceResponseDTO.getCheckIn() != null && invigilatorAttendanceResponseDTO.getCheckOut() != null){
                    double second = invigilatorAttendanceResponseDTO.getEndAt().getEpochSecond() - invigilatorAttendanceResponseDTO.getStartAt().getEpochSecond();
                    double hours = second / 3600;
                    System.out.println("Start at: " + invigilatorAttendanceResponseDTO.getStartAt());
                    System.out.println("Start at: " + invigilatorAttendanceResponseDTO.getEndAt());
                    System.out.println("hours: " + hours);
                    totalHours += hours;
                }
            }
        }
        this.totalHours = totalHours;
    }

    public void setPreCalculatedInvigilatorFree() {
        this.preCalculatedInvigilatorFree = this.totalHours * hourlyRate;
    }
}
