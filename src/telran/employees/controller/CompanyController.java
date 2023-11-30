package telran.employees.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


import telran.employees.dto.DepartmentSalary;
import telran.employees.dto.Employee;
import telran.employees.dto.SalaryDistribution;
import telran.employees.service.Company;
import telran.view.InputOutput;
import telran.view.Item;
public class CompanyController {
private static final long MIN_ID = 100000;
private static final long MAX_ID = 999999;
private static final String[] DEPARTMENTS = {"QA", "Development", "Audit", "Accounting", "Management"};
private static final int MIN_SALARY = 7000;
private static final int MAX_SALARY = 50000;
private static final int MIN_INTERVAL = 500;
private static final int MAX_INTERVAL = 5000;
private static final int MIN_AGE = 20;
private static final int MAX_AGE = 75;
private static Company company;
	public static ArrayList<Item> getItems(Company company) {
		CompanyController.company = company;
		List<Item> itemsList = getItemsList();
		ArrayList<Item> res = new ArrayList<>(itemsList);
		return res;
	}
	private static List<Item> getItemsList() {
		
		return List.of(
				Item.of("Hire new Employee", CompanyController::addEmployee),
				Item.of("Fire  Employee", CompanyController::removeEmployee),
				Item.of("Display data of Employee", CompanyController::getEmployee),
				Item.of("Display data of all Employees", CompanyController::getEmployees),
				Item.of("Distribution of salary by departments", CompanyController::getDepartmentSalaryDistribution),
				Item.of("Salary distribution per interval", CompanyController::getSalaryDistribution),
				Item.of("Display data of Employees in department", CompanyController::getEmployeesByDepartment),
				Item.of("Display data of Employees by salary", CompanyController::getEmployeesBySalary),
				Item.of("Display data of Employees by age", CompanyController::getEmployeesByAge),
				Item.of("Update salary", CompanyController::updateSalary),
				Item.of("Update department", CompanyController::updateDepartment)
				);
	}
	static void addEmployee(InputOutput io) {
		Long id = getId(io, false);
		String name = io.readString("Enter name", "Wrong name", str -> str.matches("[A-Z][a-z]{2,}"));
		String department = getDepartment(io);
		int salary = getSalary(io);
		LocalDate birthDate = io.readIsoDate("Enter birtdate in ISO format", "Wrong birthdate",
				getBirthdate(MAX_AGE), getBirthdate(MIN_AGE));
		Employee empl = new Employee(id, name, department, salary, birthDate);
		boolean res = company.addEmployee(empl );
		io.writeLine(res ? "Employee has been added" : "Employee already exists");
	}
	
	
static void removeEmployee(InputOutput io) {
	Long id = getId(io, true);
	
	io.writeObject("Removed employee is ");
	io.writeObjectLine(company.removeEmployee(id));
	}
static void getEmployee(InputOutput io) {
	Long id = getId(io, true);
	
	io.writeObject("employee is ");
	io.writeObjectLine(company.getEmployee(id));
}
static void getEmployees(InputOutput io) {
	List<Employee> employees = company.getEmployees();
	displayResult(employees, io);//fixed at class - System.out::println
}
static void getDepartmentSalaryDistribution(InputOutput io) {
	displayResult(company.getDepartmentSalaryDistribution(), io);
}
static void getSalaryDistribution(InputOutput io) {
	int interval = io.readInt("Enter salary distribution interval" , "Wrong interval",
			MIN_INTERVAL, MAX_INTERVAL);
	displayResult(company.getSalaryDistribution(interval), io);
}
static void getEmployeesByDepartment(InputOutput io) {
	String department = getDepartment(io);
	displayResult(company.getEmployeesByDepartment(department), io);
}
static void getEmployeesBySalary(InputOutput io) {
	int[] fromTo = getSalaries(io);
	displayResult(company.getEmployeesBySalary(fromTo[0], fromTo[1]), io);
}
static void getEmployeesByAge(InputOutput io) {
	int [] fromTo = getAgies(io);
	displayResult(company.getEmployeesByAge(fromTo[0], fromTo[1]), io);
}
static void updateSalary(InputOutput io) {
	Long id = getId(io, true);
	
	int salary = getSalary(io);
	Employee empl = company.updateSalary(id, salary);
	io.writeLine(String.format("old salary value %d of employee %d"
			+ " has been updated with new value %d", empl.salary(),
			empl.id(), salary));
}
static void updateDepartment(InputOutput io) {
	Long id = getId(io, true);
	
	String department = getDepartment(io);
	Employee empl = company.updateDepartment(id, department);
	io.writeLine(String.format("old deprtment %s of employee %d"
			+ " has been updated with department %s", empl.department(),
			empl.id(), department));
}
static private Long getId(InputOutput io, boolean isExists) {
	Long id = io.readLong("Enter Employee identity", "Wrong identity value",
			MIN_ID, MAX_ID);
	Employee empl = company.getEmployee(id);
	
	String exceptionText = "";
	Long res = (empl != null && isExists) || (empl == null && !isExists) ?
			id : null;
	if(res == null ) {
		exceptionText = isExists ? String.format("Employee with id %d doesn't exist", id)
				: String.format("Employee with id %d already exists", id);
	} 
	if (!exceptionText.isEmpty()) {
		throw new RuntimeException(exceptionText);
	}
	return res;
	
}
static private  <T> void displayResult(List<T> list, InputOutput io) {
	if(list.isEmpty()) {
		io.writeLine("No data matching the request");
	}
	list.forEach(io::writeObjectLine);
}
private static int[] getSalaries(InputOutput io) {
	int from = io.readInt("Enter salary from", "Wrong salary-from value", MIN_SALARY,
			MAX_SALARY - 1);
	int to =  io.readInt("Enter salary to", "Wrong salary-to value", from, MAX_SALARY);
	return new int[] {from, to};
}
private static String getDepartment(InputOutput io) {
	return io.readString("Enter department " + Arrays.deepToString(DEPARTMENTS), "Wrong department", new HashSet<String>(List.of(DEPARTMENTS)));
}
private static int[] getAgies(InputOutput io) {
	int from = io.readInt("Enter age from", "Wrong age-from value", MIN_AGE, MAX_AGE - 1 );
	int to =  io.readInt("Enter age to", "Wrong age-to value", from, MAX_AGE);
	return new int[] {from, to};
}
private static LocalDate getBirthdate(int age) {
	
	return LocalDate.now().minusYears(age);
}
private static Integer getSalary(InputOutput io) {
	return io.readInt("Enter Salary", "Wrong salary", MIN_SALARY, MAX_SALARY);
}

}
