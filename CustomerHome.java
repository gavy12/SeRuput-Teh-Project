package finalProject;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;

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
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CustomerHome extends Application{
	Scene scene;
	BorderPane root;
	MenuBar mb;
	Menu homeMenu, cartMenu, accountMenu;
	MenuItem homePageMItem, myCartMItem, logOutMItem, purchaseHistoryMItem;
	GridPane gp;
	Label  title, welcomeLbl, productDetail, priceLbl, qtyLbl, totalPriceLbl;
	Spinner<Integer> spinnerQty;
	
	//ListView
	ListView<Product> listViewProduct;
	
	//buat set name di welcomeLbl
	public static String username;
	
	int quantity = 0; //utk simpen quantity, ambil dari spinner
	int unitPrice = 0; //utk simpen unitPriceambil dari priceLbl
	
	//button
	Button addCartBtn;
	
	public static String getUsername() {
		return username; //getter
	}

	public static void setUsername(String username) {
		CustomerHome.username = username; //setter
	}

	public void initiate() {		
		root = new BorderPane();
		gp = new GridPane();
		initiateMenuBar();
		
		//Title
		title = new Label("SeRuputTeh"); title.setStyle("-fx-font-size: 30; -fx-font-weight: 	bold;");
		
		//ListView
		setListView();
		
		//welcome msg
		welcomeLbl = new Label();
		welcomeLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
		
		//description label
		productDetail = new Label("Select a product to view");
		
		//priceLabel
		priceLbl = new Label();
		
		//qty + spinner
		qtyLbl = new Label("Quantity : ");
		spinnerQty = new Spinner<>(0, 1000, 0); //(min, max, awal)
		
		spinnerQty.valueProperty().addListener((obs, oldValue, newValue) -> {
		    quantity = newValue;
		    updateTotalPrice();
		}); //listener utk cek qty spinner

		//totalPriceLbl
		totalPriceLbl = new Label();
		
		//tombol add to card
		addCartBtn = new Button("Add to Cart");
		addCartBtn.setOnAction(e -> addCartBtnEventHandler());
		
		//umpetin dulu tombol sm spinner di awal, nti bikin true di dlm listViewListener()
		qtyLbl.setVisible(false);
		spinnerQty.setVisible(false);
		addCartBtn.setVisible(false);
	}
	
	public void set() {
		//format (col, row, colSpan, rowSpan)
		
		root.setCenter(gp);
		setMenuBar();
		gp.add(title, 0, 0);
		gp.add(listViewProduct, 0, 1, 1 ,5); //column span 1, row span 5 (ad 5 lbl komponen sblhnya) -> biar posisiin label di kanan gmpg
		gp.add(welcomeLbl, 1, 1, 2, 1);
		gp.add(productDetail, 1, 2, 3, 1); //column span 3
		gp.add(priceLbl, 1, 3, 2, 1);
		gp.add(qtyLbl, 1, 4);
		gp.add(spinnerQty, 2, 4);
		gp.add(totalPriceLbl, 3, 4);
		gp.add(addCartBtn, 1, 5, 2, 1);
	}
	
	public void position() {
		gp.setVgap(10);
		title.setPadding(new Insets(20));	
		
		//set welcomeLbl di atas
		GridPane.setValignment(welcomeLbl, VPos.TOP);
		
		//set margin spy tabel ga mepet kiri bgt
		Insets listViewMargin = new Insets(0,20,0,20);
		GridPane.setMargin(listViewProduct, listViewMargin);
		
		//posisikan desc label, dan wrap biar ga ...
		GridPane.setValignment(productDetail, VPos.TOP);
		productDetail.setWrapText(true);
		productDetail.setMaxWidth(400); //spy ga ngestretch pnjg
		
		//rapiin totalPriceLbl
		totalPriceLbl.setPadding(new Insets(15));
		
		//posisikan addCartBtn ke bagian atas row 5
		GridPane.setValignment(addCartBtn, VPos.TOP);
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

	public void setListView() {
		listViewProduct = new ListView<>();
		listViewProduct.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); //single mksdnya cmn bs select 1 item di satu waktu
		listViewListener();
	    loadProdFromDB();
	}

	private void listViewListener() {
		listViewProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				//update descriptionLbl pas produk lain dipilih
				updateProductDesc(newSelection.getProductName());//masukin product name dr newSelected ke constructor method updateProductDesc
				qtyLbl.setVisible(true);
				spinnerQty.setVisible(true);
	            addCartBtn.setVisible(true); //munculin klo misalkan produk selected
	            
			}else {
				//tampilin tulisan default kl gada yg dipilih
				welcomeLbl.setText(username);
				productDetail.setText("Select a product to view");
				unitPrice = 0; //klo gada produk yg diipilih, jdi 0
				priceLbl.setText(""); //text jg ga akan show		
				qtyLbl.setVisible(false);
				spinnerQty.setVisible(false);
	            addCartBtn.setVisible(false); //umpetin klo ga select produk
			}
		});
	}
	
	private void updateProductDesc(String productName) {
		try (Connection connection = DatabaseConnection.getConnection()){
			String query = "SELECT product_des, product_price FROM product WHERE product_name = ?"; //? place holder, utk diisi
			
			try(PreparedStatement ps = connection.prepareStatement(query)) {
				ps.setString(1, productName);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						//retrieve(get) data
						String tempDescription = rs.getString("product_des"); //ambil dr kolom 'product_des'
						int tempPrice = rs.getInt("product_price"); //ambil dr kolom 'product_price'
						
						//set unitPrice sesuai price yg di dpt dri db
						unitPrice = tempPrice;
						
						//set labels
						productDetail.setText(tempDescription); //update detailProduk
						welcomeLbl.setText(productName); //update welcomeLbl nama jadi nama produk
						priceLbl.setText("Price: Rp. " + tempPrice);
						
					} //ResultSet itu java object utk represent hasil dri eksekusi querynya
				}//next() utk pindahin kursor ke next row dari resultSet -> kursor pertama terletak di row 0 (sblm row prtma)
			}//rs.next() jadi true kalo msh ada row selanjutnya, jd yg di dlm if bakal di execute
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private void updateTotalPrice() {
		int quantity = spinnerQty.getValue();
		int total = unitPrice * quantity;
		if (quantity > 1) {
			totalPriceLbl.setText("Total : " + total);
		}else {
			totalPriceLbl.setText(""); //clear kalo qty nya 0 atau 1
		}
	}
	
	private void loadProdFromDB() {
		try (Connection connection = DatabaseConnection.getConnection()) {
			String query = "SELECT * FROM product"; //query buat ambil dr tabel product
			
			//pake PreparedStatement utk execute query
			try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				ResultSet rs = preparedStatement.executeQuery();
				
				//connect pke framework observableList connect backend ke ui, bikin temp utk tampung product
				ObservableList<Product> productList = FXCollections.observableArrayList();
				
				//bikin loop untuk ambil data dri row ke row, query yg di execute RS akan di loop disini
				while (rs.next()) {
					Product product = new Product(rs.getString("product_name")); //nama kolom di sql
					productList.add(product);
					
					//set productID sekalian biar nanti bisa di add to cart
					product.setProductID(rs.getString("productID")); //rs, ambil dari db, setter ke Product.java
				}
				
				//set ke listView
				listViewProduct.setItems(productList);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setWelcomeMessage(String username) {
		welcomeLbl.setText("Welcome, " + username);
	}
	
	private void addCartBtnEventHandler() {
		Product selectedProduct = listViewProduct.getSelectionModel().getSelectedItem();
		String productName = selectedProduct.getProductName() ;
		String userID = getUserIDbyUsername(Login.getUsername()); //masukin inputan username pas user login ke dlm parameter metod
		
		if (!productName.isEmpty() && quantity > 0) {
			String productID = selectedProduct.getProductID(); //ambil dri getter di class product
			
			//simpen productID, userID, quantity
			insertToCart(productID, userID, quantity);//masukin variable yg udh dpt diatas ke parameter
			spinnerQty.getValueFactory().setValue(0); //set spinner ke 0 lagi setelah add
		}
	}
	
	private String getUserIDbyUsername(String tempUsername) {
		String userID = null; //default klo ga ktmu
		
		try (Connection connection = DatabaseConnection.getConnection()) {
			String query = "SELECT userID FROM user WHERE username = ?"; //placeholder
			
			try (PreparedStatement ps = connection.prepareStatement(query)) {
				ps.setString(1, tempUsername); //1 representasikan tanda tanya diatas
				
				try(ResultSet rs = ps.executeQuery()) {
					if(rs.next()) {
						userID = rs.getString("userID");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userID; //return ke method
	}
	
	private void insertToCart(String productID, String userID, int quantity) {
		try (Connection connection = DatabaseConnection.getConnection()) {
			String query = "INSERT INTO cart (productID, userID, quantity) VALUES (?, ?, ?)";
			
			try (PreparedStatement ps = connection.prepareStatement(query)) {
				ps.setString(1, productID);
				ps.setString(2, userID);
				ps.setInt(3, quantity);
		
				int rowsAffected = ps.executeUpdate(); //sekalian utk cek dan validasi Alert, sekalian execute query
				
				if (rowsAffected > 0) {
					Alert addCartConfirmAlert = new Alert(AlertType.CONFIRMATION);
					addCartConfirmAlert.setHeaderText("Added to Cart");
					addCartConfirmAlert.showAndWait();
				}else {
					Alert addCartErrorAlert = new Alert(AlertType.ERROR);
					addCartErrorAlert.setHeaderText("Error");
					addCartErrorAlert.setContentText("quantity must be at least 1");
					addCartErrorAlert.showAndWait();
				}
			}	
		}catch (SQLException e) {
			e.printStackTrace();
		}
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
		PurchaseHistoryPage php = new PurchaseHistoryPage();
		
		try {
			php.start(currentStage); //nti parameternya yg msk disini itu primaryStage utk replace primaryStagenya
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
		primaryStage.setTitle("Home");
		// TODO Auto-generated method stub
		initiate();
		set();
		position();
		
		setWelcomeMessage(username); //masukin username yg di get ke parameter method
		
		//MenuItem event handler utk pindah page
		homePageMItem.setOnAction(e -> directToHomePage(primaryStage)); //mskin primaryStagenya ke method switchpage, supaya primaryStage yg di replace, bkn buka stage bru
		myCartMItem.setOnAction(e -> directToCart(primaryStage));
		purchaseHistoryMItem.setOnAction(e -> directToPurchaseHistory(primaryStage));
		logOutMItem.setOnAction(e -> directToLogin(primaryStage));
		
		
		scene = new Scene(root,800,600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
}
