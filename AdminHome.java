
package finalProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminHome extends Application{
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

	public void initiate() {
		root = new BorderPane();
		mb = new MenuBar();
		
		initiateMenu();
		gp = new GridPane();
		vBoxProductInfo = new VBox();
		
		titleLbl = new Label("SeRuputTeh"); titleLbl.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");
		
		listViewProduct = new ListView<>();
		
		welcomeLbl = new Label("Welcome, admin" ); welcomeLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
		productDesc = new Label("Select a product to view");
		priceLbl = new Label(); priceLbl.setVisible(false);	
		
		
	}
	
	public void set() {
		setMenu();
		root.setCenter(gp);
		gp.add(titleLbl, 0, 0);
		gp.add(listViewProduct, 0, 1);
		
		
		//masukin komponen ke vbox kanan
		vBoxProductInfo.getChildren().addAll(welcomeLbl, productDesc, priceLbl);
		
		gp.add(vBoxProductInfo, 1, 1);
		
		setListView();
	}
	
	public void position() {
		gp.setHgap(15);
		titleLbl.setPadding(new Insets(10,10,10,10));
		
		//rapihin productDesc
		productDesc.setWrapText(true);
		productDesc.setMaxWidth(400); //spy ga ngestretch pnjg
		
		//rapihin productInfo
		vBoxProductInfo.setSpacing(15);
		
		gp.setMargin(listViewProduct, new Insets(5,10,10,15));
		
		listViewProduct.setMaxHeight(350);
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
