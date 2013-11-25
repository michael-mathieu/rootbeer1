package net.michaelmathieu.rootbeer.exception;

public class InvalidIdInstance extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private short nbMaxObjects;

	private short argument;

	public InvalidIdInstance(short nbMaxObjects, short argument) {
		super("Invalid ID instance: " + argument + " (max " + nbMaxObjects + ")");
		this.nbMaxObjects = nbMaxObjects;
		this.argument = argument;
	}

	public short getNbMaxObjects() {
		return nbMaxObjects;
	}

	public short getArgument() {
		return argument;
	}
}