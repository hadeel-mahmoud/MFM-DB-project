import java.sql.Date;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class Employee {
	
	private int EID;
	private String FirstName;
	private String LastName;
	private int PhoneNumber;
	private Date DOB;
	private int Salary;
	private String FullName;
	
	public Employee() {
		
	}
	public Employee(int eID, String firstname , String lastname, int phoneNumber, Date date, int salary) {
		super();
		EID = eID;
		FirstName = firstname;
		LastName = lastname ;
		PhoneNumber = phoneNumber;
		DOB = date;
		Salary = salary;
		FullName = firstname+" "+lastname;
	}
	public String getFullName() {
		return FirstName+" "+LastName;
	}
	public void setFullName() {
		FullName=FirstName+" "+LastName;
	}
	public int getEID() {
		return EID;
	}
	public void setEID(int eID) {
		EID = eID;
	}
	public String getFirstName() {
		return FirstName;
	}
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	
	public int getPhoneNumber() {
		return PhoneNumber;
	}
	public void setPhoneNumber(int phoneNumber) {
		PhoneNumber = phoneNumber;
	}
	public Date getDOB() {
		return DOB;
	}
	public void setDOB(Date dOB) {
		DOB = dOB;
	}
	public int getSalary() {
		return Salary;
	}
	public void setSalary(int salary) {
		Salary = salary;
	}
	@Override
	public String toString() {
		return "Employee [EID=" + EID + ", FirstName=" + FirstName + ", LastName=" + LastName + ", PhoneNumber=" + PhoneNumber + ", DOB=" + DOB
				+ ", Salary=" + Salary + "]";
	}
	
	
	

}
