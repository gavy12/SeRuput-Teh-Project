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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CartPage extends Application{
	Scene scene;
	BorderPane root;
	MenuBar mb;
	Menu homeMenu, cartMenu, accountMenu;
	MenuItem homePageMItem, myCartMItem, logOutMItem, purchaseHistoryMItem;
	GridPane gp, gpPretelanBawah;
	Label titleLbl, welcomeLbl, detailProductLbl, priceLbl, qtyLbl, totalLbl;
	Label grandTotalLbl, orderInformationLbl, usernameLbl, phoneLbl, addressLbl;
	Spinner<Integer> spinnerQty;
	Button updateCartBtn, removeFromCartBtn, makePurchaseButton;
	ListView<Cart> listViewCart;
	
	int unitPrice;
	int quantity;
	
	
	
	public void initiate() {
		root = new BorderPane();
		gp = new GridPane();
		gpPretelanBawah = new GridPane();
		initiateMenuBar();
		
		//title
		titleLbl = new Label(Login.getUsername() + "'s Cart");
		titleLbl.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");		
	    
		//welcome message
		welcomeLbl = new Label();
		welcomeLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
		
		// Initialize welcomeLbl sblm call di loadCartItemsFromDB
		welcomeLbl.setText("Welcome, " + Login.getUsername());

		//productDetail
		detailProductLbl = new Label("Select a product to add and remove");
		
		//priceLabel
		priceLbl = new Label();
		
		//qty spinner dan pretelan lain di kanan
		qtyLbl = new Label("Quantity : ");
		spinnerQty = new Spinner<>(-100, 100, 0); //minvalue, maxvalue, initial
		totalLbl = new Label();
		updateCartBtn = new Button("Update Cart");
		spinnerQty.valueProperty().addListener((obs, oldValue, newValue) -> {
		    quantity = newValue;
		    updateTotalPriceLbl();
		}); //listener utk cek qty spinner
		removeFromCartBtn = new Button("Remove From Cart");
	

		//button handler
		updateCartBtn.setOnAction(e -> updateCartBtnEventHandler());
		removeFromCartBtn.setOnAction(e -> removeFromCartEventHandler());
		
		  //bikin semua invisible dulu
	    qtyLbl.setVisible(false);
	    spinnerQty.setVisible(false);
	    totalLbl.setVisible(false);
	    updateCartBtn.setVisible(false);
	    removeFromCartBtn.setVisible(false);

	    //pretelan dibawah listView
	    grandTotalLbl = new Label("Total : ");
	    orderInformationLbl = new Label("Order Information"); orderInformationLbl.setStyle("-fx-font-weight: bold;");
	    usernameLbl = new Label("Username : " + Login.getUsername()); //hny berlaku klo user udh login
	    phoneLbl = new Label("Phone Number : " + getPhoneNumberFromDB()); //hny berlaku klo user udh login
	    addressLbl = new Label("Address : " + getAddressFromDB());
	    makePurchaseButton = new Button("Make purchase");
	    
	    makePurchaseButton.setOnAction(e -> {
			try {
				showPurchaseConfirmationPopUp();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		//setListView
		setupListViewCart();
	}
	
	public void set() {
		root.setCenter(gp);
		setMenuBar();
		
		//set title
		gp.add(titleLbl, 0, 0);
		
		//set List View Cart
		gp.add(listViewCart, 0, 1, 1, 5); //row span 5
		
		//setWelcomeLbl dan pretelan lainnya
		gp.add(welcomeLbl, 1, 1, 3, 1); 
		gp.add(detailProductLbl, 1, 2, 8, 1); //col span 8 sekalian biar aman
		gp.add(priceLbl, 1, 3, 2, 1); //col span set 3 aj, biar kl pnjg aman
		gp.add(qtyLbl, 1, 4);
		gp.add(spinnerQty, 2, 4, 4, 1); //colspan 4
		gp.add(totalLbl, 6, 4);
		gp.add(updateCartBtn, 1, 5, 2, 1);
		gp.add(removeFromCartBtn, 3, 5, 4, 1);
		
		//set yg dibwhnya listView
		gpPretelanBawah.add(grandTotalLbl, 0, 1);
		gpPretelanBawah.add(orderInformationLbl, 0, 2);
		gpPretelanBawah.add(usernameLbl, 0, 3);
		gpPretelanBawah.add(phoneLbl, 0, 4);
		gpPretelanBawah.add(addressLbl, 0, 5);
		gpPretelanBawah.add(makePurchaseButton, 0, 6);
		gp.add(gpPretelanBawah, 0, 6); //mskin ke gp utama
	}
	
	public void position() {
		gp.setVgap(10);
		gp.setHgap(7);
		titleLbl.setPadding(new Insets(20));
		
		//welcomeLbl di atas
		GridPane.setValignment(welcomeLbl, VPos.TOP);
		
		//rapihin list view
		Insets listViewMargin = new Insets(0,20,0,20);
		GridPane.setMargin(listViewCart, listViewMargin);
		listViewCart.setMaxHeight(200);
		
		//rapihin detailProduct
		GridPane.setValignment(detailProductLbl, VPos.TOP);
		detailProductLbl.setWrapText(true);
		detailProductLbl.setMaxWidth(400); //spy ga ngestretch pnjg
		
		//rapihin total
		totalLbl.setPadding(new Insets(15));
		
		//rapihin spinner
		spinnerQty.setMaxWidth(130);
		
		//rapihin tombol update cart sama remove
		GridPane.setValignment(updateCartBtn, VPos.TOP);
		GridPane.setValignment(removeFromCartBtn, VPos.TOP);
		updateCartBtn.setPrefWidth(120);
		
		//rapihin gpPretelanBawah
		gp.setMargin(gpPretelanBawah, listViewMargin); //(top,left,bottom, right)
		gpPretelanBawah.setVgap(10);
		grandTotalLbl.setPadding(new Insets(0, 0, 5, 0)); //biar ada jarak sm orderInformation
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

	private void setupListViewCart() {
		listViewCart = new ListView<>();
		listViewCart.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); //single = 1 item at a time
		loadCartItemsFromDB();
		listViewCartListener();
	}
	
	public void loadCartItemsFromDB () {
		try (Connection connection = DatabaseConnection.getConnection()) {
			String query = "SELECT c.quantity, p.product_name, p.product_price " + "FROM cart c " + 
						"JOIN product p ON c.productID = p.productID " + "WHERE c.userID = ?"; //ambil semua data dari entitas cart, pke INNER JOIN 2 table
				
			//preparedStatement utk execute query
			try (PreparedStatement ps = connection.prepareStatement(query)) {
				ps.setString(1, getUserID());
				ResultSet rs = ps.executeQuery();
				
				ObservableList<Cart> cartList = FXCollections.observableArrayList();
				
				listViewCart.getItems().clear(); //bersihin dulu, cari aman ga duplikat
				cartList.clear();
				
				//while rs masih ada next, dia akan trs loop
				while(rs.next()) {
					int quantity = rs.getInt("quantity"); //ambil kolom quantity
					int unitPrice = rs.getInt("product_price");
					String productName = rs.getString("product_name");
				
					int totalPrice = quantity*unitPrice;
					
					Cart tempCart = new Cart(quantity, productName, totalPrice);
					cartList.add(tempCart); //masukin ke arrayList
				}
				// Jumlahin semua totalPrice di cartList utk dpt grandTotal
	            int allTotal = cartList.stream().mapToInt(Cart::getTotalPrice).sum();
	            grandTotalLbl.setText("Total : " + allTotal);
				
				if (cartList.isEmpty()) {
					//set welcomeLbl ke "No Item In Cart"
					welcomeLbl.setText("No Item in Cart");
					detailProductLbl.setText("Consider adding one");
				}else {
					//set ke sesuai case
					welcomeLbl.setText("Welcome, " + Login.getUsername());
					detailProductLbl.setText("Select a product to add and remove");
				}
				listViewCart.setItems(cartList); //masukin arraylist ke listView
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void listViewCartListener () {
		listViewCart.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if(newSelection != null) {
				//update descriptionLbl klo ada produk dipilih
				updateProductDesc(newSelection.getProductName()); //saat pindah selection, productName newSelection dimasukin ke method
				priceLbl.setVisible(true);
				qtyLbl.setVisible(true);
				spinnerQty.setVisible(true);
				totalLbl.setVisible(true);
				updateCartBtn.setVisible(true);
				removeFromCartBtn.setVisible(true);
			}else {
				welcomeLbl.setText("Welcome, " + Login.getUsername());
				detailProductLbl.setText("Select a product to add and remove");
				unitPrice = 0;
				priceLbl.setVisible(false);
				qtyLbl.setVisible(false);
				spinnerQty.setVisible(false);
				totalLbl.setVisible(false);
				updateCartBtn.setVisible(false);
				removeFromCartBtn.setVisible(false);
			}
		});
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
	
	private void updateProductDesc(String productName) {
		try (Connection con = DatabaseConnection.getConnection()) {
			String query = "SELECT product_des, product_price FROM product WHERE product_name = ?";
			
			try (PreparedStatement ps = con.prepareStatement(query)) {
				ps.setString(1, productName);
				try (ResultSet rs = ps.executeQuery()) {
					if(rs.next()) {
						String tempDescription = rs.getString("product_des");
						int tempPrice = rs.getInt("product_price");
						
						unitPrice = tempPrice;
						
						//update lbls
						welcomeLbl.setText(productName);
						detailProductLbl.setText(tempDescription);
						priceLbl.setText("Price: Rp. " + tempPrice);
					}
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void updateTotalPriceLbl() {
		int quantity = spinnerQty.getValue();
		int total = unitPrice * quantity;
		totalLbl.setText("Total : " + total);
		}
	
	private void updateCartBtnEventHandler() {
		//ambil selected item dri listViewCart
		Cart selectedCart = listViewCart.getSelectionModel().getSelectedItem();
		
		if (selectedCart != null) {
			int currentQty = selectedCart.getQuantity();
			int newQty = currentQty + spinnerQty.getValue();
			
			//check apakah hasil newQty valid (kalo < 0 itu ga valid, klo 0 delete, kalo >1 update)
			if (newQty == 0) {
				//remove produk dri cart
				removeFromCart(selectedCart); //pnggil method, selected cart masukin ke parameter method removeFromCart()
				Alert removeFromCartAlert = new Alert (AlertType.CONFIRMATION);
				removeFromCartAlert.setHeaderText("Deleted from Cart");
				removeFromCartAlert.showAndWait();
				spinnerQty.getValueFactory().setValue(0);
				
			}else if (newQty > 0) {
				//update qty dri cart
				updateQtyInCart(selectedCart, newQty); //mskin selectedCart dan newQty ke parameter method
				Alert updatedCartAlert = new Alert (AlertType.CONFIRMATION);
				updatedCartAlert.setHeaderText("Updated Cart");
				updatedCartAlert.showAndWait();
				spinnerQty.getValueFactory().setValue(0);
				
			}else {
				Alert invalidQtyErrorAlert = new Alert(AlertType.ERROR);
				invalidQtyErrorAlert.setHeaderText("Not a Valid Amount");
				invalidQtyErrorAlert.showAndWait();
			}
			refreshTable();
		}
	}
	
	private void removeFromCartEventHandler() {
		Cart SelectedCart = listViewCart.getSelectionModel().getSelectedItem();
		removeFromCart(SelectedCart);
		
		refreshTable();
	}
	
	private void removeFromCart(Cart selectedCartItem) {
		try(Connection con = DatabaseConnection.getConnection()) {
			String query = "DELETE FROM cart " +
                    "WHERE productID = ? AND userID = (SELECT userID FROM user WHERE username = ?)";
			
			try(PreparedStatement ps = con.prepareStatement(query)) {
				ps.setString(1, getProductID(selectedCartItem.getProductName())); //ambil nama product dri selectedCart, masukin ke parameter method getProductID()
				ps.setString(2, Login.getUsername()); //2 -> isi tanda tanya ke dua
				ps.executeUpdate();
				
				Alert removedCartAlert = new Alert(AlertType.ERROR);
				removedCartAlert.setHeaderText("Deleted from Cart");
				removedCartAlert.showAndWait();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void updateQtyInCart(Cart selectedCartItem, int newQty) {
		try(Connection con = DatabaseConnection.getConnection()) {
			String query = "UPDATE cart c " +
					"JOIN user u ON c.userID = u.userID " +
					"SET c.quantity = ? WHERE productID = ? AND u.username = ?";
			
			try (PreparedStatement ps = con.prepareStatement(query)) {
				ps.setInt(1, newQty);
				ps.setString(2, getProductID(selectedCartItem.getProductName())); //mskin productName ke parameter method getProductID, biar dpt productID
				ps.setString(3, Login.getUsername());
				ps.executeUpdate();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getProductID (String productName) {
		String productID = null;
		try (Connection con = DatabaseConnection.getConnection()) {
			String query = "SELECT productID from product WHERE product_name = ?";
			
			try (PreparedStatement ps = con.prepareStatement(query)) {
				ps.setString(1, productName);
				try(ResultSet rs = ps.executeQuery()) {
					if(rs.next()) {
						productID = rs.getString("productID");
					}
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return productID;
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
	
	public void refreshTable() {
		loadCartItemsFromDB();
		listViewCart.refresh();
	}
	
	private void directToHomePage(Stage stageSkrg) {
		 CustomerHome ch = new CustomerHome();
		 
			try {
				ch.start(stageSkrg);		
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void directToCart(Stage stageSkrg) {
		CartPage cart = new CartPage();
		
		try {
			cart.start(stageSkrg);		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void directToLogin(Stage stageSkrg) {
		Login login = new Login();
		
		try {
			login.start(stageSkrg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void directToPurchaseHistory(Stage stageSkrg) {
		PurchaseHistoryPage php = new PurchaseHistoryPage();
		
		try {
			php.start(stageSkrg);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void showPurchaseConfirmationPopUp() {
		PopUp purchasePopUp = new PopUp();
		Stage stage = new Stage();
		
		try {
			purchasePopUp.start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    public ObservableList<Cart> getListViewCartItems() {
    	listViewCart = new ListView<>();
        return listViewCart.getItems();
    }
   
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Cart");
		initiate();
		set();
		position();
		
		scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		//add event handler ke menuItems, method dibawah
		homePageMItem.setOnAction(e -> directToHomePage(primaryStage));
		myCartMItem.setOnAction(e -> directToCart(primaryStage));
		logOutMItem.setOnAction(e -> directToLogin(primaryStage));
		purchaseHistoryMItem.setOnAction(e -> directToPurchaseHistory(primaryStage));	
	}

}
