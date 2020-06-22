public class whileException extends Exception{
	private String message;

	public whileException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}