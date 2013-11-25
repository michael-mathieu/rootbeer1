package net.michaelmathieu.rootbeer.exception;

public class InvalidDimensionsNumber extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private byte nbDimensions;

	private int argument;

	public InvalidDimensionsNumber(byte nbDimensions, int argument) {
		super("Invalid dimensions number: " + argument + " (max " + nbDimensions + ")");
		this.nbDimensions = nbDimensions;
		this.argument = argument;
	}

	public byte getNbDimensions() {
		return nbDimensions;
	}

	public int getArgument() {
		return argument;
	}
}