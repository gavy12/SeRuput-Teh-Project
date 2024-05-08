package finalProject;

public class Cart {
	protected int quantity;
	protected String productName;
	protected int totalPrice;
	
	public Cart(int quantity, String productName, int totalPrice) {
		super();
		this.quantity = quantity;
		this.productName = productName;
		this.totalPrice = totalPrice;
	}
	
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public String toString() {
		return String.format("%dx %s (Rp.%d)", quantity, productName, totalPrice);
	} //toString utk formatting isi text di listViewCart
	
}	
