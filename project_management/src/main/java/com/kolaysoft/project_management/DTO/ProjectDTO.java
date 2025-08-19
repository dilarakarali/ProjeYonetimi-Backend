package com.kolaysoft.project_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    private Long id;
    private String projectName;
    private String status;
    private BigDecimal budget;
    private LocalDate startDate;
    private LocalDate endDate;

    // Bu projede hangi çalışanlar var?
    private List<String> employeeNames;
}
