package net.michaelmathieu.rootbeer.exception;

import net.michaelmathieu.rootbeer.util.SharedMemoryManager;

public class InvalidUnsignedShort extends NumberFormatException {

	private static final long serialVersionUID = 1L;

	private int argument;

	public InvalidUnsignedShort(int argument) {
		super("Invalid unsigned short: " + argument + " (min " + SharedMemoryManager.UNSIGNED_SHORT_MIN_VALUE + " max "
				+ SharedMemoryManager.UNSIGNED_SHORT_MAX_VALUE + ")");
		this.argument = argument;
	}

	public int getArgument() {
		return argument;
	}
}