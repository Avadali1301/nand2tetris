public class elseException extends Exception{
	private String message;

	public elseException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}