package example.presentation.controller.employee;

import example.application.service.employee.EmployeeQueryService;
import example.application.service.employee.EmployeeRecordService;
import example.domain.model.employee.Employee;
import example.domain.model.employee.EmployeeNumber;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 従業員の更新
 */
@Controller
@RequestMapping("employees/{employeeNumber}/update")
@SessionAttributes({"employee"})
class EmployeeUpdateController {

    private static final String[] allowFields =
            {
                    "name.value",
                    "mailAddress.value",
                    "phoneNumber.value",
            };

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(allowFields);
    }

    EmployeeQueryService employeeQueryService;
    EmployeeRecordService employeeRecordService;

    @GetMapping("")
    String clearSessionAtStart(@PathVariable(value = "employeeNumber") EmployeeNumber employeeNumber,
                               SessionStatus status) {
        status.setComplete();
        return "forward:/employees/" + employeeNumber + "/update/input";
    }

    @GetMapping(value = "input")
    String formToEdit(@PathVariable(value = "employeeNumber") EmployeeNumber employeeNumber,
                      Model model) {
        Employee employee = employeeQueryService.choose(employeeNumber);
        model.addAttribute("employee", employee);
        return "employee/update/form";
    }


    @PostMapping(value = "register")
    String registerThenRedirect(@Validated @ModelAttribute Employee employee,
                                BindingResult result,
                                SessionStatus status,
                                RedirectAttributes attributes) {
        if (result.hasErrors()) return "employee/update/form";

        employeeRecordService.registerName(employee.employeeNumber(), employee.name());
        employeeRecordService.registerMailAddress(employee.employeeNumber(), employee.mailAddress());
        employeeRecordService.registerPhoneNumber(employee.employeeNumber(), employee.phoneNumber());

        status.setComplete();

        attributes.addAttribute("updateResult", "completed");

        return "redirect:/employees/" + employee.employeeNumber();
    }

    EmployeeUpdateController(EmployeeRecordService employeeRecordService, EmployeeQueryService employeeQueryService) {
        this.employeeRecordService = employeeRecordService;
        this.employeeQueryService = employeeQueryService;
    }
}
