import java.time.LocalDate;
import java.util.Date;

public class Citizen {
	private int ID;
	private String firstName;
	private String lastName;
	private int phoneNumber;
	private Date date;
	private String address;
	private String FullName;


	public Citizen(int ID,String firstName, String lastName, int phoneNumber,Date date,String address) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.ID = ID;
		this.phoneNumber = phoneNumber;
		this.date = date;
		this.address=address;
		this.FullName=firstName+" "+lastName;
	}
	public String getFullName() {
		return firstName+" "+lastName;
	}
	public void setFullName() {
		this.FullName=firstName+" "+lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "Citizen [ID=" + ID + ", firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber="
				+ phoneNumber + ", date=" + date + ", address=" + address + "]";
	}
	
	
	
}
