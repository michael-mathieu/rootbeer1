package net.michaelmathieu.rootbeer.exception;

public class InvalidObjectsNumber extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private short argument;

	public InvalidObjectsNumber(short argument) {
		super("Invalid objects number: " + argument);
		this.argument = argument;
	}

	public short getArgument() {
		return argument;
	}
}