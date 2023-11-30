package telran.employees.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

import telran.employees.dto.DepartmentSalary;
import telran.employees.dto.Employee;
import telran.employees.dto.SalaryDistribution;

public class CompanyImpl implements Company {
	HashMap<Long, Employee> employees = new HashMap<>(); //most effective structure for the interface methods
	TreeMap<LocalDate, List<Employee>> employeesDate = new TreeMap<>();
	TreeMap<Integer, List<Employee>> employeesSalary = new TreeMap<>();
	HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
	ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	Lock readLock = readWriteLock.readLock();
	Lock writeLock = readWriteLock.writeLock();
	@Override
	public boolean addEmployee(Employee empl) {
		
		try {
			writeLock.lock();
			boolean res = employees.putIfAbsent(empl.id(), empl) == null;
			if (res) {
				LocalDate date = empl.birthDate();
				Integer salary = empl.salary();
				String department = empl.department();
				addToIndex(empl, date, employeesDate);
				addToIndex(empl, salary, employeesSalary);
				addToIndex(empl, department, employeesDepartment);
			}
			return res;
		} finally {
			writeLock.unlock();
		}
	}

	private <T> void addToIndex(Employee empl, T key, Map<T, List<Employee>> map) {
		map.computeIfAbsent(key, k -> new LinkedList<>()).add(empl);
	}

	@Override
	public Employee removeEmployee(long id) {
		try {
			writeLock.lock();
			Employee empl = employees.remove(id);
			if (empl != null) {
				LocalDate date = empl.birthDate();
				Integer salary = empl.salary();
				String department = empl.department();
				removeFromIndex(empl, date, employeesDate);
				removeFromIndex(empl, salary, employeesSalary);
				removeFromIndex(empl, department, employeesDepartment);
			}
			return empl;
		} finally {
			writeLock.unlock();
		}
	}

	private <T> void removeFromIndex(Employee empl, T key, Map<T, List<Employee>> map) {

		List<Employee> employeesCol = map.get(key);
		employeesCol.remove(empl);
		if (employeesCol.isEmpty()) {
			map.remove(key);
		}
	}

	@Override
	public Employee getEmployee(long id) {
		
		try {
			readLock.lock();
			return employees.get(id);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<Employee> getEmployees() {
		try {
			readLock.lock();
			return new ArrayList<>(employees.values());
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<DepartmentSalary> getDepartmentSalaryDistribution() {
		try {
			readLock.lock();
			return new LinkedList<>(employees.values().stream()
					.collect(Collectors.groupingBy(Employee::department, Collectors.averagingInt(Employee::salary)))
					.entrySet().stream().map(e -> new DepartmentSalary(e.getKey(), e.getValue())).toList());
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<SalaryDistribution> getSalaryDistribution(int interval) {
		try {
			readLock.lock();
			Map<Integer, Long> mapIntervalNumbers = employees.values().stream()
					.collect(Collectors.groupingBy(e -> e.salary() / interval, Collectors.counting()));
			return mapIntervalNumbers.entrySet().stream()
					.map(e -> new SalaryDistribution(e.getKey() * interval, e.getKey() * interval + interval,
							e.getValue().intValue()))
					.sorted((sd1, sd2) -> Integer.compare(sd1.min(), sd2.min())).toList();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<Employee> getEmployeesByDepartment(String department) {
		try {
			readLock.lock();
			Collection<Employee> employeesCol = employeesDepartment.get(department);
			ArrayList<Employee> res = new ArrayList<>();
			if (employeesCol != null) {
				res.addAll(employeesCol);
			}
			return res;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo) {
		try {
			readLock.lock();
			return employeesSalary.subMap(salaryFrom, salaryTo).values().stream().flatMap(col -> col.stream())
					.toList();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<Employee> getEmployeesByAge(int ageFrom, int ageTo) {
		
		try {
			readLock.lock();
			LocalDate dateFrom = getDate(ageTo);
			LocalDate dateTo = getDate(ageFrom);
			return employeesDate.subMap(dateFrom, dateTo).values().stream().flatMap(List::stream).toList();
		} finally {
			readLock.unlock();
		}
	}

	private LocalDate getDate(int age) {
		LocalDate currentDate = LocalDate.now();
		
		return currentDate.minusYears(age);
	}

	
	

	@Override
	public Employee updateSalary(long id, int newSalary) {
		try {
			writeLock.lock();
			Employee empl = removeEmployee(id);
			if (empl != null) {
				Employee newEmployee = new Employee(id, empl.name(), empl.department(), newSalary, empl.birthDate());
				addEmployee(newEmployee);
			}
			return empl;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Employee updateDepartment(long id, String department) {
		try {
			writeLock.lock();
			Employee empl = removeEmployee(id);
			if (empl != null) {
				Employee newEmployee = new Employee(id, empl.name(), department, empl.salary(), empl.birthDate());
				addEmployee(newEmployee);
			}
			return empl;
		} finally {
			writeLock.unlock();
		}
	}

}
