package finalProject;

public class TransactionDetail {
	protected int qty;
	protected String productName;
	protected int productTotalPrice;
	
	public TransactionDetail(int qty, String productName, int productTotalPrice) {
		super();
		this.qty = qty;
		this.productName = productName;
		this.productTotalPrice = productTotalPrice;
	}
	
	public int getQty() {
		return qty;
	}
	
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public int getProductTotalPrice() {
		return productTotalPrice;
	}
	
	public void setProductPrice(int productTotalPrice) {
		this.productTotalPrice = productTotalPrice;
	}
	
	public String toString() {
		return String.format("%dx %s (Rp.%d)", qty, productName, productTotalPrice);
	} //toString utk formatting isi text di listViewCart

}
