package finalProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Register extends Application {
	
	//main scene
	Scene scene;
	BorderPane root;
	GridPane gp;
	
	//Labels and textfield
	Label registerTitle, usernameLbl, emailLbl, passwordLbl, confirmPassLbl, phoneLabel, addressLbl, genderLbl, haveAccount;
	TextField usernameField, emailField, phoneField;
	PasswordField passwordField, confirmPassField;
	TextArea addressField;
	Hyperlink loginLink;
	
	//CheckBox 
	CheckBox tnc;
	
	//button
	RadioButton maleRB,femaleRB;
	ToggleGroup genderToggleGroup;
	Button registerBtn;
	
	public void initialize() {
		root = new BorderPane();
		gp = new GridPane();
		
		//initalize labels
		registerTitle = new Label ("Register"); registerTitle.setStyle("-fx-font-size:30; -fx-font-weight: bold;");
		usernameLbl = new Label("Username : ");
		emailLbl = new Label("Email : ");
		passwordLbl = new Label("Password : ");
		confirmPassLbl = new Label("Confirm password : ");
		phoneLabel = new Label("Phone number : ");
		addressLbl = new Label("Address : ");
		genderLbl = new Label("Gender : ");
		
		//initalize fields and contents
		usernameField = new TextField(); usernameField.setPromptText("input username...");
		emailField = new TextField(); emailField.setPromptText("input email...");
		passwordField = new PasswordField(); passwordField.setPromptText("input password...");
		confirmPassField = new PasswordField(); confirmPassField.setPromptText("input confirm password...");
		phoneField = new TextField(); phoneField.setPromptText("input phone number...");
		addressField = new TextArea(); addressField.setPromptText("input address...");
		
		//initialize radio buttons
		genderToggleGroup = new ToggleGroup();
		maleRB = new RadioButton("Male"); maleRB.setToggleGroup(genderToggleGroup);
		femaleRB = new RadioButton("Female"); femaleRB.setToggleGroup(genderToggleGroup); //1 choice at a time
		
		tnc = new CheckBox("I agree to all terms and condition");
		haveAccount = new Label("Have an account? ");
		loginLink = new Hyperlink("login here");
		
		registerBtn = new Button("Register");
		
		
	}
	
	public void set() {
		root.setCenter(gp);
		
		//add all label
		gp.add(registerTitle, 1, 0); //title column 1
		gp.add(usernameLbl, 0, 1);
		gp.add(emailLbl, 0, 2);
		gp.add(passwordLbl, 0, 3);
		gp.add(confirmPassLbl, 0, 4);
		gp.add(phoneLabel, 0, 5);
		gp.add(addressLbl, 0, 6);
		gp.add(genderLbl, 0, 7);
		
		//set column span
		GridPane.setColumnSpan(usernameField, 3);
		GridPane.setColumnSpan(emailField, 3);
		GridPane.setColumnSpan(passwordField, 3);
		GridPane.setColumnSpan(confirmPassField, 3);
		GridPane.setColumnSpan(phoneField, 3);
		GridPane.setColumnSpan(addressField, 3);
		GridPane.setColumnSpan(tnc, 3);
		GridPane.setColumnSpan(haveAccount, 3);
		GridPane.setColumnSpan(registerBtn, 3);
		
		//add fields
		gp.add(usernameField, 1, 1);
		gp.add(emailField, 1, 2);
		gp.add(passwordField, 1, 3);
		gp.add(confirmPassField, 1, 4);
		gp.add(phoneField, 1, 5);
		gp.add(addressField, 1, 6);
		gp.add(maleRB, 1, 7);
		gp.add(femaleRB, 2, 7);
		gp.add(tnc, 1, 8);
		gp.add(haveAccount, 1, 9);
		gp.add(loginLink, 2, 9);
		gp.add(registerBtn, 1, 10);	
	}
	
	public void position() {
		//center the gp
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(10);
		gp.setVgap(10);
	}
	
	public void eventHandler() {
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setHeaderText("Failed to Register");
		loginLink.setOnAction(e -> {
			switchToLoginScene();
		});
		
		registerBtn.setOnAction(e -> {
			//generate userID, assign to customer not done
			if (usernameField.getText().isEmpty() || usernameField.getText().isBlank() ) {
				errorAlert.setContentText("Username must be filled");
				errorAlert.showAndWait();
			}else if (isUsernameExist()) {
				errorAlert.setContentText("Username has been used, try another username");
				errorAlert.showAndWait();
			}else if (!emailField.getText().endsWith("@gmail.com") || emailField.getText().isEmpty()) {
				errorAlert.setContentText("Email must be filled and ends with '@gmail.com' ");
				errorAlert.show();
			}else if (passwordField.getText().length() < 5 || passwordField.getText().length() > 20 ||
					(!isValidPassword(passwordField.getText()))) {
				errorAlert.setContentText("Password must be alphanumeric between 5-20 characters");
				errorAlert.showAndWait();
			}else if (passwordField.getText().isEmpty()){
				errorAlert.setContentText("Password field must be filled");
				errorAlert.showAndWait();
			}else if (!confirmPassField.getText().equals(passwordField.getText())) {
				errorAlert.setContentText("Confirm password does not match password");
				errorAlert.showAndWait();
			}else if (phoneField.getText().isEmpty()) {
				errorAlert.setContentText("Phone number must be filled");
				errorAlert.showAndWait();
			}else if (!isNumeric(phoneField.getText())) {
				errorAlert.setContentText("Phone number must contain only numeric characters");
	            errorAlert.showAndWait();
			}else if (addressField.getText().isEmpty()) {
				errorAlert.setContentText("Address must be filled");
				errorAlert.showAndWait();
			} else if (genderToggleGroup.getSelectedToggle() == null) {
	            errorAlert.setContentText("Gender must be selected");
	            errorAlert.showAndWait();
	        } else if (!tnc.isSelected()) {
	        	errorAlert.setContentText("You must agree to the terms and condition");
	        	errorAlert.showAndWait();
	        } else {
        		//panggil metod insert user yg udh dibikin di class databaseConnection
	        		try {
						insertUser(
								usernameField.getText(), //username
								passwordField.getText(), //password
								addressField.getText(), //address
								phoneField.getText(),
								maleRB.isSelected() ? "Male" : "Female" 
									//kalo male selected, maleRB.isSelected() jadi true, klo ga jdi female
							);
						
						Alert registSuccessAlert = new Alert(AlertType.CONFIRMATION);
		        		registSuccessAlert.setHeaderText("Success");
		        		registSuccessAlert.setContentText("Registered Successfully!");
		        		registSuccessAlert.showAndWait();
		        		switchToLoginScene(); //arahin ke login scene
		        		
					} catch (SQLException e1) {
						// kalo misalkan database error(disini hrs di try catch, kyk fungsi klo mo pindah page)
						e1.printStackTrace();
						
						Alert dbErrorAlert = new Alert(AlertType.ERROR); //ini asumsi sbnrnya, alert klo misalkan database eror
						dbErrorAlert.setHeaderText("Database error");
						dbErrorAlert.setContentText("Failed to register.");
						dbErrorAlert.showAndWait();
					}	
	        }
		});
	}
	
	private boolean isValidPassword(String password) {
	    return password.matches("^[a-zA-Z0-9]{5,20}$"); //ini format utk campuran alphanumeric
	} //5,20 mksdnya 5-20 characters, a-z ya represent karakter, 
	
	private boolean isNumeric(String input) {
	    return input.matches("\\d+");
	}
	
	private void switchToLoginScene() {
		Login login = new Login();
		Stage stage = (Stage) registerBtn.getScene().getWindow();
		   try {
		        login.start(stage);
		    } catch (Exception e2) {
		        e2.printStackTrace();
		    }		
	}
	
	private boolean isUsernameExist() {
		int jumlah = 0;
		
		try (Connection con = DatabaseConnection.getConnection()) {
	        String query = "SELECT COUNT(*) AS count FROM user WHERE username = ?"; //COUNT gunanya utk itung ada brp yg match dgn kondisi WHERE
	        
	        try (PreparedStatement ps = con.prepareStatement(query)) {
	            ps.setString(1, usernameField.getText());
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    jumlah = rs.getInt("count");                    
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		if (jumlah > 0) {
			return true;
		}else {
			return false;
		}
	}
	
    //bikin constructor bwt mskin inputan dari kelas register. kecuali userID dan role(bkn inputan user, generate sistem)
    public static void insertUser(String username, String password, String address, String phone_num, String gender) throws SQLException {
    	
    	try (Connection connection = DatabaseConnection.getConnection()) {
    		String userID = generateUserID();
    		String role = "User";
    		
    		String query = "INSERT INTO user (userID, username, password, role, address, phone_num, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
    		
    		
    		//masukin data variabel java ke format query yg udah dibikin diatas
    		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    			preparedStatement.setString(1, userID); //nomor merepresentasikan urutan placeholder '?'
    			preparedStatement.setString(2, username);
    			preparedStatement.setString(3, password);
    			preparedStatement.setString(4, role);
    			preparedStatement.setString(5, address);
    			preparedStatement.setString(6, phone_num);
    			preparedStatement.setString(7, gender);
    		//execute query diatas diatas
    			preparedStatement.executeUpdate();
    		}	
    	}
    }
    
    private static String generateUserID() {
    	Random random = new Random();
        int randomValue = random.nextInt(999); // bikin integer antara 0 - 999
        return "CU" + String.format("%d", randomValue); //ini untuk generate userID ssat user
    }
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Login");
		initialize();
		set();
		position();
		eventHandler();
		
		scene = new Scene(root,800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
	}

}
