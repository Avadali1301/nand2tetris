public class variablesException extends Exception{
	private String message;

	public variablesException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}