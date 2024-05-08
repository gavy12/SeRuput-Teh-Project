package finalProject;

public class Product {
	protected final String productName;
	protected String productID; //ini untuk nampung productID saat rs masuk ke listView, tpi yg ditampilin hny nama
	
	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getProductName() {
		return productName; //getter utk nama produk
	}

	public Product(String productName) {
		this.productName = productName; 
	}

	 
    @Override
    public String toString() {
        return productName; // supaya bs muncul di listView dgn format yg bnr, value productName di kls di return ke String
    } 

}
