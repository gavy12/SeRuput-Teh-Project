package finalProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Login extends Application{

	//main scene
	Scene scene;
	BorderPane root;
	GridPane gp;
	
	
	//labels and textfields
	Label loginTitle, usernameLbl, passwordLbl, registerText;
	TextField usernameField, passwordField;
	Hyperlink registerLink;
	
	//variable static utk store username, dipake buat welcomeLbl di homePage
	private static String username;
	
	//method utk get username
	public static String getUsername() {
		return username;
	}
	
	//button
	Button loginButton;
	
	
	
	public void initiate() {
		root = new BorderPane();
		gp = new GridPane();
		
		
		//initalize labels
		loginTitle = new Label ("Login");
		loginTitle.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");
		usernameLbl = new Label ("Username : "); 
		passwordLbl = new Label ("Password : "); 
		registerText = new Label("Don't have an account yet? ");
		
		//initialize fields, make it appear longer
		usernameField = new TextField(); usernameField.setPrefWidth(240);
		usernameField.setPromptText("Input username...");
		passwordField = new PasswordField(); passwordField.setPrefWidth(240);
		passwordField.setPromptText("Input password..."); //set bg text
		
		registerLink = new Hyperlink("register here"); registerLink.setTextFill(Color.BLUE);
				
		//initialize buttons
		loginButton = new Button("Login"); loginButton.setPrefWidth(150);
	}
	
	public void set() {
	    root.setCenter(gp);

	    gp.setPadding(new Insets(20, 20, 20, 20));
	    gp.add(loginTitle, 0, 0);
	    gp.add(usernameLbl, 0, 1);
	    gp.add(passwordLbl, 0, 2);
	    
	    //set the TextField supaya take up 2 columns, appear longer
	    GridPane.setColumnSpan(usernameField, 2);
	    GridPane.setColumnSpan(passwordField, 2);
	    
	    gp.add(usernameField, 1, 1);
	    gp.add(passwordField, 1, 2);
	    gp.add(registerText, 1, 3);
	    gp.add(registerLink, 2, 3);
	    gp.add(loginButton, 1, 4);

	    GridPane.setColumnIndex(loginTitle, 1);
	}
	
	public void position() {
	    // Center the GridPane in the BorderPane
	    gp.setAlignment(Pos.CENTER);

	    gp.setHgap(10);
	    gp.setVgap(10);
	    
	}

	public void setEventHandler() {
		registerLink.setOnAction(e -> {
			try {
				switchToRegisterScene ();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		loginButton.setOnAction(e -> {
			if (usernameField.getText().isEmpty() || usernameField.getText().isBlank() || 
					passwordField.getText().isBlank() || passwordField.getText().isEmpty()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Failed to Login");
				alert.setContentText("All fields must be filled");
				alert.show();
			}else {
				username = usernameField.getText();
				String password = passwordField.getText();
				
			//if yang ini untuk cek apakah data username dan password valid
				if (isValidUser(username, password)) {
				//ambil user role dari database, pake method yang udah dibikin dibawah
					String role = getUserRole(username);
					
				//if yang di dalem ini untuk cek apakah role == Admin OR role == User
					if ("Admin".equalsIgnoreCase(role)) {
						switchToAdminHome();
					}else {
						switchToCustomerHome();
					}
				}else {
					Alert invalidCredentialAlert = new Alert(AlertType.ERROR);
					invalidCredentialAlert.setHeaderText("Failed to Login");
					invalidCredentialAlert.setContentText("Invalid credential");
					invalidCredentialAlert.showAndWait();
				}
			}
		});
	}
	
	private void switchToRegisterScene() throws Exception {
	    Register register = new Register();
	    
	    // Get the current stage from any control within the Login scene
	    Stage stage = (Stage) loginButton.getScene().getWindow();

	    // Set the Register scene on the stage
	    register.start(stage);
	}
	
	//bikin metod untuk cek valid user dari database
	private boolean isValidUser(String username, String password) {		
		try(Connection connection = DatabaseConnection.getConnection()) {
			String query = "SELECT * FROM user WHERE username = ? AND password = ?"; //tanda tanya itu placeholder
			
			//
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, password); //angka disini mengacu pada urutan '?' placeholder
				
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					return resultSet.next(); //return valuenya jadi true kalo misalkan ketemu di DB
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String getUserRole(String username) {
		String userRole = null;
		
		try(Connection connection = DatabaseConnection.getConnection()) {
			//ambil role dari sql pake preparedStatement
			String query = "SELECT role FROM user WHERE username = ?"; //? itu placeholder
			try (PreparedStatement ps = connection.prepareStatement(query)) {
				ps.setString(1, username); //1 mengacu pada placeholder tanda tanya diatas
				
			//eksekusi query lalu ambil hasilnya pakai ResultSet
				try (ResultSet resultSet = ps.executeQuery()) {
				//cek apakah hasilnya tersedia, next berati dia ke next row
					if(resultSet.next()) {
						//ambil role user dari set
						userRole = resultSet.getString("role");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(); //
		}
		
		return userRole;
	}
	

	private void switchToAdminHome() {
		//set username sblm msk ke adminHome
		
		AdminHome adminHome = new AdminHome();
		Stage stage = (Stage) loginButton.getScene().getWindow();
		try {
			adminHome.start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void switchToCustomerHome() {
		//set username sblm msk ke customerHome
		CustomerHome.setUsername(username);
		
		CustomerHome customerHome = new CustomerHome();
		Stage stage = (Stage) loginButton.getScene().getWindow(); //replace stage skrg dgn scene yg baru
		try {
			customerHome.start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Login");
	    initiate();
	    set();
	    position();
	    setEventHandler();

	    scene = new Scene(root, 800, 600);
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}

}