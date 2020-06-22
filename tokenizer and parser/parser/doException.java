public class doException extends Exception{
	private String message;

	public doException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}