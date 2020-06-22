public class returnException extends Exception{
	private String message;

	public returnException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}