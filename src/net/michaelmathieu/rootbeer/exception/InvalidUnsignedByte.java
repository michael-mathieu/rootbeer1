package net.michaelmathieu.rootbeer.exception;

import net.michaelmathieu.rootbeer.util.SharedMemoryManager;

public class InvalidUnsignedByte extends NumberFormatException {

	private static final long serialVersionUID = 1L;

	private short argument;

	public InvalidUnsignedByte(short argument) {
		super("Invalid unsigned byte: " + argument + " (min " + SharedMemoryManager.UNSIGNED_BYTE_MIN_VALUE + " max "
				+ SharedMemoryManager.UNSIGNED_BYTE_MAX_VALUE + ")");
		this.argument = argument;
	}

	public short getArgument() {
		return argument;
	}
}