package net.michaelmathieu.rootbeer.exception;

public class InstanceAlreadyExists extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private short argument;

	public InstanceAlreadyExists(short argument) {
		super("Instance already exists: " + argument);
		this.argument = argument;
	}

	public short getArgument() {
		return argument;
	}
}