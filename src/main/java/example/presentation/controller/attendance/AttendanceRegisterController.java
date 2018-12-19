package example.presentation.controller.attendance;

import example.application.service.attendance.AttendanceQueryService;
import example.application.service.attendance.AttendanceRecordService;
import example.application.service.worker.WorkerQueryService;
import example.domain.model.attendance.Attendance;
import example.domain.model.attendance.WorkEndTime;
import example.domain.model.attendance.WorkMonth;
import example.domain.model.attendance.WorkStartTime;
import example.domain.model.worker.ContractingWorkers;
import example.domain.type.time.ClockTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * 勤怠コントローラー
 */
@Controller
@RequestMapping("attendance")
public class AttendanceRegisterController {

    WorkerQueryService workerQueryService;
    AttendanceRecordService attendanceRecordService;
    AttendanceQueryService attendanceQueryService;

    public AttendanceRegisterController(WorkerQueryService workerQueryService, AttendanceRecordService attendanceRecordService, AttendanceQueryService attendanceQueryService) {
        this.workerQueryService = workerQueryService;
        this.attendanceRecordService = attendanceRecordService;
        this.attendanceQueryService = attendanceQueryService;
    }

    @ModelAttribute("workers")
    ContractingWorkers workers() {
        return workerQueryService.contractingWorkers();
    }

    @ModelAttribute("attendanceForm")
    AttendanceForm attendanceForm() {
        AttendanceForm attendanceForm = new AttendanceForm();
        return attendanceForm;
    }

    @GetMapping
    String init(Model model) {
        return "attendance/form";
    }

    @PostMapping
    String register(@Validated @ModelAttribute("attendanceForm") AttendanceForm attendanceForm,
                    BindingResult result) {
        if (result.hasErrors()) return "attendance/form";


        ClockTime startTime = new ClockTime(Integer.valueOf(attendanceForm.startHour), Integer.valueOf(attendanceForm.startMinute));
        ClockTime endTime = new ClockTime(Integer.valueOf(attendanceForm.endHour), Integer.valueOf(attendanceForm.endMinute));

        attendanceRecordService.registerAttendance(
                attendanceForm.workerNumber,
                new Attendance(
                        attendanceForm.workDay,
                        new WorkStartTime(startTime),
                        new WorkEndTime(endTime),
                        attendanceForm.normalBreakTime,
                        attendanceForm.midnightBreakTime
                )
        );

        WorkMonth workMonth = attendanceForm.workDay.month();

        return "redirect:/attendances/" + attendanceForm.workerNumber.value() + "/" + workMonth.toString();
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(
                "workerNumber",
                "workDay.value.value",
                "startHour",
                "startMinute",
                "endHour",
                "endMinute",
                "normalBreakTime",
                "midnightBreakTime"
        );
    }
}
