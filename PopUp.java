package finalProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopUp extends Application {
    Scene scene;
    BorderPane root;
    VBox contentVbox;
    HBox buttonHbox;
    Label titleLbl, questionLbl;
    Button yesBtn, noBtn;
    
    ObservableList<CartForTD> cartListTemporer = FXCollections.observableArrayList();
 

    public void initiate() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #89abc0; -fx-font-size: 16");
        
        contentVbox = new VBox();
        contentVbox.setStyle("-fx-background-color: #6d96ae");
        
        titleLbl = new Label("Order Confirmation");
        titleLbl.setStyle("-fx-background-color: #424547; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-font-size: 16");
        //pake html color picker, hex code
        
        questionLbl = new Label("Are you sure you want to make purchase?"); //nanti pake vbox
        
        buttonHbox = new HBox();
        yesBtn = new Button("Yes"); yesBtn.setOnAction(e -> yesBtnEventHandler());
        noBtn = new Button("No"); noBtn.setOnAction(e -> noBtnHandler());
    }

    public void set() {
        // Add content to the popup
    	root.setTop(titleLbl);
    	
    	root.setCenter(contentVbox);
    	
    	buttonHbox.getChildren().addAll(yesBtn,noBtn);
    	contentVbox.getChildren().addAll(questionLbl, buttonHbox);
        
    }

    public void position() {
        titleLbl.setPrefWidth(400);
        titleLbl.setAlignment(Pos.CENTER);
        
        //rapihin button nya
        yesBtn.setPrefSize(120, 35);
        noBtn.setPrefSize(120, 35);
        //rapihin jarak
        buttonHbox.setSpacing(10);
        
        buttonHbox.setAlignment(Pos.CENTER);
        contentVbox.setAlignment(Pos.CENTER); 
    }
    
    private void yesBtnEventHandler () {    	
    	
    	if (!isCartEmpty()) {
    		moveCartToTransaction();
    		removeAllFromCart();
    		
    		Alert successTransactionAlert = new Alert(AlertType.CONFIRMATION);
    		successTransactionAlert.setHeaderText("Successfully Purchased");
    		  successTransactionAlert.setOnShown(e -> {
    	            Stage stage = (Stage) successTransactionAlert.getDialogPane().getScene().getWindow();
    	            stage.close(); //tutup popup   	     
    	        });
    		successTransactionAlert.showAndWait();
    	    } else {
    	        Alert failedTransactionAlert = new Alert(AlertType.ERROR);
    	        failedTransactionAlert.setHeaderText("Failed to Make Transaction");
    	        failedTransactionAlert.showAndWait();
    	    }
    }
    
    private void noBtnHandler () {
    	Stage stage = (Stage) noBtn.getScene().getWindow();
        stage.close();
    }
    
    private boolean isCartEmpty() {
    	int jumlah = 0;
    	
    	try (Connection con = DatabaseConnection.getConnection()) {
    		String query = "SELECT COUNT(*) AS count FROM cart c JOIN user u WHERE u.username = ?";
    		
    		try (PreparedStatement ps = con.prepareStatement(query)) {
    			ps.setString(1, Login.getUsername());
    			
    			try(ResultSet rs = ps.executeQuery()) {
    				if (rs.next()) {
    					jumlah = rs.getInt("count");
    				}
    			}
    		}
    	}catch (SQLException e) {
    		e.printStackTrace();
    	}
    		
    	if (jumlah < 1 ) {
    		return true;	
    	}else {
    		return false;
    	}
    }
    
    private void loadCart() {
    	try (Connection con = DatabaseConnection.getConnection()) {
    		String query = "SELECT productID, quantity FROM CART WHERE userID = ?";
    		
    		try (PreparedStatement ps = con.prepareStatement(query)) {
				ps.setString(1, getUserID());
				ResultSet rs = ps.executeQuery();
				
				
				
				//while rs masih ada next, dia akan trs loop
				while(rs.next()) {					
					String productID = rs.getString("productID");
					int quantity = rs.getInt("quantity"); //ambil kolom quantity
					CartForTD tempCartForTD = new CartForTD(productID, quantity);
					cartListTemporer.add(tempCartForTD);
    	}
    		}catch (SQLException e) {
			// TODO: handle exception
    		e.printStackTrace();
    		}
    	}catch (SQLException e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    }
   
    
    
    private void moveCartToTransaction() {
        try (Connection con = DatabaseConnection.getConnection()) {
            String transactionID = generateTransactionID();
            String insertHeaderQuery = "INSERT INTO transaction_header (transactionID, userID) VALUES (?, ?)";

            try (PreparedStatement headerPs = con.prepareStatement(insertHeaderQuery)) {
                headerPs.setString(1, transactionID);
                headerPs.setString(2, getUserID());
                headerPs.executeUpdate();

                String insertDetailQuery = "INSERT INTO transaction_detail (transactionID, productID, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement detailPs = con.prepareStatement(insertDetailQuery)) {
                    for (CartForTD cartItem : cartListTemporer) {
                        detailPs.setString(1, transactionID);
                        detailPs.setString(2, cartItem.getProductID());
                        detailPs.setInt(3, cartItem.getQuantity());
                        detailPs.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void removeAllFromCart() {
    	try (Connection con = DatabaseConnection.getConnection()) {
    		String deleteCartQuery = "DELETE FROM cart WHERE userID = ?";
    		
    		try(PreparedStatement ps = con.prepareStatement(deleteCartQuery)) {
    			ps.setString(1, getUserID());
    			ps.executeUpdate();
    		}
    		
    	}catch (SQLException e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    }
    
    private static String generateTransactionID () {
    	Random random = new Random();
    	int randomValue = random.nextInt(999); //bikin integer antara 0-999
    	return "TR" + String.format("%d", randomValue);
    }
    
    private String getUserID () {
		String userID = null;
		
		try (Connection con = DatabaseConnection.getConnection()) {
			String query = "SELECT userID FROM user WHERE username = ?";
			
			try(PreparedStatement ps = con.prepareStatement(query)) {
				ps.setString(1, Login.getUsername());
				try(ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						userID = rs.getString("userID");
					}
				}
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return userID;
	}
    
 

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	
        initiate();
        set();
        position();

        //bikin stage untuk si popupnya
        Stage popupStage = new Stage();

        popupStage.setTitle("Order Confirmation");

        //modality ngeblokir supaya user gbs ke window lain dan harus interact sama popUp yang ada
        popupStage.initModality(Modality.APPLICATION_MODAL);

        //bikin yang punya popup si primary stage yg sblmnya yaitu si CartPage
        popupStage.initOwner(primaryStage);

        scene = new Scene(root, 400, 300);
        popupStage.setScene(scene);

        // Show the popup stage
        popupStage.showAndWait();  // Use showAndWait to wait for the user to close the popup
    }
}
