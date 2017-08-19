package fr.tsadeo.app.dsntotree.model;

public class ErrorMessage implements Comparable<ErrorMessage>{
	
	private final int numLine;
	private final String message;
	
	
	public int getNumLine() {
		return numLine;
	}


	public String getMessage() {
		return message;
	}

	public ErrorMessage(String message) {
		this(0, message);
	}

	public ErrorMessage(int numLine, String message) {
		this.numLine = numLine;
		this.message = message;
	}
	
	public String toString() {
		String lineMessage = (this.numLine > 0)?"Ligne ".concat(this.numLine + "").concat(" : "):"";
		return lineMessage.concat(message); 
	}

	//------------------------------------------- implements Comparable
	@Override
	public int compareTo(ErrorMessage o) {
		if (o == null) {
			return 1;
		}
		if (this == o){
			return 0;
		}
		return new Integer(this.numLine).compareTo(new Integer(o.getNumLine()));
	}

}
