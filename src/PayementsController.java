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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PayementsController implements Initializable{

	@FXML
	private TextField CitizenID;

	@FXML
	private Button AddFund;

	@FXML
	private Button addCitizenPayment;

	@FXML
	private Button ShowAddNewFund;

	@FXML
	private Button ShowPayForService;

	@FXML
	private TextField IBAN;

	@FXML
	private ComboBox<String> Services;

	@FXML
	private TextField Amount;

	@FXML
	private DatePicker DateOfPayement;

	private String dbUsername = "root";
	private String dbPassword = "1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "DatabaseProject";
	private Connection con;


	private ObservableList<String> ComboBoxList;

	public PayementsController() throws Exception 
	{
		con = DBConn.getInstances(URL, port, dbName, dbUsername, dbPassword);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ComboBoxList = FXCollections.observableArrayList("Graves","Garbage");
		CitizenID.setVisible(false);
		AddFund.setVisible(false);
		addCitizenPayment.setVisible(false);
		ShowAddNewFund.setVisible(true);
		ShowPayForService.setVisible(true);
		IBAN.setVisible(false);
		Services.setVisible(false);
		Amount.setVisible(false);
		DateOfPayement.setVisible(false);
		DateOfPayement.setEditable(false);

		Services.setItems(ComboBoxList);


	}

	@FXML
	void AddFund(ActionEvent event) {
		String IBANNumber= IBAN.getText();
		String amount=Amount.getText();
		LocalDate DOP=DateOfPayement.getValue();

		if(IBANNumber==null||amount==null||DOP==null||!amount.matches("[0-9]+")) {
			Alert("Make sure your entries are correct");
		}
		else
		{
			try {
				System.out.println("Insert into transaction (ServiceType,Amount,CreatedDateTime) values('payment',"+
						amount +",'"+ DOP+ "');");
				ExecuteStatement("Insert into transaction (ServiceType,Amount,CreatedDateTime) values('payment',"+
						amount +",'"+ DOP+ "');");


				ExecuteStatement("SET @TId := (select  TId from transaction order by TId desc LIMIT 1);");


				System.out.println("Insert into fund(IBAN,TransactionID) values ('"+IBANNumber+"',@TId);");

				ExecuteStatement("Insert into fund(IBAN,TransactionID) values ('"+IBANNumber+"',@TId);");

			} catch (Exception e) {
				Alert("something wrong with inserting data");
			}

			IBAN.clear();
			Amount.clear();
			DateOfPayement.setValue(null);

		}

	}


	@FXML
	void ShowAddNewFund(ActionEvent event) {
		CitizenID.setVisible(false);
		AddFund.setVisible(true);
		addCitizenPayment.setVisible(false);
		IBAN.setVisible(true);
		Services.setVisible(false);
		Amount.setVisible(true);
		DateOfPayement.setVisible(true);
	}

	@FXML
	void ShowPayForService(ActionEvent event) {
		CitizenID.setVisible(true);
		AddFund.setVisible(false);
		addCitizenPayment.setVisible(true);
		IBAN.setVisible(false);
		Services.setVisible(true);
		Amount.setVisible(true);
		DateOfPayement.setVisible(true);
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
	void addCitizenPayment(ActionEvent event)  {
		String CID=CitizenID.getText();
		String amount=Amount.getText();
		LocalDate dateOfPayement=DateOfPayement.getValue();
		String service=Services.getValue();

		if(CID==null||amount==null||dateOfPayement==null||service==null||!CID.matches("[0-9]+")||
				!amount.matches("[0-9]+")) 
		{
			Alert("Make sure your entries are correct");
		} else
			try {
				if ((ifIdExists(Integer.parseInt(CID)) == false) )
				{
					Alert("This Id doesn't exist. Please try again");
				}
				else 
				{

					if(service=="Graves")
					{
						try {
							ExecuteStatement("Insert into transaction (ServiceType,Amount,CreatedDateTime) values('graves',"+
									amount +",'"+ dateOfPayement+ "');");

							System.out.println("SET @TId := (select  TId from transaction order by TId desc LIMIT 1);");
							ExecuteStatement("SET @TId := (select  TId from transaction order by TId desc LIMIT 1);");

							ExecuteStatement("SET @CID :=(Select CId From citizen where IdentityNumber="+CID+");" );

							System.out.println("Insert into transactionCitizenAssociation (TransactionId,CitizenId)  values(@TId,@CID);");
							ExecuteStatement("Insert into transactionCitizenAssociation (TransactionId,CitizenId)  values(@TId,@CID);");

						} catch (SQLException e) {
							Alert("something wrong with citizen's payement");
						}

					}
					else if(service=="Garbage")
					{
						try {
							ExecuteStatement("Insert into transaction (ServiceType,Amount,CreatedDateTime) values('garbage',"+
									amount +",'"+ dateOfPayement+ "');");

							System.out.println("SET @TId := (select  TId from transaction order by TId desc LIMIT 1);");
							ExecuteStatement("SET @TId := (select  TId from transaction order by TId desc LIMIT 1);");

							ExecuteStatement("SET @CID :=(Select CId From citizen where IdentityNumber="+CID+");" );

							System.out.println("Insert into transactionCitizenAssociation (TransactionId,CitizenId)  values(@TId,@CID);");
							ExecuteStatement("Insert into transactionCitizenAssociation (TransactionId,CitizenId)  values(@TId,@CID);");

						} catch (SQLException e) {
							Alert("something wrong with citizen's payement");
						}

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
	void GoBack(ActionEvent event) throws IOException {
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

		} catch (SQLException s) {
			s.printStackTrace();
			System.out.println("SQL statement is not executed!");

		} catch (Exception e) {
			System.out.println("execute" + e.getMessage());
		}

	}






}
