package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.request.ExamSlotRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ExamSlotDetail;
import com.fullsnacke.eimsfuhcmbe.entity.*;
import com.fullsnacke.eimsfuhcmbe.exception.repository.examslot.ExamSlotNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subject.SubjectNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.repository.subjectexam.SubjectExamNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class ExamSlotServiceImpl implements ExamSlotService {

    @Autowired
    private ExamSlotRepository examSlotRepository;

    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExamSlotHallRepository examSlotHallRepository;
    @Autowired
    private ExamSlotRoomRepository examSlotRoomRepository;
    @Autowired
    private InvigilatorRegistrationRepository invigilatorRegistrationRepository;

    @Override
    public List<ExamSlot> getAllExamSlot() {
        return examSlotRepository.findAll();
    }

    @Override
    public ExamSlot createExamSlot(ExamSlot examSlot) {
        return examSlotRepository.save(examSlot);
    }

    @Override
    public ExamSlot updateExamSlotExamSlot(ExamSlot examSlotInRequest, int id) {
//        int id = examSlotInRequest.getId();
        ExamSlot examSlotInDB =examSlotRepository.findExamSlotById(id);

        User user = userRepository.findUserById(examSlotInRequest.getUpdatedBy().getId());

        if (examSlotInDB == null)
            throw new EntityNotFoundException("ExamSlot not found with ID: " + id);

        if (user.getRole().getId() == 1)
            examSlotInDB.setUpdatedBy(user);

        examSlotInDB.setStartAt(examSlotInRequest.getStartAt());
        examSlotInDB.setEndAt(examSlotInRequest.getEndAt());

        return examSlotRepository.save(examSlotInDB);
    }

    public ExamSlot managerUpdateExamSlot(ExamSlot examSlotInRequest, int id) {
//        int id = examSlotInRequest.getId();
        ExamSlot examSlotInDB =examSlotRepository.findExamSlotById(id);

        User user = userRepository.findUserById(examSlotInRequest.getUpdatedBy().getId());

        if (examSlotInDB == null)
            throw new EntityNotFoundException("ExamSlot not found with ID: " + id);

        if (user.getRole().getId() == 1)
            examSlotInDB.setUpdatedBy(user);

        examSlotInDB.setStatus(examSlotInRequest.getStatus());
        examSlotInDB.setUpdatedBy(examSlotInRequest.getUpdatedBy());

        return examSlotRepository.save(examSlotInDB);
    }

    @Override
    public ExamSlot findById(int id) {
        return examSlotRepository.findById(id)
                .orElseThrow(() -> new ExamSlotNotFoundException("Exam Slot not found with ID: " + id));
    }

    @Override
    public void deleteExamSlot(int id) {
        ExamSlot examSlot = findById(id);
            examSlotRepository.delete(examSlot);
    }

    @Override
    public List<List<Room>> getHallForExamSlot(int examSlotId) {
        ExamSlot examSlot = examSlotRepository.findExamSlotById(examSlotId);
        List<ExamSlotHall> examSlotHallList = examSlotHallRepository.findByExamSlot(examSlot);
        if (examSlotHallList == null) {
            return new ArrayList<>();
        }

        List<List<Room>> result = new ArrayList<>();
        for (ExamSlotHall hall : examSlotHallList) {
            List<ExamSlotRoom> rooms = examSlotRoomRepository.findByExamSlotHall(hall);
            if (rooms == null) {
                return new ArrayList<>();
            }
            List<Room> roomNames = new ArrayList<>();
            for (ExamSlotRoom room : rooms) {
                roomNames.add(room.getRoom());
            }
            result.add(roomNames);
        }
        return result;
    }

    @Override
    public List<ExamSlot> getExamSlotsBySemesterId(int semesterId) {
        Semester semester = semesterRepository.findById(semesterId).orElseThrow(() -> new RuntimeException("Semester not found"));

        return examSlotRepository.findExamSlotsBySemesterWithDetails(semester);
    }

    public List<ExamSlotDetail> getExamSlotsStatusIn (LocalDate startAt, LocalDate endAt) {

        List<ExamSlot> examSlots = examSlotRepository.findExamSlotsByStartAtBetween(toZonedDateTime(startAt), toZonedDateTime(endAt));
        if (examSlots == null) {
            log.info("No exam slots found in the given date range");
            return new ArrayList<>();
        }
        System.out.println("Exam slots: " + examSlots.size());
        List<ExamSlotDetail> examSlotDetails = new ArrayList<>();
        for (ExamSlot examSlot : examSlots) {
            ExamSlotDetail examSlotDetail = ExamSlotDetail.builder()
                    .examSlotId(examSlot.getId())
                    .subjectCode(examSlot.getSubjectExam().getSubjectId().getCode())
                    .examType(examSlot.getSubjectExam().getExamType())
                    .startAt(examSlot.getStartAt())
                    .endAt(examSlot.getEndAt())
                    .numberOfRegistered(invigilatorRegistrationRepository.countByExamSlot(examSlot))
                    .requiredInvigilators(examSlot.getRequiredInvigilators())
                    .build();
            examSlotDetails.add(examSlotDetail);
        }
        return examSlotDetails;
    }
    private ZonedDateTime toZonedDateTime(LocalDate date) {
        // Tạo LocalTime để xác định thời gian trong ngày
        LocalTime localTime = LocalTime.of(0, 00); // 0 giờ s0 phút

        // Xác định múi giờ (ZoneId)
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        return ZonedDateTime.of(date, localTime, zoneId);
    }
}