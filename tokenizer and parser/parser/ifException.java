public class ifException extends Exception{
	private String message;

	public ifException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}