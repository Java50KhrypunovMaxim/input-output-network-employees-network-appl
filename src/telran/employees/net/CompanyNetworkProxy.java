package telran.employees.net;

import java.util.List;

import telran.employees.dto.DepartmentSalary;
import telran.employees.dto.Employee;
import telran.employees.dto.SalaryDistribution;
import telran.employees.dto.UpdateDepartmentData;
import telran.employees.dto.UpdateSalaryData;
import telran.employees.service.Company;
import telran.net.NetworkHandler;

public class CompanyNetworkProxy implements Company {
	private NetworkHandler networkHandler;
	
	public CompanyNetworkProxy(NetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	@Override
	public boolean addEmployee(Employee empl) {
		
		return networkHandler.send("employee/add", empl);
	}

	@Override
	public Employee removeEmployee(long id) {
		
		return networkHandler.send("employee/remove", id);
	}

	@Override
	public Employee getEmployee(long id) {
		
		return networkHandler.send("employee/get", id);
	}

	@Override
	public List<Employee> getEmployees() {
		
		return networkHandler.send("employees/all", null);
	}

	@Override
	public List<DepartmentSalary> getDepartmentSalaryDistribution() {
		
		return networkHandler.send("employees/department/salary/distribution", null);
	}

	@Override
	public List<SalaryDistribution> getSalaryDistribution(int interval) {
		return networkHandler.send("employees/salary/distribution", interval);
	}

	@Override
	public List<Employee> getEmployeesByDepartment(String department) {
		return networkHandler.send("employees/department/get", department);
	}

	@Override
	public List<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo) {
		return networkHandler.send("employees/salary/get", new int[] {salaryFrom, salaryTo});
	}

	@Override
	public List<Employee> getEmployeesByAge(int ageFrom, int ageTo) {
		return networkHandler.send("employees/age/get", new int[] {ageFrom, ageTo});
	}

	@Override
	public Employee updateSalary(long id, int newSalary) {
		
		return networkHandler.send("employee/salary/update", new UpdateSalaryData(id, newSalary));
	}

	@Override
	public Employee updateDepartment(long id, String department) {
		return networkHandler.send("employee/department/update", new UpdateDepartmentData(id, department));
	}

}
