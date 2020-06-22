public class classesException extends Exception{
	private String message;

	public classesException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}