package net.michaelmathieu.rootbeer.exception;

public class InstanceNotExists extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private short argument;

	public InstanceNotExists(short argument) {
		super("Instance doesn't exist: " + argument);
		this.argument = argument;
	}

	public short getArgument() {
		return argument;
	}
}