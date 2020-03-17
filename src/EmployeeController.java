import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.mysql.jdbc.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmployeeController implements Initializable{

	@FXML
	private TableView<Employee> EmployeeTable;

	@FXML
	private TableColumn<Employee, Integer> TableEid;

	@FXML
	private TableColumn<Employee, String> TableName;

	@FXML
	private TableColumn<Employee, Integer> TablePhoneNum;

	@FXML
	private TableColumn<Employee, Date> TableDate;

	@FXML
	private TableColumn<Employee, Integer> TableSalary;

	@FXML
	private Button showAddEmp;
	@FXML
	private Button showDeleteEmp;
	@FXML
	private Button showSearchEmp;
	@FXML
	private Button ShowModifyEmp;
	@FXML
	private Button BackButton;

	
	@FXML
	private TextField EID;
	@FXML
	private TextField FirstName;
	@FXML
	private TextField LastName;
	@FXML
	private TextField PhoneNumber;
	@FXML
	private DatePicker DOB;
	@FXML
	private TextField Salary;

	@FXML
	private Button modify;

	@FXML
	private Button search;

	@FXML
	private Button add;

	@FXML
	private Button delete;
	
	@FXML
	private Label SearchLabel;
	
	

	private ArrayList<Employee> employees;


	private ObservableList<Employee> EmployeeList;


	private String dbUsername = "root";
	private String dbPassword = "1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "LastEditionDataBaseProject";
	private Connection con;

	public EmployeeController() throws Exception {
		con = DBConn.getInstances(URL, port, dbName, dbUsername, dbPassword);
	}

	@Override
	public void initialize(java.net.URL location, ResourceBundle resources) {
		LastName.setVisible(false);
		EID.setVisible(false);
		DOB.setVisible(false);
		FirstName.setVisible(false);
		PhoneNumber.setVisible(false);
		modify.setVisible(false);
		search.setVisible(false);
		add.setVisible(false);
		delete.setVisible(false);
		BackButton.setVisible(true);
		SearchLabel.setVisible(false);
		Salary.setVisible(false);
		employees = new ArrayList<>();
		try {
			getData();
			EmployeeList = FXCollections.observableArrayList(employees);
		} catch (ClassNotFoundException e) {
			Alert("problem getting data ");
		} catch (SQLException e) {
			Alert("problem getting data ");
		}


		// Setting up columns in the table
		TableEid.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("EID"));
		TablePhoneNum.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("PhoneNumber"));
		TableDate.setCellValueFactory(new PropertyValueFactory<Employee, Date>("DOB"));
		TableSalary.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("Salary"));
		TableName.setCellValueFactory(new PropertyValueFactory<Employee, String>("FullName"));
	
		EmployeeTable.setItems(EmployeeList);
    
	}
	
	void updateTable() {
		employees = new ArrayList<>();
		try {
			getData();
			EmployeeList = FXCollections.observableArrayList(employees);

		} catch (ClassNotFoundException e) {
			Alert("problem getting data ");
		} catch (SQLException e) {
			Alert("problem getting data ");
		}

		// Setting up columns in the table
		TableEid.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("ID"));
		TablePhoneNum.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("phoneNumber"));
		TableDate.setCellValueFactory(new PropertyValueFactory<Employee, Date>("date"));
		TableSalary.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("salary"));
		TableName.setCellValueFactory(new PropertyValueFactory<Employee, String>("FullName"));
		EmployeeTable.setItems(EmployeeList);
	}
	
	public void ExecuteStatement(String SQL) throws SQLException { //execute in mysql 

		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(SQL);
			stmt.close();

		} catch (SQLException s) {
			s.printStackTrace();
			System.out.println("SQL statement is not executed!");

		} catch (Exception e) {
			System.out.println("execute" + e.getMessage());
		}

	}
	

	
	private void getData() throws SQLException, ClassNotFoundException { //To upload data from mysql 

		String SQL;

		SQL = "select * from employee";
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL); 

		while (rs.next())
			employees.add(new Employee(rs.getInt(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getDate(7), rs.getInt(6))); // 2 is the order of lines in mysql

		rs.close();
		stmt.close();
		System.out.println(employees);
	}
	
	private boolean ifIdExists(int eid) throws ClassNotFoundException, SQLException {

		String SQL = "Select 0 From employee where employee.IdentityNumber=" + eid + ";";
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next()) {
			if (Integer.parseInt(rs.getString(1)) == 0) {
				rs.close();
				stmt.close();
				return true;
			}
		}

		rs.close();
		stmt.close();

		return false;
	}

	
	@FXML
	void AddEmployee(ActionEvent event) throws ClassNotFoundException, SQLException {
		String eid = EID.getText();
		String firstname = FirstName.getText();
		String lastname = LastName.getText();
		String phonenumber = PhoneNumber.getText();
		LocalDate dob = DOB.getValue();
		String salary = Salary.getText();
	if (firstname == null || lastname == null || eid == null || phonenumber == null || dob == null || salary == null
				|| !firstname.matches("[a-zA-Z]+") || !lastname.matches("[a-zA-Z]+") || !eid.matches("[0-9]+")
				|| !phonenumber.matches("[0-9]+")) {
			Alert("Make sure your entries are correct");
		} else if ((ifIdExists(Integer.parseInt(eid)) == true)) {
			Alert("This Id already exists. Please try again");
		} else {
			int e = Integer.parseInt(eid);
			int p = Integer.parseInt(phonenumber);
			Date d = Date.valueOf(dob);
			int s = Integer.parseInt(salary);
			System.out.println(e +" "+ p + " "+d +" "+s +  " " );
			Employee x = new Employee(e, firstname, lastname, p,d, s);
	//		System.out.println(x.toString());
			EmployeeList.add(x);
			FirstName.clear();
			LastName.clear();
			EID.clear();
			PhoneNumber.clear();
			DOB.setValue(null);
			Salary.clear();
			insertData(x);
			EmployeeTable.setItems(EmployeeList);

		}
		System.out.println(EmployeeList);
	}
	
	private void insertData(Employee emp) {

		try {
			System.out.println("Insert into employee (IdentityNumber,FName,LName,Phone,Salary,DOB) values("
					+ emp.getEID() + ",'" + emp.getFirstName() + "','" + emp.getLastName() + "'," + emp.getPhoneNumber()
					+ ",'" + emp.getSalary() + "','" + emp.getDOB() + "')");
			ExecuteStatement("Insert into employee (IdentityNumber,FName,LName,Phone,Salary,DOB) values(" + emp.getEID()
			+ ",'" + emp.getFirstName() + "','" + emp.getLastName() + "'," + emp.getPhoneNumber() + ",'"
			+ emp.getSalary() + "','" + emp.getDOB() + "')");

		} catch (Exception e) {
			Alert("something wrong with inserting data");
		}
	}
	
	@FXML
	void DeleteEmployee(ActionEvent event) throws NumberFormatException, ClassNotFoundException, SQLException {
		String eid = EID.getText();
		if (eid == null || !eid.matches("[0-9]+")) {
			Alert("Please enter the correct id");
		} else if ((ifIdExists(Integer.parseInt(eid)) == false)) {
			Alert("The following ID does not exist. Please try again");
		} else {
			try {
				System.out.println("DELETE FROM employee WHERE IdentityNumber=" + eid + ";");
				ExecuteStatement("DELETE FROM employee WHERE IdentityNumber=" + eid + ";");

			}

			catch (Exception e) {
				System.out.println("something is wrong with excuting the statement");

			}

			updateTable();
		}

	}

	@FXML
	void ModifyEmployee(ActionEvent event) throws Exception {
		String firstName = FirstName.getText();
		String lastName = LastName.getText();
		String eid = EID.getText();
		String phone = PhoneNumber.getText();
		LocalDate dob = DOB.getValue();
		String salary = Salary.getText();
		//StringUtils.isNullOrEmpty(firstName);

		if (firstName == null & lastName == null & eid == null & phone == null & dob == null & salary == null
				|| !eid.matches("[0-9]+")) {
			Alert("Make sure your entries are correct");
		}
		// checking if ID exists
		else 
		{
			if ((ifIdExists(Integer.parseInt(eid)) == false)) 
			{
				Alert("This Id doesn't exist. Please try again");
			}
			else 
			{
				String updateSqlQuery = " UPDATE employee \n SET ";
				boolean flag=false;
				if (!StringUtils.isNullOrEmpty(salary)) 
				{
					updateSqlQuery = updateSqlQuery + "Salary='" + salary + "'\n,";
					flag=true;
				}
				if (!StringUtils.isNullOrEmpty(firstName))
				{
					updateSqlQuery = updateSqlQuery + "FName='" + firstName + "'\n,";
					flag=true;
				}
				if (!StringUtils.isNullOrEmpty(lastName))
				{
					updateSqlQuery = updateSqlQuery + "LName='" + lastName + "'\n,";
					flag=true;
				}


				updateSqlQuery = updateSqlQuery.substring(0, updateSqlQuery.length() - 1);

				updateSqlQuery = updateSqlQuery + " WHERE IdentityNumber= " + eid;
				System.out.println(updateSqlQuery);

				if(flag==true)
				{
					ExecuteStatement(updateSqlQuery);
					updateTable();
				}
				else 
					Alert("Please make sure your entries are correct");
			}
		}
		FirstName.clear();
		LastName.clear();
		EID.clear();
		PhoneNumber.clear();
		DOB.setValue(null);
		Salary.clear();

	}
	
	@FXML
	void SearchEmployee(ActionEvent event) throws NumberFormatException, ClassNotFoundException, SQLException {
		SearchLabel.setVisible(true);

		String eid = EID.getText();

		if (eid == null || !eid.matches("[0-9]+")) {
			Alert("Please enter the correct id");
		} else if ((ifIdExists(Integer.parseInt(eid)) == false)) {
			Alert("The following ID does not exist. Please try again");
		} else {
			String SQL = "Select e.IdentityNumber, e.FName,e.LName,e.Phone,e.Salary,e.DOB "
					+ "From employee e where IdentityNumber=" + eid + ";";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);

			while (rs.next()) {
				SearchLabel.setText("EID=" + rs.getInt(1) + "\n" + "Full Name=" + rs.getString(2) + " " + rs.getString(3)
				+ "\n" + "Phone Number=" + rs.getInt(4) + "\n" + "DOB=" + rs.getDate(6) + "\n" + "Salary="
				+ rs.getString(5));
				SearchLabel.setFont(Font.font("Cambria", 20));
				SearchLabel.setTextFill(Color.web("#000000"));
				SearchLabel.setStyle("-fx-font-weight: bold;");
			}

			rs.close();
			stmt.close();
		}

	}
	
	@FXML
	void ShowAddEmployee(ActionEvent event) {
		LastName.setVisible(true);
		EID.setVisible(true);
		Salary.setVisible(true);
		DOB.setVisible(true);
		FirstName.setVisible(true);
		PhoneNumber.setVisible(true);
		modify.setVisible(false);
		search.setVisible(false);
		add.setVisible(true);
		delete.setVisible(false);
		BackButton.setVisible(true);

	}
	
	@FXML
	void ShowDeleteEmployee(ActionEvent event) {
		LastName.setVisible(false);
		EID.setVisible(true);
		Salary.setVisible(false);
		DOB.setVisible(false);
		BackButton.setVisible(false);
		FirstName.setVisible(false);
		PhoneNumber.setVisible(false);
		modify.setVisible(false);
		search.setVisible(false);
		add.setVisible(false);
		delete.setVisible(true);
		BackButton.setVisible(true);
	}
	
	@FXML
	void ShowModifyEmployee(ActionEvent event) {
		LastName.setVisible(true);
		EID.setVisible(true);
		Salary.setVisible(true);
		DOB.setVisible(true);
		FirstName.setVisible(true);
		PhoneNumber.setVisible(true);
		modify.setVisible(true);
		search.setVisible(false);
		add.setVisible(false);
		delete.setVisible(false);
		BackButton.setVisible(true);
	}
	
	@FXML
	void ShowSearchEmployee(ActionEvent event) {
		LastName.setVisible(false);
		EID.setVisible(true);
		Salary.setVisible(false);
		DOB.setVisible(false);
		BackButton.setVisible(false);
		FirstName.setVisible(false);
		PhoneNumber.setVisible(false);
		modify.setVisible(false);
		search.setVisible(true);
		add.setVisible(false);
		delete.setVisible(false);
		BackButton.setVisible(true);

	}


	void Alert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.setTitle("Error!");
		alert.setHeaderText(null);
		alert.setResizable(false);
		alert.show();
	}

	@FXML
	public void GoBack(ActionEvent event) throws IOException {
		Stage primaryStage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Main_Page");
		primaryStage.show();
	}
	
}
