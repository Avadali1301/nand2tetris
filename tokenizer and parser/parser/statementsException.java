public class statementsException extends Exception{
	private String message;

	public statementsException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}