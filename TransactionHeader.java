package finalProject;

public class TransactionHeader {
	
	//variabel utk transcation header
	protected String transactionID;

	public TransactionHeader(String transactionID) {
		super();
		this.transactionID = transactionID;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	
	public String toString() {
		return transactionID;
	}
}
