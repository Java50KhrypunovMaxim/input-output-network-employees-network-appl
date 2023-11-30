package telran.employees.dto;

import java.io.Serializable;

public record UpdateSalaryData(long id, int newSalary) implements Serializable {

}
