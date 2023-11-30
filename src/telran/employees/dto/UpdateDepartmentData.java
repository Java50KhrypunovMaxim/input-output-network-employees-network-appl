package telran.employees.dto;

import java.io.Serializable;

public record UpdateDepartmentData(long id, String newDepartment) implements Serializable {

}
