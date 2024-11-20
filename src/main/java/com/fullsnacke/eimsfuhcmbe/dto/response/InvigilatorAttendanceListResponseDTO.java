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
    private int totalExamSlots;
    private double hourlyRate;
    private double totalHours;
    private double preCalculatedInvigilatorFree;
    private String fuId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNum;
    private Integer id;

    public void setTotalHours() {
        double totalHours = 0;
        int totalExamSlots = 0;
        if(!invigilatorAttendanceList.isEmpty()){
            for (InvigilatorAttendanceResponseDTO invigilatorAttendanceResponseDTO : invigilatorAttendanceList) {
                if(invigilatorAttendanceResponseDTO.getCheckIn() != null && invigilatorAttendanceResponseDTO.getCheckOut() != null){
                    double second = invigilatorAttendanceResponseDTO.getEndAt().getEpochSecond() - invigilatorAttendanceResponseDTO.getStartAt().getEpochSecond();
                    double hours = second / 3600;
                    hours = hours*100;
                    hours = Math.round(hours);
                    hours = hours /100;
                    System.out.println("Start at: " + invigilatorAttendanceResponseDTO.getStartAt());
                    System.out.println("Start at: " + invigilatorAttendanceResponseDTO.getEndAt());
                    System.out.println("hours: " + hours);
                    totalHours += hours;
                    totalExamSlots++;
                }
            }
        }
        this.totalHours = totalHours;
        this.totalExamSlots = totalExamSlots;
    }

    public void setPreCalculatedInvigilatorFree() {
        this.preCalculatedInvigilatorFree = this.totalHours * hourlyRate;
    }
}
