package finalProject;



public class CartForTD {
	protected String productID;
	protected int quantity;
	
	public CartForTD(String productID, int quantity) {
		super();
		this.productID = productID;
		this.quantity = quantity;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}
