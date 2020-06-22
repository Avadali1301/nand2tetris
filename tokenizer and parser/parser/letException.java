public class letException extends Exception{
	private String message;

	public letException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}