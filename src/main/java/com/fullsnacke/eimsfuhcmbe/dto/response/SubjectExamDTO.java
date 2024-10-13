package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectExamDTO {

    Integer subjectExamId;
    Integer duration;
    String examType;
    String subjectName;
    String subjectCode;

    public SubjectExamDTO(Integer subjectExamId, Integer duration, String examType, String subjectName, String subjectCode) {
        this.subjectExamId = subjectExamId;
        this.duration = duration;
        this.examType = examType;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

}
