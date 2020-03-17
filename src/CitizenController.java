import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

import com.mysql.jdbc.StringUtils;

import java.sql.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CitizenController implements Initializable {

	@FXML
	private TableView<Citizen> CitizenTable;

	@FXML
	private TableColumn<Citizen, Integer> TableID;

	@FXML
	private TableColumn<Citizen, String> TableName;

	@FXML
	private TableColumn<Citizen, Integer> TablePhone;

	@FXML
	private TableColumn<Citizen, Date> TableDate;

	@FXML
	private TableColumn<Citizen, String> TableAddress;

	@FXML
	private Button ShowAddCitizen;

	@FXML
	private Button ShowDeleteCitizen;

	@FXML
	private Button ShowSearchCitizen;

	@FXML
	private Button ShowModifyCitizen;

	@FXML
	private TextField LastName;

	@FXML
	private TextField ID;

	@FXML
	private TextField Address;

	@FXML
	private DatePicker DOB;

	@FXML
	private Button BackButton;

	@FXML
	private TextField FirstName;

	@FXML
	private TextField PhoneNumber;

	@FXML
	private Button ModifyCitizen;

	@FXML
	private Button SearchCitizen;

	@FXML
	private Button AddCitizen;

	@FXML
	private Button DeleteCitizen;

	@FXML
	private Label SearchLabel;

	private ArrayList<Citizen> citizens;

	private ObservableList<Citizen> citizenList;

	private String dbUsername = "root";
	private String dbPassword = "1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "DatabaseProject";
	private Connection con;

	public CitizenController() throws Exception {
		con = DBConn.getInstances(URL, port, dbName, dbUsername, dbPassword);
	}

	@Override
	public void initialize(java.net.URL location, ResourceBundle resources) {
		LastName.setVisible(false);
		ID.setVisible(false);
		Address.setVisible(false);
		DOB.setVisible(false);
		FirstName.setVisible(false);
		PhoneNumber.setVisible(false);
		ModifyCitizen.setVisible(false);
		SearchCitizen.setVisible(false);
		AddCitizen.setVisible(false);
		DeleteCitizen.setVisible(false);
		BackButton.setVisible(true);
		SearchLabel.setVisible(false);
		citizens = new ArrayList<>();
		try {
			getData();
			citizenList = FXCollections.observableArrayList(citizens);

		} catch (ClassNotFoundException e) {
			Alert("problem getting data ");
		} catch (SQLException e) {
			Alert("problem getting data ");
		}

		// Setting up columns in the table
		TableID.setCellValueFactory(new PropertyValueFactory<Citizen, Integer>("ID"));
		TablePhone.setCellValueFactory(new PropertyValueFactory<Citizen, Integer>("phoneNumber"));
		TableDate.setCellValueFactory(new PropertyValueFactory<Citizen, Date>("date"));
		TableAddress.setCellValueFactory(new PropertyValueFactory<Citizen, String>("address"));
		TableName.setCellValueFactory(new PropertyValueFactory<Citizen, String>("FullName"));
		CitizenTable.setItems(citizenList);

	}

	void updateTable() {
		citizens = new ArrayList<>();
		try {
			getData();
			citizenList = FXCollections.observableArrayList(citizens);

		} catch (ClassNotFoundException e) {
			Alert("problem getting data ");
		} catch (SQLException e) {
			Alert("problem getting data ");
		}

		// Setting up columns in the table
		TableID.setCellValueFactory(new PropertyValueFactory<Citizen, Integer>("ID"));
		TablePhone.setCellValueFactory(new PropertyValueFactory<Citizen, Integer>("phoneNumber"));
		TableDate.setCellValueFactory(new PropertyValueFactory<Citizen, Date>("date"));
		TableAddress.setCellValueFactory(new PropertyValueFactory<Citizen, String>("address"));
		TableName.setCellValueFactory(new PropertyValueFactory<Citizen, String>("FullName"));
		CitizenTable.setItems(citizenList);
	}

	public void ExecuteStatement(String SQL) throws SQLException {

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

	private void getData() throws SQLException, ClassNotFoundException {

		String SQL;

		SQL = "select * from citizen";
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next())
			citizens.add(new Citizen(rs.getInt(2), // 2 is the order of lines in mysql
					rs.getString(3), rs.getString(4), rs.getInt(5), rs.getDate(6), rs.getString(7)));

		rs.close();
		stmt.close();

		// System.out.println(citizens);

	}

	private boolean ifIdExists(int cid) throws ClassNotFoundException, SQLException {

		String SQL = "Select 0 From citizen where citizen.IdentityNumber=" + cid + ";";
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
	void AddCitizen(ActionEvent event) throws ClassNotFoundException, SQLException {
		String firstName = FirstName.getText();
		String lastName = LastName.getText();
		String cid = ID.getText();
		String phone = PhoneNumber.getText();
		LocalDate dob = DOB.getValue();
		String address = Address.getText();
		if (firstName == null || lastName == null || cid == null || phone == null || dob == null || address == null
				|| !firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+") || !cid.matches("[0-9]+")
				|| !phone.matches("[0-9]+")) {
			Alert("Make sure your entries are correct");
		} else if ((ifIdExists(Integer.parseInt(cid)) == true)) {
			Alert("This Id already exists. Please try again");
		} else {
			Citizen x = new Citizen(Integer.parseInt(cid), firstName, lastName, Integer.parseInt(phone),
					Date.valueOf(dob), address);
			citizenList.add(x);
			FirstName.clear();
			LastName.clear();
			ID.clear();
			PhoneNumber.clear();
			DOB.setValue(null);
			Address.clear();
			insertData(x);
			CitizenTable.setItems(citizenList);

		}
		System.out.println(citizenList);
	}

	private void insertData(Citizen cit) {

		try {
			System.out.println("Insert into citizen (IdentityNumber,FName,LName,Phone,DOB,Address) values("
					+ cit.getID() + ",'" + cit.getFirstName() + "','" + cit.getLastName() + "'," + cit.getPhoneNumber()
					+ ",'" + cit.getDate() + "','" + cit.getAddress() + "')");
			ExecuteStatement("Insert into citizen (IdentityNumber,FName,LName,Phone,DOB,Address) values(" + cit.getID()
			+ ",'" + cit.getFirstName() + "','" + cit.getLastName() + "'," + cit.getPhoneNumber() + ",'"
			+ cit.getDate() + "','" + cit.getAddress() + "')");

		} catch (Exception e) {
			Alert("something wrong with inserting data");
		}
	}

	@FXML
	void DeleteCitizen(ActionEvent event) throws NumberFormatException, ClassNotFoundException, SQLException {
		String cid = ID.getText();
		if (cid == null || !cid.matches("[0-9]+")) {
			Alert("Please enter the correct id");
		} else if ((ifIdExists(Integer.parseInt(cid)) == false)) {
			Alert("The following ID does not exist. Please try again");
		} else {
			try {
				System.out.println("DELETE FROM citizen WHERE IdentityNumber=" + cid + ";");
				ExecuteStatement("DELETE FROM citizen WHERE IdentityNumber=" + cid + ";");

			}

			catch (Exception e) {
				System.out.println("something is wrong with excuting the statement");

			}

			updateTable();
		}

	}

	@FXML
	void ModifyCitizen(ActionEvent event) throws Exception {
		String firstName = FirstName.getText();
		String lastName = LastName.getText();
		String cid = ID.getText();
		String phone = PhoneNumber.getText();
		LocalDate dob = DOB.getValue();
		String address = Address.getText();
		//StringUtils.isNullOrEmpty(firstName);

		if (firstName == null & lastName == null & cid == null & phone == null & dob == null & address == null
				|| !cid.matches("[0-9]+")) {
			Alert("Make sure your entries are correct");
		}
		// checking if ID exists
		else 
		{
			if ((ifIdExists(Integer.parseInt(cid)) == false)) 
			{
				Alert("This Id doesn't exist. Please try again");
			}
			else 
			{
				String updateSqlQuery = " UPDATE citizen \n SET ";
				boolean flag=false;
				if (!StringUtils.isNullOrEmpty(address)) 
				{
					updateSqlQuery = updateSqlQuery + "Address='" + address + "'\n,";
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

				updateSqlQuery = updateSqlQuery + " WHERE IdentityNumber= " + cid;
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
		ID.clear();
		PhoneNumber.clear();
		DOB.setValue(null);
		Address.clear();
	}


	@FXML
	void SearchCitizen(ActionEvent event) throws NumberFormatException, ClassNotFoundException, SQLException {
		SearchLabel.setVisible(true);

		String cid = ID.getText();

		if (cid == null || !cid.matches("[0-9]+")) {
			Alert("Please enter the correct id");
		} else if ((ifIdExists(Integer.parseInt(cid)) == false)) {
			Alert("The following ID does not exist. Please try again");
		} else {
			String SQL = "Select c.IdentityNumber, c.FName,c.LName,c.Phone,c.DOB,c.Address "
					+ "From citizen c where IdentityNumber=" + cid + ";";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);

			while (rs.next()) {
				SearchLabel.setText("ID=" + rs.getInt(1) + "\n" + "Full Name=" + rs.getString(2) + " " + rs.getString(3)
				+ "\n" + "Phone Number=" + rs.getInt(4) + "\n" + "DOB=" + rs.getDate(5) + "\n" + "Address="
				+ rs.getString(6));
				SearchLabel.setFont(Font.font("Cambria", 20));
				SearchLabel.setTextFill(Color.web("#F0FFFF"));
				SearchLabel.setStyle("-fx-font-weight: bold;");
			}

			rs.close();
			stmt.close();
		}

	}

	@FXML
	void ShowAddCitizen(ActionEvent event) {
		LastName.setVisible(true);
		ID.setVisible(true);
		Address.setVisible(true);
		DOB.setVisible(true);
		FirstName.setVisible(true);
		PhoneNumber.setVisible(true);
		ModifyCitizen.setVisible(false);
		SearchCitizen.setVisible(false);
		AddCitizen.setVisible(true);
		DeleteCitizen.setVisible(false);
		BackButton.setVisible(true);

	}

	@FXML
	void ShowDeleteCitizen(ActionEvent event) {
		LastName.setVisible(false);
		ID.setVisible(true);
		Address.setVisible(false);
		DOB.setVisible(false);
		BackButton.setVisible(false);
		FirstName.setVisible(false);
		PhoneNumber.setVisible(false);
		ModifyCitizen.setVisible(false);
		SearchCitizen.setVisible(false);
		AddCitizen.setVisible(false);
		DeleteCitizen.setVisible(true);
		BackButton.setVisible(true);
	}

	@FXML
	void ShowModifyCitizen(ActionEvent event) {
		LastName.setVisible(true);
		ID.setVisible(true);
		Address.setVisible(true);
		DOB.setVisible(true);
		FirstName.setVisible(true);
		PhoneNumber.setVisible(true);
		ModifyCitizen.setVisible(true);
		SearchCitizen.setVisible(false);
		AddCitizen.setVisible(false);
		DeleteCitizen.setVisible(false);
		BackButton.setVisible(true);
	}

	@FXML
	void ShowSearchCitizen(ActionEvent event) {
		LastName.setVisible(false);
		ID.setVisible(true);
		Address.setVisible(false);
		DOB.setVisible(false);
		BackButton.setVisible(false);
		FirstName.setVisible(false);
		PhoneNumber.setVisible(false);
		ModifyCitizen.setVisible(false);
		SearchCitizen.setVisible(true);
		AddCitizen.setVisible(false);
		DeleteCitizen.setVisible(false);
		BackButton.setVisible(true);

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
	void GoBack(ActionEvent event) throws IOException {
		Stage primaryStage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Main");
		primaryStage.show();
	}

}
