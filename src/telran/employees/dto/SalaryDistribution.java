package telran.employees.dto;

import java.io.Serializable;

public record SalaryDistribution(int min, int max, int amount) implements Serializable {

}
