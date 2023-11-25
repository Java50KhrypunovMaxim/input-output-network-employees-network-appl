package telran.employees.dto;

import java.io.Serializable;

public record DepartmentSalary(String department, double salary) implements Serializable{

}
