public class subroutineException extends Exception{
	private String message;

	public subroutineException(String message){
		this.message = message;
	}
	public String toString() {
		return this.message;
	}
}