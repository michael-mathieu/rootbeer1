package net.michaelmathieu.rootbeer.exception;

public class InvalidDimensionLength extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private short dimensionLength = -1;

	private short argument;

	public InvalidDimensionLength(short argument) {
		super("Invalid dimension length: " + argument);
		this.argument = argument;
	}

	public InvalidDimensionLength(short dimensionLength, short argument) {
		super("Invalid dimension length: " + argument + " (max " + dimensionLength + ")");
		this.argument = argument;
		this.dimensionLength = dimensionLength;
	}

	public short getDimensionLength() {
		return dimensionLength;
	}

	public short getArgument() {
		return argument;
	}
}