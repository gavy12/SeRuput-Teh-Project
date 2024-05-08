package finalProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManageProduct extends Application{
	Scene scene;
	BorderPane root;
	MenuBar mb;
	Menu homeMenu, manageProdMenu, accountMenu;
	MenuItem homePageMItem, manageProdMItem, logOutMItem;
	GridPane gp;
	VBox vBoxProductInfo;
	HBox hBoxButton;
	ListView<Product> listViewProduct;
	Label titleLbl, welcomeLbl, productDesc, priceLbl;
	Button addProductBtn, updateProductBtn, removeProductBtn;
	
	//komponen untuk add product form
	VBox vBoxAddProductForm;
	HBox hBoxAddOrBackButton;
	Label inputProductNameLbl, inputProductPriceLbl, inputProductDescLbl;
	TextField inputProductName, inputProductPrice;
	TextArea inputProductDesc;
	Button addProductConfirmButton, backButtonPunyaAddProduct;
	
	//komponen untuk update product form
	VBox vBoxUpdateProductForm;
	HBox hBoxUpdateOrBackBtn;
	Label updateProductLbl;
	TextField inputNewPrice;
	Button updateProductConfirmBtn, backButtonPunyaUpdateProduct;

	//komponen untuk remove product form
	VBox vBoxRemoveProductForm;
	HBox hBoxRemoveOrBackBtn;
	Label areYouSureLabel;
	Button removeProductConfirmBtn, backButtonPunyaRemoveProduct;
	
	public void initiate() {
		root = new BorderPane();
		mb = new MenuBar();
		
		initiateMenu();
		gp = new GridPane();
		vBoxProductInfo = new VBox();
		
		titleLbl = new Label("Manage Products"); titleLbl.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");
		
		listViewProduct = new ListView<>();
		
		welcomeLbl = new Label("Welcome, admin" ); welcomeLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
		productDesc = new Label("Select a product to update");
		priceLbl = new Label(); priceLbl.setVisible(false);
		
		addProductBtn = new Button("Add Product");
		addProductBtn.setOnAction(e -> addProductButtonHandler());
		updateProductBtn = new Button("Update Product");
		updateProductBtn.setOnAction(e -> updateProductButtonHandler());
		removeProductBtn = new Button("Remove Product");
		removeProductBtn.setOnAction(e -> removeProductButtonHandler());
		
		hBoxButton = new HBox();
		
		//untuk form addProduct
		initiateKomponenAddProductForm();
		
		//untuk form updateProduct
		initiateKomponenUpdateProductForm();
		
		//form removeProduct
		initiateKomponenRemoveProductForm();
		
		
	}
	
	public void set() {
		
		setMenu();
		root.setCenter(gp);
		gp.add(titleLbl, 0, 0);
		gp.add(listViewProduct, 0, 1, 1, 5); //column span set 5
		
		hBoxButton.getChildren().addAll(updateProductBtn, removeProductBtn);
		
		//masukin komponen ke vbox kanan
		vBoxProductInfo.getChildren().addAll(welcomeLbl, productDesc, priceLbl, addProductBtn, hBoxButton);
		
		gp.add(vBoxProductInfo, 1, 1);
		
		setListView();
		
		setKomponenAddProductForm();
		vBoxAddProductForm.setVisible(false);
		
		//masukin form addProduct ke gp
		gp.add(vBoxAddProductForm, 1, 2);
		GridPane.setValignment(vBoxAddProductForm, VPos.BOTTOM);
		GridPane.setMargin(vBoxAddProductForm, new Insets(5, 20, 5, 5));
		
		
		setKomponenUpdateProductForm();
		vBoxUpdateProductForm.setVisible(false); //umpetin dulu
		//masukin form updateProduct ke gp, nanti tinggal mainin visibility nya aja
		gp.add(vBoxUpdateProductForm, 1, 2);
		
		setKomponenRemoveProductForm();
		vBoxRemoveProductForm.setVisible(false);
		//masukin form updateProduct ke gp, nanti tinggal visibility nya yang di switch
		gp.add(vBoxRemoveProductForm, 1, 2);
		
	}
	
	public void position() {
		gp.setHgap(15);
		titleLbl.setPadding(new Insets(10,10,10,15));
		
		//rapihin productDesc
		productDesc.setWrapText(true);
		productDesc.setMaxWidth(400); //spy ga ngestretch pnjg
		
		//rapihin tombol2 biar sama semua sizenya
		addProductBtn.setPrefSize(150, 5);
		updateProductBtn.setPrefSize(150, 5);
		removeProductBtn.setPrefSize(150, 5);
		//rapihin HBox button
		hBoxButton.setSpacing(10);
		
		//rapihin productInfo
		vBoxProductInfo.setSpacing(10);
		
		gp.setMargin(listViewProduct, new Insets(0,10,10,15));
		listViewProduct.setMaxHeight(400);
	}
	
	private void initiateMenu () {
		//initiate menu
				homeMenu = new Menu("Home");
				manageProdMenu = new Menu("Manage Products");
				accountMenu = new Menu("Account");
				
		//initiate menuItem (subMenu)
				homePageMItem = new MenuItem("HomePage");
				manageProdMItem = new MenuItem("Manage Products");
				logOutMItem = new MenuItem("Log out");
	}
	
	private void initiateKomponenAddProductForm () {
		vBoxAddProductForm = new VBox();
		hBoxAddOrBackButton = new HBox();
		
		inputProductNameLbl = new Label("Input product name"); 
		inputProductNameLbl.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
		inputProductName = new TextField(); inputProductName.setPromptText("Input product name...");
		
		
		inputProductPriceLbl = new Label("Input product price"); 
		inputProductPriceLbl.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
		inputProductPrice = new TextField(); inputProductPrice.setPromptText("Input product price...");
		
		
		inputProductDescLbl = new Label("Input product description");
		inputProductDescLbl.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
		inputProductDesc = new TextArea(); inputProductDesc.setPromptText("Input product description...");
		
		addProductConfirmButton = new Button("Add Product");
		addProductConfirmButton.setOnAction(e -> addProductConfirmButtonHandler());
		backButtonPunyaAddProduct = new Button("Back");
		backButtonPunyaAddProduct.setOnAction(e -> {
			vBoxAddProductForm.setVisible(false);
			addProductBtn.setVisible(true);
			updateProductBtn.setVisible(true);
			removeProductBtn.setVisible(true);
		});
		
		
	}
	
	private void setKomponenAddProductForm () {
		hBoxAddOrBackButton.getChildren().addAll(addProductConfirmButton, backButtonPunyaAddProduct);
		
		vBoxAddProductForm.getChildren().addAll(inputProductNameLbl, inputProductName, inputProductPriceLbl, inputProductPrice,
												inputProductDescLbl, inputProductDesc, hBoxAddOrBackButton);
		
		vBoxAddProductForm.setSpacing(10);
		hBoxAddOrBackButton.setSpacing(10);
		addProductConfirmButton.setPrefSize(110,5);
		backButtonPunyaAddProduct.setPrefSize(110, 5);
		
		inputProductName.setPrefSize(110,5);
		inputProductPrice.setPrefSize(110,5);
		inputProductDesc.setPrefSize(110, 50);
		
		addProductConfirmButton.setOnAction(e -> addProductConfirmButtonHandler());
	}
	
	private void initiateKomponenUpdateProductForm() {
		vBoxUpdateProductForm = new VBox();
		hBoxUpdateOrBackBtn = new HBox();
		
		updateProductLbl = new Label("Update Product"); updateProductLbl.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
		inputNewPrice = new TextField(); inputNewPrice.setPromptText("Input new price...");
		
		updateProductConfirmBtn = new Button("Update Product");
		updateProductConfirmBtn.setOnAction(e -> updateProductConfirmButtonHandler());
		backButtonPunyaUpdateProduct = new Button("Back");
		backButtonPunyaUpdateProduct.setOnAction(e -> {
			vBoxUpdateProductForm.setVisible(false);
			addProductBtn.setVisible(true);
			updateProductBtn.setVisible(true);
			removeProductBtn.setVisible(true);
		});
		
	}
	
	private void setKomponenUpdateProductForm() {
		//set tombol yang dibawah dulu
		hBoxUpdateOrBackBtn.getChildren().addAll(updateProductConfirmBtn, backButtonPunyaUpdateProduct);
		hBoxUpdateOrBackBtn.setSpacing(10);
		
		vBoxUpdateProductForm.getChildren().addAll(updateProductLbl, inputNewPrice, hBoxUpdateOrBackBtn);
		vBoxUpdateProductForm.setSpacing(10);
		
		updateProductConfirmBtn.setPrefSize(110, 5);
		backButtonPunyaUpdateProduct.setPrefSize(110, 5);
	}
	
	private void initiateKomponenRemoveProductForm() {
		vBoxRemoveProductForm = new VBox();
		hBoxRemoveOrBackBtn = new HBox();
		
		areYouSureLabel = new Label("Are you sure, you want to remove this product?");
		areYouSureLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
		areYouSureLabel.setPadding(new Insets(20, 5, 0, 5));
		areYouSureLabel.setMaxWidth(300);
		areYouSureLabel.setWrapText(true);
		
		removeProductConfirmBtn = new Button("Remove Product");
		removeProductConfirmBtn.setPrefSize(110, 5);
		removeProductConfirmBtn.setOnAction(e -> removeProductConfirmButtonHandler());
		
		backButtonPunyaRemoveProduct = new Button("Back");
		backButtonPunyaRemoveProduct.setPrefSize(110, 5);
		backButtonPunyaRemoveProduct.setOnAction(e -> {
			vBoxRemoveProductForm.setVisible(false);
			addProductBtn.setVisible(true);
			updateProductBtn.setVisible(true);
			removeProductBtn.setVisible(true);
		});
		
		vBoxRemoveProductForm.setVisible(false);
	}
	
	private void setKomponenRemoveProductForm() {
		hBoxRemoveOrBackBtn.getChildren().addAll(removeProductConfirmBtn, backButtonPunyaRemoveProduct);
		hBoxRemoveOrBackBtn.setSpacing(10);
		
		vBoxRemoveProductForm.getChildren().addAll(areYouSureLabel, hBoxRemoveOrBackBtn);
		vBoxRemoveProductForm.setSpacing(10);
		
	}
	
	private void setMenu() {
		//insert menus to the menu bar
		mb.getMenus().addAll(homeMenu, manageProdMenu, accountMenu);
		
	//insert the menuItems
		homeMenu.getItems().addAll(homePageMItem);
		manageProdMenu.getItems().addAll(manageProdMItem);
		accountMenu.getItems().addAll(logOutMItem);
		
	//insert menubar into bp root
		root.setTop(mb);
	}
	
	public void setListView() {
		
		listViewProduct.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); //single mksdnya cmn bs select 1 item di satu waktu
		listViewListener();
	    loadProdFromDB();
	}
	
	private void listViewListener() {
		listViewProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				//update descriptionLbl pas produk lain dipilih
				updateProductDesc(newSelection.getProductName());//masukin product name dr newSelected ke constructor method updateProductDesc
	            vBoxProductInfo.setVisible(true); //product info yg di kanan munculin
				hBoxButton.setVisible(true); //set hbox yg nampung tombol biar muncul
				addProductBtn.setVisible(true);
				updateProductBtn.setVisible(true);
				removeProductBtn.setVisible(true);
			}else {
				//tampilin tulisan default kl gada yg dipilih
				welcomeLbl.setText("Welcome, admin");
				productDesc.setText("Select a product to view");
				priceLbl.setText(""); //text jg ga akan show		
			}
		});
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
				}
				
				//set ke listView
				listViewProduct.setItems(productList);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
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
						
						//set labels
						productDesc.setText(tempDescription); //update detailProduk
						welcomeLbl.setText(productName); //update welcomeLbl nama jadi nama produk
						priceLbl.setText("Price: Rp. " + tempPrice);
						priceLbl.setVisible(true);
						
					} //ResultSet itu java object utk represent hasil dri eksekusi querynya
				}//next() utk pindahin kursor ke next row dari resultSet -> kursor pertama terletak di row 0 (sblm row prtma)
			}//rs.next() jadi true kalo msh ada row selanjutnya, jd yg di dlm if bakal di execute
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private void addProductButtonHandler() {
		addProductBtn.setVisible(false);
		updateProductBtn.setVisible(false);
		removeProductBtn.setVisible(false);
		
		vBoxAddProductForm.setVisible(true);
	}
	
	private void addProductConfirmButtonHandler() {
		
		Alert addProductErrorAlert = new Alert(AlertType.ERROR);
		addProductErrorAlert.setHeaderText("Add product fail");
		
		Alert addProductConfirmAlert = new Alert(AlertType.CONFIRMATION);
		addProductConfirmAlert.setHeaderText("Product added");
		
		if (inputProductName.getText().isEmpty()) {
			addProductErrorAlert.setContentText("Product name cannot be empty");
			addProductErrorAlert.showAndWait();
		}else if(inputProductPrice.getText().isEmpty()) {
			addProductErrorAlert.setContentText("Product price cannot be empty");
			addProductErrorAlert.showAndWait();
		}else if(!isNumeric(inputProductPrice.getText())) {
			addProductErrorAlert.setContentText("Product price must be a number");
			addProductErrorAlert.showAndWait();
		}else if(inputProductDesc.getText().isEmpty()) {
			addProductErrorAlert.setContentText("Product description cannot be empty");
			addProductErrorAlert.showAndWait();
		}else {
			try (Connection con = DatabaseConnection.getConnection()) {
				String addConfirmButtonQuery = "INSERT INTO product (productID, product_name, product_price, product_des) VALUES (?, ?, ?, ?)";
				String newProductID = generateProductID();
				
				try (PreparedStatement ps = con.prepareStatement(addConfirmButtonQuery)) {
					ps.setString(1, newProductID);
					ps.setString(2, inputProductName.getText());
					ps.setString(3, inputProductPrice.getText());
					ps.setString(4, inputProductDesc.getText());
					ps.executeUpdate();
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}	
			addProductConfirmAlert.showAndWait();
			refreshTable();
			returnToInitialState();
			vBoxAddProductForm.setVisible(false); //umpetin lagi addProductForm nya
			vBoxProductInfo.setVisible(true); //munculin lagi tombol2nya
		}
		
	}
	
	private void updateProductButtonHandler() {
		addProductBtn.setVisible(false);
		updateProductBtn.setVisible(false);
		removeProductBtn.setVisible(false);
		
		vBoxUpdateProductForm.setVisible(true);
	}
	
	private void updateProductConfirmButtonHandler () {
		Product selectedProduct = listViewProduct.getSelectionModel().getSelectedItem();
		try(Connection con = DatabaseConnection.getConnection()) {
			String queryUpdatePrice = "UPDATE product SET product_price = ? WHERE productID = ?";
			
			
			try (PreparedStatement ps = con.prepareStatement(queryUpdatePrice)) {
				String priceText = inputNewPrice.getText();
				
				if(isNumeric(priceText)) {
					int priceValue = Integer.parseInt(priceText); //harus convert dari String ke int baru bs masuk ke db
					ps.setInt(1, priceValue);
					ps.setString(2, getProductID(selectedProduct.getProductName())); //masukin productName dr selected list ke cons method
					ps.executeUpdate();
					Alert updateSuccessAlert = new Alert(AlertType.CONFIRMATION);
					updateSuccessAlert.setHeaderText("Update Success");
					updateSuccessAlert.showAndWait();
					refreshTable();
					returnToInitialState();
					vBoxUpdateProductForm.setVisible(false); //umpetin lagi
					vBoxProductInfo.setVisible(true); //munculin lagi product info
					
				}else {
					Alert invalidInputAlert = new Alert(Alert.AlertType.ERROR);
	                invalidInputAlert.setHeaderText("Invalid Input");
	                invalidInputAlert.setContentText("Product price must be a number.");
	                invalidInputAlert.showAndWait();
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	private void removeProductButtonHandler() {
		vBoxRemoveProductForm.setVisible(true);
		
		addProductBtn.setVisible(false);
		updateProductBtn.setVisible(false);
		removeProductBtn.setVisible(false);
	}
	
	private void removeProductConfirmButtonHandler() {
		Product selectedProduct = listViewProduct.getSelectionModel().getSelectedItem();
		
		if (selectedProduct != null) {
			try(Connection con = DatabaseConnection.getConnection()) {
				String removeProductQuery = "DELETE FROM product WHERE productID = ?";
				
				try(PreparedStatement ps = con.prepareStatement(removeProductQuery)) {
					ps.setString(1, getProductID(selectedProduct.getProductName())); //mskin productName ke constructor method getProductID
					ps.executeUpdate();
					
					Alert removeSuccessAlert = new Alert(AlertType.CONFIRMATION);
					removeSuccessAlert.setHeaderText("Product removed");
					removeSuccessAlert.showAndWait();
				}
				
				
			}catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			refreshTable();
			returnToInitialState();
			vBoxRemoveProductForm.setVisible(false); //umpetin lgi
			vBoxProductInfo.setVisible(true); //munculin lagi product info
		}else {
			Alert removeErrorAlert = new Alert(AlertType.ERROR);
			removeErrorAlert.setHeaderText("Remove product fail");
			removeErrorAlert.setContentText("No product selected");
			removeErrorAlert.showAndWait();
		}
		
		
		
	}
	
	private String getProductID(String productName) {
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
	
	
	private boolean isNumeric(String input) {
	    return input.matches("\\d+");
	}
	
	private void returnToInitialState () {
		welcomeLbl.setText("Welcome, admin" );
		welcomeLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
		
		productDesc.setText("Select a Product to Update");
	}
	
	private void directToHomePage (Stage currentStage) {
		AdminHome ah = new AdminHome();
		
		try {
			ah.start(currentStage);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void directToManageProduct (Stage currentStage) {
		ManageProduct mp = new ManageProduct();
		
		try {
			mp.start(currentStage);
		}catch (Exception e) {
			// TODO: handle exception
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
	
	private static String generateProductID() {
		Random rand = new Random();
		int randomTigaDigit = rand.nextInt(999);
		return "TE" + String.format("%d", randomTigaDigit);
	}
	
	
	public void refreshTable() {
		loadProdFromDB();
		listViewProduct.refresh();
	}
	
	
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		initiate();
		set();
		position();
		
		scene = new Scene(root, 900, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		homePageMItem.setOnAction(e -> directToHomePage(primaryStage));
		manageProdMItem.setOnAction(e -> directToManageProduct(primaryStage));
		logOutMItem.setOnAction(e -> directToLogin(primaryStage));
	}

}
