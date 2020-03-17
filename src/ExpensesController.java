import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExpensesController implements Initializable{

	@FXML
	private Button Back;

	@FXML
	private TextField ProjectName;

	@FXML
	private TextField projectDescription;

	@FXML
	private DatePicker DateOfPayement;

	@FXML
	private Button addProject;

	@FXML
	private Button ShowEmployeeSalaries;

	@FXML
	private Button ShowNewProject;

	@FXML
	private TextField Amount;

	@FXML
	private Button SendSalaries;


	private String dbUsername = "root";
	private String dbPassword = "1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "DatabaseProject";
	private Connection con;

	private ArrayList<Employee> employees;


	private ObservableList<Employee> EmployeeList;

	public ExpensesController() throws Exception 
	{
		con = DBConn.getInstances(URL, port, dbName, dbUsername, dbPassword);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ProjectName.setVisible(false);;
		projectDescription.setVisible(false);
		DateOfPayement.setVisible(false);
		addProject.setVisible(false);
		ShowEmployeeSalaries.setVisible(true);
		ShowNewProject.setVisible(true);
		Amount.setVisible(false);
		SendSalaries.setVisible(false);
		Back.setVisible(true);
		employees = new ArrayList<>();
		try {
			getData();
			EmployeeList = FXCollections.observableArrayList(employees);
			System.out.println(EmployeeList);
		} catch (ClassNotFoundException e) {
			Alert("problem getting data ");
		} catch (SQLException e) {
			Alert("problem getting data ");
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
	}


	@FXML
	void ShowEmployeeSalaries(ActionEvent event) {
		ProjectName.setVisible(false);
		projectDescription.setVisible(false);
		DateOfPayement.setVisible(false);
		addProject.setVisible(false);
		ShowEmployeeSalaries.setVisible(true);
		ShowNewProject.setVisible(true);
		Amount.setVisible(false);
		SendSalaries.setVisible(true);
		Back.setVisible(true);

	}

	@FXML
	void ShowNewProject(ActionEvent event) {
		ProjectName.setVisible(true);
		projectDescription.setVisible(true);
		DateOfPayement.setVisible(true);
		addProject.setVisible(true);
		ShowEmployeeSalaries.setVisible(true);
		ShowNewProject.setVisible(true);
		Amount.setVisible(true);
		SendSalaries.setVisible(false);
		Back.setVisible(true);

	}

	private boolean checkIfMoneyAvailable(double amount) throws NumberFormatException, SQLException {

		ExecuteStatement("SET @AvailableMoney:= (Select SUM(amount) from  transaction where ServiceType in ('payment','garbage','graves'));");
		ExecuteStatement("SET @MoneySpent:=(Select SUM(amount) from  transaction where ServiceType in ('projectExpenses','salary'));");
		ExecuteStatement("SET @MoneyNeeded :=("+amount+");");

		String SQL2="SELECT (@AvailableMoney - (@MoneySpent+@MoneyNeeded));";
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL2);

		while (rs.next()) {
			if (Double.parseDouble(rs.getString(1)) > 0) {
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
	void addProject(ActionEvent event) {

		LocalDate currentDate=DateOfPayement.getValue();
		String projectName=ProjectName.getText();
		String ProjectDescription=projectDescription.getText();
		String amount=Amount.getText();

		if(currentDate==null||projectName==null||ProjectDescription==null||amount==null||
				!amount.matches("[0-9]+")) 
		{
			Alert("Make sure your entries are correct");
		} else
			try {

				if(checkIfMoneyAvailable(Double.parseDouble(amount))==false) 
				{
					Alert("The money avaibale isn't enough for a new project!");
				}
				else 
				{
					try 
					{
						System.out.println("Insert into transaction (ServiceType,Amount,CreatedDateTime) values('projectExpenses',"+
								amount +",'"+ currentDate+ "');");
						ExecuteStatement("Insert into transaction (ServiceType,Amount,CreatedDateTime) values('projectExpenses',"+
								amount +",'"+ currentDate+ "');");
						ExecuteStatement("SET @TId := (select  TId from transaction order by TId desc LIMIT 1);");
						ExecuteStatement("Insert into project (TransactionId,ProjectName,ProjectDescription)  values(@TId,'"+projectName+"','"+ProjectDescription+"');");

						ExecuteStatement("SET @PId := (select  PId from project order by PId desc LIMIT 1);");

						ExecuteStatement("Insert into TransactionProjectAssociation (TransactionId,ProjectId)  values(@TId,@PID);");




					}

					catch (Exception e) {
						Alert("something wrong with inserting data");
					}

				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}




	}
	@FXML
	void SendSalaries(ActionEvent event) {

		//
		//			for (int i=0; i< empList.size(); i++){
		//				
		//				(insert transiaction("Salary",empList.get(i).salary, date))
		//				-- get transiactionId via order b desc
		//				insert on transiactionEmployeeAssoc
		double amount=0;
		for(int i=0;i<EmployeeList.size();i++) 
		{
			amount=amount+EmployeeList.get(i).getSalary();
		}

		try 
		{
			if(checkIfMoneyAvailable(amount)==false) 
			{
				Alert("The money avaibale isn't enough for paying the salaries!");
			}

			else 
			{
				LocalDate today=LocalDate.now();
				for(int i=0;i<EmployeeList.size();i++) 
				{

					ExecuteStatement("Insert into transaction (ServiceType,Amount,CreatedDateTime) values('salary',"+
							EmployeeList.get(i).getSalary() +",'"+today+ "');");
					System.out.println("Insert into transaction (ServiceType,Amount,CreatedDateTime) values('salary',"+
							EmployeeList.get(i).getSalary() +",'"+today+ "');");

					ExecuteStatement("SET @TId := (select  TId from transaction order by TId desc LIMIT 1);");
					System.out.println("SET @TId := (select  TId from transaction order by TId desc LIMIT 1);");
					ExecuteStatement("SET @EID :=(Select EId From employee where IdentityNumber="+EmployeeList.get(i).getEID()+");" );
					System.out.println("SET @EID :=(Select EId From employee where IdentityNumber="+EmployeeList.get(i).getEID()+");" );
					ExecuteStatement("Insert into TransactionEmployeeAssociation (TransactionId,EmployeeId)  values(@TId,@EID);");
					System.out.println("Insert into TransactionEmployeeAssociation (TransactionId,EmployeeId)  values(@TId,@EID);");
				}


			}
		} catch (Exception e) 
		{
			Alert("something wrong with checking is money is available");
			e.printStackTrace();
		} 
	}



	void Alert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.setTitle("Error!");
		alert.setHeaderText(null);
		alert.setResizable(false);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.show();
	}

	@FXML
	void Back(ActionEvent event) throws IOException {
		Stage primaryStage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Main");
		primaryStage.show();
	}

	public void ExecuteStatement(String SQL) throws SQLException {

		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(SQL);
			stmt.close();
		}

		catch (Exception e) {
			Alert("problem with excuting statement");
			System.out.println("execute" + e.getMessage());
		}

	}

}
