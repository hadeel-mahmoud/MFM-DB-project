import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ReportsController implements Initializable{
	@FXML
	private Button Back;

	@FXML
	private Button ExpensesReport;

	@FXML
	private Button RevenueReport;

	@FXML
	private DatePicker FromDate;
	
	@FXML
	private DatePicker ToDate;

	@FXML
	private Label Report;


	private String dbUsername = "root";
	private String dbPassword = "1234";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "DatabaseProject";
	private Connection con;

	public ReportsController() throws Exception 
	{
		con = DBConn.getInstances(URL, port, dbName, dbUsername, dbPassword);
	}

	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		Report.setVisible(false);
	}



	

	@FXML
	void RevenueReport(ActionEvent event) throws SQLException {
		LocalDate from = FromDate.getValue();
		LocalDate to=ToDate.getValue();
		String SQL;
		System.out.println("Select * from  transaction where ServiceType in ('payment','garbage','graves') AND CreatedDateTime >= '"+Date.valueOf(from)+"' AND CreatedDateTime <=  '"+Date.valueOf(to)+"';");

		SQL = "Select * from  transaction where ServiceType in ('payment','garbage','graves') AND CreatedDateTime >= '"+Date.valueOf(from)+"' AND CreatedDateTime <=  '"+Date.valueOf(to)+"';";
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);

		while (rs.next()) {
			Report.setText(rs.getInt(1)+rs.getString(2)+rs.getInt(3)+rs.getDate(4)+"\n");
			System.out.println(rs.getInt(1)+rs.getString(2)+rs.getInt(3)+rs.getDate(4)+"\n");
			}
			

		rs.close();
		stmt.close();
		
	}

	@FXML
	void ExpensesReport(ActionEvent event) {

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





}
