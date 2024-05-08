package finalProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PurchaseHistoryPage extends Application {
	Scene scene;
	BorderPane root;
	GridPane gp, gpPretelanKanan;
	MenuBar mb;
	Menu homeMenu, cartMenu, accountMenu;
	MenuItem homePageMItem, myCartMItem, logOutMItem, purchaseHistoryMItem;
	
	Label titleLbl, transactionIdLbl, usernameLbl, phoneNumberLbl, addressLbl, totalLbl;
	
	ListView<TransactionHeader> listViewTransactionHeader;
	ListView<TransactionDetail> listViewTransactionDetail;
	
	public void initiate() {
		root = new BorderPane();
		gp = new GridPane();
		gpPretelanKanan = new GridPane();
		initiateMenuBar();
		
		//title
		titleLbl = new Label(Login.getUsername() + "'s Cart"); //mulai execute dri class Login yaaa, biar ada namanya
		titleLbl.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");	
		
		//pretelan komponen di sebelah kanan
		transactionIdLbl = new Label();
		transactionIdLbl.setStyle("-fx-font-weight: bold;");
		
		usernameLbl = new Label("Username : " + Login.getUsername());
		phoneNumberLbl = new Label("Phone Number : " + getPhoneNumberFromDB()); //cmn bs klo user dh login, krn getName dri loginPage
		addressLbl = new Label("Address : " + getAddressFromDB()); //ini jg cmn bs klo user dh login
		totalLbl = new Label(); //ini blkgan dah, logic nya susah, hrs kaliin quantity sama product_price
		
		//bikin semua invisible dulu
		phoneNumberLbl.setVisible(false);
		addressLbl.setVisible(false);
		totalLbl.setVisible(false);
		
		//initiateListView
		listViewInitiate();
		listViewTransactionDetail.setVisible(false);
		
		
		
	}
	
	public void set() {
		root.setCenter(gp);
		setMenuBar();
		
		//set Title
		gp.add(titleLbl, 0, 0, 2, 1); //column span 2
		gp.add(listViewTransactionHeader, 0, 1);
		
		//masukin components ke gridpanePretelanKanan
		gpPretelanKanan.add(transactionIdLbl, 0, 0);
		gpPretelanKanan.add(usernameLbl, 0, 1);
		gpPretelanKanan.add(phoneNumberLbl, 0, 2);
		gpPretelanKanan.add(addressLbl, 0, 3);
		gpPretelanKanan.add(totalLbl, 0, 4);
		gpPretelanKanan.add(listViewTransactionDetail, 0, 5);
		
		//masukin gpPretelanKanan ke gp utama
		gp.add(gpPretelanKanan, 1, 1);
		
	}
	
	public void position() {
		gp.setVgap(5);
		gp.setHgap(5);
		gpPretelanKanan.setVgap(5);
		
		titleLbl.setPadding(new Insets(20));
		
		//naikin gpPretelanKanan
		gp.setValignment(gpPretelanKanan, VPos.TOP);
		
		//rapihin list view yg kiri
		Insets listViewMargin = new Insets(0, 20, 0, 20);
		GridPane.setMargin(listViewTransactionHeader, listViewMargin);
		GridPane.setValignment(listViewTransactionHeader, VPos.TOP);
		listViewTransactionHeader.setMaxHeight(300);
		listViewTransactionHeader.setMaxWidth(150);
		
		listViewTransactionDetail.setMaxSize(300, 150);
	}
	
	public void initiateMenuBar () {
		//menubar
		mb = new MenuBar();
			//initiate menu
				homeMenu = new Menu("Home");
				cartMenu = new Menu("Cart");
				accountMenu = new Menu("Account");
				
			//initiate menuItem (subMenu)
				homePageMItem = new MenuItem("HomePage");
				myCartMItem = new MenuItem("My Cart");
				logOutMItem = new MenuItem("Log out");
				purchaseHistoryMItem = new MenuItem("Purchase History");
	}
	
	public void setMenuBar() {
				mb.getMenus().addAll(homeMenu, cartMenu, accountMenu);
				homeMenu.getItems().addAll(homePageMItem);
				cartMenu.getItems().addAll(myCartMItem);
				accountMenu.getItems().addAll(logOutMItem, purchaseHistoryMItem);
				root.setTop(mb);
	}

	private void listViewInitiate() {
		listViewTransactionHeader = new ListView<>();
		listViewTransactionDetail = new ListView<>();
		
		listViewTransactionHeader.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		isiTransactionHeaderListView();
		isiTransactionDetailListView();
		listViewTransactionHeaderListener();
		
	}
	
	private String getPhoneNumberFromDB() {
	    String phoneNumber = null;

	    try (Connection con = DatabaseConnection.getConnection()) {
	        String query = "SELECT phone_num FROM user WHERE username = ?";

	        try (PreparedStatement ps = con.prepareStatement(query)) {
	            ps.setString(1, Login.getUsername());

	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    phoneNumber = rs.getString("phone_num");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return phoneNumber;
	}
	
	private String getAddressFromDB() {
	    String address = null;

	    try (Connection con = DatabaseConnection.getConnection()) {
	        String query = "SELECT address FROM user WHERE username = ?";

	        try (PreparedStatement ps = con.prepareStatement(query)) {
	            ps.setString(1, Login.getUsername());

	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    address = rs.getString("address");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return address;
	}

	private void isiTransactionHeaderListView() {
		try (Connection con = DatabaseConnection.getConnection()) {
			String query = "SELECT transactionID FROM transaction_header th WHERE th.userID = ?";
			
			try(PreparedStatement ps = con.prepareStatement(query)) {
				ps.setString(1, getUserID());
				ResultSet rs = ps.executeQuery();
				
				ObservableList<TransactionHeader> transactionHeaderList = FXCollections.observableArrayList();
				
				while(rs.next()) {
					String transactionID = rs.getString("transactionID");
					
					TransactionHeader tempTh = new TransactionHeader(transactionID);
					transactionHeaderList.add(tempTh);
				}
				
				if (transactionHeaderList.isEmpty()) {
					transactionIdLbl.setText("There's No History");
					usernameLbl.setText("Consider Purchasing Our Products");
					
				}else {
					transactionIdLbl.setText("Select a transaction to view details");
					transactionIdLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
				}
				listViewTransactionHeader.setItems(transactionHeaderList);
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void listViewTransactionHeaderListener() {
		listViewTransactionHeader.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			
			TransactionHeader selectedTH = listViewTransactionHeader.getSelectionModel().getSelectedItem();
			//utk ambil selection dari user, supaya nanti bs get info-info utk deskripsi di kanan
			
			if (newSelection != null) {
				//update label transactionId
				transactionIdLbl.setText("Transaction ID : " + selectedTH.getTransactionID());
				usernameLbl.setText("Username : " + Login.getUsername());
				usernameLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
				phoneNumberLbl.setText("Phone Number : " + getPhoneNumberFromDB());
				addressLbl.setText("Address : " + getAddressFromDB());
				totalLbl.setText("Total : ");
				listViewTransactionDetail.setVisible(true);
			} else {
				transactionIdLbl.setText("Select a transaction to view details");
				transactionIdLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
				usernameLbl.setVisible(false);
				phoneNumberLbl.setVisible(false);
				addressLbl.setVisible(false);
				totalLbl.setVisible(false);
			}
		});
	}
	
	private void isiTransactionDetailListView() {
		
		try (Connection con = DatabaseConnection.getConnection()) {
			String query = "SELECT product_name, quantity, product_price " +
							"FROM product p JOIN transaction_detail td " +
							"ON p.productID = td.productID " +
							"JOIN transaction_header th ON td.transactionID = th.transactionID " + 
							"WHERE th.userID = ?";
			
			try (PreparedStatement ps = con.prepareStatement(query)) {
				ps.setString(1, getUserID());
				ResultSet rs = ps.executeQuery();
				
				ObservableList<TransactionDetail> transactionDetailList = FXCollections.observableArrayList();
				
				while (rs.next()) {
					int tempQty = rs.getInt("quantity"); //nama kolom
					String tempProductName = rs.getString("product_name");
					int unitPrice = rs.getInt("product_price");
					
					int productTotalPrice = tempQty*unitPrice;
					
					TransactionDetail insertTd = new TransactionDetail(tempQty, tempProductName, productTotalPrice);
					transactionDetailList.add(insertTd);
				}
				int allTotal = transactionDetailList.stream().mapToInt(TransactionDetail::getProductTotalPrice).sum();
				totalLbl.setText("Total : Rp." + allTotal);
				listViewTransactionDetail.setItems(transactionDetailList);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String getUserID () {
		String userID = null;
		
		try (Connection con = DatabaseConnection.getConnection()) {
			String query = "SELECT userID FROM user WHERE username = ?";
			
			try(PreparedStatement ps = con.prepareStatement(query)) {
				ps.setString(1, Login.getUsername()); //harus mulai dri login page dulu
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
	
 	private void directToHomePage(Stage currentStage) {
	    CustomerHome ch = new CustomerHome();
	    
	    try {
	    	ch.start(currentStage);
	    }catch (Exception e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    }
	}
	
	private void directToCart(Stage currentStage) {
		CartPage cart = new CartPage();
		try {
			cart.start(currentStage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void directToLogin(Stage currentStage) {
		Login login = new Login();
		
		try {
			login.start(currentStage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void directToPurchaseHistory(Stage currentStage) {
		PurchaseHistoryPage ph = new PurchaseHistoryPage();
		
		try {
			ph.start(currentStage); //nti parameternya yg msk disini itu primaryStage utk replace primaryStagenya
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("History");
		// TODO Auto-generated method stub
		initiate();
		set();
		position();
		
		 scene = new Scene(root, 800, 600);
		    primaryStage.setScene(scene);
		    primaryStage.show();
		    
			//MenuItem event handler utk pindah page
			homePageMItem.setOnAction(e -> directToHomePage(primaryStage)); //mskin primaryStagenya ke method switchpage, supaya primaryStage yg di replace, bkn buka stage bru
			myCartMItem.setOnAction(e -> directToCart(primaryStage));
			purchaseHistoryMItem.setOnAction(e -> directToPurchaseHistory(primaryStage));
			logOutMItem.setOnAction(e -> directToLogin(primaryStage));
			
	}

}
