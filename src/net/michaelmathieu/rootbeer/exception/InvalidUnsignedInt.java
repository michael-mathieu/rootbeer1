package net.michaelmathieu.rootbeer.exception;

import net.michaelmathieu.rootbeer.util.SharedMemoryManager;

public class InvalidUnsignedInt extends NumberFormatException {

	private static final long serialVersionUID = 1L;

	private long argument;

	public InvalidUnsignedInt(long argument) {
		super("Invalid unsigned int: " + argument + " (min " + SharedMemoryManager.UNSIGNED_INT_MIN_VALUE + " max "
				+ SharedMemoryManager.UNSIGNED_INT_MAX_VALUE + ")");
		this.argument = argument;
		this.argument = argument;
	}

	public long getArgument() {
		return argument;
	}
}