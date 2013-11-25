package net.michaelmathieu.rootbeer.util;

import net.michaelmathieu.rootbeer.exception.InstanceAlreadyExists;
import net.michaelmathieu.rootbeer.exception.InstanceNotExists;
import net.michaelmathieu.rootbeer.exception.InvalidDimensionLength;
import net.michaelmathieu.rootbeer.exception.InvalidDimensionsNumber;
import net.michaelmathieu.rootbeer.exception.InvalidIdInstance;
import net.michaelmathieu.rootbeer.exception.InvalidObjectsNumber;
import net.michaelmathieu.rootbeer.exception.InvalidUnsignedByte;
import net.michaelmathieu.rootbeer.exception.InvalidUnsignedInt;
import net.michaelmathieu.rootbeer.exception.InvalidUnsignedShort;
import net.michaelmathieu.rootbeer.exception.SharedMemoryManagerNotInitialized;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;

/**
 * The goal of this class is to simplify the usage of the shared memory: no need to know the underlying bit representation.
 * 
 * Example with a simple byte:
 *   createInstance((short) 0, SharedMemoryManager.BYTE);
 *   setByte((short) 0, (byte) 120);
 *   getByte((short) 0);
 * 
 * Example with an array with 1 dimension:
 *   createInstance((short) 1, SharedMemoryManager.INT, (short) 5);
 *   for (short i = 0; i < 5; i++) {
 *     setInt((short) 1, i + 1, i);
 *     getInt((short) 1, i);
 *   }
 * 
 * Example with an array with 2 dimensions:
 *   createInstance((short) 2, SharedMemoryManager.INT, (short) 3, (short) 2);
 *   for (short i = 0; i < 3; i++) {
 *     for (short j = 0; j < 2; j++) {
 *       setInt((short) 2, (i * 10) + j, i, j);
 *       getInt((short) 2, i, j);
 *     }
 *   }
 * 
 * @author Michaël Mathieu
 */
public class SharedMemoryManager {

	public static final int MAX_SIZE_SHARED_MEMORY_IN_BYTE = 48 * 1024;

	public static final byte NB_MAX_DIMENSIONS = 15; // (2^NB_DIMENSIONS_SIZE_IN_BIT)-1

	// (unsigned) byte
	public static final byte BYTE_SIZE_IN_BYTE = 1;

	public static final byte BYTE = 0;

	public static final byte BYTE_MIN_VALUE = Byte.MIN_VALUE;

	public static final byte BYTE_MAX_VALUE = Byte.MAX_VALUE;

	public static final byte UNSIGNED_BYTE = 1;

	public static final byte UNSIGNED_BYTE_MIN_VALUE = 0;

	public static final short UNSIGNED_BYTE_MAX_VALUE = 255;

	// (unsigned) short
	public static final byte SHORT_SIZE_IN_BYTE = 2;

	public static final byte SHORT = 2;

	public static final short SHORT_MIN_VALUE = Short.MIN_VALUE;

	public static final short SHORT_MAX_VALUE = Short.MAX_VALUE;

	public static final byte UNSIGNED_SHORT = 3;

	public static final byte UNSIGNED_SHORT_MIN_VALUE = 0;

	public static final int UNSIGNED_SHORT_MAX_VALUE = 65535;

	// (unsigned) int
	public static final byte INT_SIZE_IN_BYTE = 4;

	public static final byte INT = 4;

	public static final int INT_MIN_VALUE = Integer.MIN_VALUE;

	public static final int INT_MAX_VALUE = Integer.MAX_VALUE;

	public static final byte UNSIGNED_INT = 5;

	public static final byte UNSIGNED_INT_MIN_VALUE = 0;

	public static final long UNSIGNED_INT_MAX_VALUE = 4294967295L;

	// long
	public static final byte LONG_SIZE_IN_BYTE = 8;

	public static final byte LONG = 6;

	public static final long LONG_MIN_VALUE = Long.MIN_VALUE;

	public static final long LONG_MAX_VALUE = Long.MAX_VALUE;

	// char
	public static final byte CHAR_SIZE_IN_BYTE = 2;

	public static final byte CHAR = 7;

	public static final char CHAR_MIN_VALUE = Character.MIN_VALUE;

	public static final char CHAR_MAX_VALUE = Character.MAX_VALUE;

	// float
	public static final byte FLOAT_SIZE_IN_BYTE = 4;

	public static final byte FLOAT = 8;

	public static final float FLOAT_MIN_VALUE = Float.MIN_VALUE;

	public static final float FLOAT_MAX_VALUE = Float.MAX_VALUE;

	// double
	public static final byte DOUBLE_SIZE_IN_BYTE = Double.SIZE;

	public static final byte DOUBLE = 9;

	public static final double DOUBLE_MIN_VALUE = Double.MIN_VALUE;

	public static final double DOUBLE_MAX_VALUE = Double.MAX_VALUE;

	// internal representation
	private static final byte NB_MAX_OBJECTS_IN_BYTE = SHORT_SIZE_IN_BYTE;

	private static final byte NEXT_FREE_SIZE_IN_BYTE = SHORT_SIZE_IN_BYTE;

	private static final byte INDEX_SIZE_IN_BYTE = SHORT_SIZE_IN_BYTE;

	// META-DATA: for an instance: type (4 bits) dimensions (4 bits)
	private static final byte META_DATA_SIZE_IN_BYTE = 1;

	private static final byte NB_DIMENSIONS_SIZE_IN_BIT = 4;

	private static final byte NB_DIMENSIONS_OFFSET_IN_BIT = 0;

	private static final byte NB_DIMENSIONS_MASK = 0xF;

	private static final byte TYPE_SIZE_IN_BIT = 4;

	private static final byte TYPE_OFFSET_IN_BIT = NB_DIMENSIONS_OFFSET_IN_BIT + NB_DIMENSIONS_SIZE_IN_BIT;

	private static final byte TYPE_MASK = 0xF;

	// META-DATA: for a dimension
	private static final byte LENGTH_DIMENSION_SIZE_IN_BYTE = SHORT_SIZE_IN_BYTE;

	public static void init(short nbMaxObjects) {
		if (nbMaxObjects < 1) {
			throw new InvalidObjectsNumber(nbMaxObjects);
		}
		setNbMaxObjects(nbMaxObjects);
		setNextFree(NB_MAX_OBJECTS_IN_BYTE + NEXT_FREE_SIZE_IN_BYTE + (nbMaxObjects * INDEX_SIZE_IN_BYTE));
	}

	public static short getNbMaxObjects() {
		return RootbeerGpu.getSharedShort(0);
	}

	private static void setNbMaxObjects(short nbMaxObjects) {
		RootbeerGpu.setSharedShort(0, nbMaxObjects);
	}

	private static int getNextFree() {
		return fromUnsignedShort(RootbeerGpu.getSharedShort(NB_MAX_OBJECTS_IN_BYTE));
	}

	private static void setNextFree(int nextFree) {
		RootbeerGpu.setSharedShort(NB_MAX_OBJECTS_IN_BYTE, toUnsignedShort(nextFree));
	}

	public static int getFreeMemoryInByte() {
		return MAX_SIZE_SHARED_MEMORY_IN_BYTE - getNextFree();
	}

	/**
	 * @param type use the constants BYTE, UNSIGNED_BYTE, SHORT, UNSIGNED_SHORT, INT, UNSIGNED_INT, LONG, CHAR, FLOAT, DOUBLE
	 * @return The index of this object
	 */
	public synchronized static void createInstance(short id, byte type, short... lengthDimensions) {
		if (id < 0 || id >= getNbMaxObjects()) {
			throw new InvalidIdInstance(getNbMaxObjects(), id);
		}

		if (getStartsAt(id) > 0) {
			throw new InstanceAlreadyExists(id);
		}

		int nextFree = getNextFree();
		setStartsAt(id, nextFree);

		// save meta data into shared memory
		byte nbDimensions = (byte) lengthDimensions.length; // keep in memory since in OpenCL it's time consuming
		RootbeerGpu.setSharedByte(nextFree, getMetaData(type, nbDimensions));
		nextFree += META_DATA_SIZE_IN_BYTE;

		// move to the next free byte
		if (nbDimensions == 0) {
			nextFree += getSize(type);
		} else {
			nextFree += getSize(nextFree, type, nbDimensions, lengthDimensions);
		}
		setNextFree(nextFree);
	}

	private static byte getSize(byte type) {
		return getSizeInByte(type);
	}

	private static int getSize(int nextFree, byte type, byte nbDimensions, short[] lengthDimensions) {
		// save length of dimensions
		int size = 1;
		for (byte d = 0; d < nbDimensions; d++) {
			if (lengthDimensions[d] < 0) {
				throw new InvalidDimensionLength(lengthDimensions[d]);
			}
			setLengthDimension(nextFree, lengthDimensions[d]);
			size *= lengthDimensions[d];

			nextFree += LENGTH_DIMENSION_SIZE_IN_BYTE; // move to the next length dimension
		}
		// total size: dimensions + payload
		return (nbDimensions * LENGTH_DIMENSION_SIZE_IN_BYTE) + (getSizeInByte(type) * size);
	}

	private static int getIndex(short id, int cursor, short[] dimensions) {
		// read meta-data
		byte metaData = RootbeerGpu.getSharedByte(cursor);
		byte nbDimensions = getNbDimensions(metaData);

		if (dimensions.length != nbDimensions) {
			throw new InvalidDimensionsNumber(nbDimensions, dimensions.length);
		}

		cursor += META_DATA_SIZE_IN_BYTE;

		short dimensionLength;
		int index = 0;
		int sizePreviousDimension = 1;
		for (byte d = 0; d < nbDimensions; d++) {
			dimensionLength = getDimensionLength(cursor);

			// check that the dimensions are correct
			if (dimensions[d] < 0 || dimensions[d] >= dimensionLength) {
				throw new InvalidDimensionLength(dimensionLength, dimensions[d]);
			}

			index += dimensions[d] * sizePreviousDimension;
			sizePreviousDimension *= dimensionLength;

			cursor += LENGTH_DIMENSION_SIZE_IN_BYTE; // move to the next length dimension
		}

		return cursor + (index * getSizeInByte(getType(metaData)));
	}

	private static byte getSizeInByte(byte type) {
		switch (type) {
		case BYTE:
		case UNSIGNED_BYTE:
			return BYTE_SIZE_IN_BYTE;
		case SHORT:
		case UNSIGNED_SHORT:
			return SHORT_SIZE_IN_BYTE;
		case INT:
		case UNSIGNED_INT:
			return INT_SIZE_IN_BYTE;
		case LONG:
			return LONG_SIZE_IN_BYTE;
		case CHAR:
			return CHAR_SIZE_IN_BYTE;
		case FLOAT:
			return FLOAT_SIZE_IN_BYTE;
		case DOUBLE:
			return DOUBLE_SIZE_IN_BYTE;
		default:
			throw new IllegalArgumentException();
		}
	}

	private static void setStartsAt(short id, int startAt) {
		RootbeerGpu.setSharedShort(NB_MAX_OBJECTS_IN_BYTE + NEXT_FREE_SIZE_IN_BYTE + (id * INDEX_SIZE_IN_BYTE),
				toUnsignedShort(startAt));
	}

	private static int getStartsAt(short id) {
		return fromUnsignedShort(RootbeerGpu.getSharedShort(NB_MAX_OBJECTS_IN_BYTE + NEXT_FREE_SIZE_IN_BYTE
				+ (id * INDEX_SIZE_IN_BYTE)));
	}

	private static int getStartsAtForExisting(short id) {
		short nbMaxObjects = getNbMaxObjects();
		if (nbMaxObjects == 0) {
			throw new SharedMemoryManagerNotInitialized();
		}
		if (id < 0 || id >= nbMaxObjects) {
			throw new InvalidIdInstance(nbMaxObjects, id);
		}

		int startsAt = getStartsAt(id);
		if (startsAt == 0) {
			throw new InstanceNotExists(id);
		}
		return startsAt;
	}

	public static boolean instanceExists(short id) {
		return getStartsAt(id) != 0;
	}

	public static byte getByte(short id, short... dimensions) {
		return RootbeerGpu.getSharedByte(getIndex(id, getStartsAtForExisting(id), dimensions));
	}

	public static short getUnsignedByte(short id, short... dimensions) {
		return fromUnsignedByte(RootbeerGpu.getSharedByte(getIndex(id, getStartsAtForExisting(id), dimensions)));
	}

	public static short getShort(short id, short... dimensions) {
		return RootbeerGpu.getSharedShort(getIndex(id, getStartsAtForExisting(id), dimensions));
	}

	public static int getUnsignedShort(short id, short... dimensions) {
		return fromUnsignedShort(RootbeerGpu.getSharedShort(getIndex(id, getStartsAtForExisting(id), dimensions)));
	}

	public static int getInt(short id, short... dimensions) {
		return RootbeerGpu.getSharedInteger(getIndex(id, getStartsAtForExisting(id), dimensions));
	}

	public static long getUnsignedInt(short id, short... dimensions) {
		return fromUnsignedInt(RootbeerGpu.getSharedInteger(getIndex(id, getStartsAtForExisting(id), dimensions)));
	}

	public static long getLong(short id, short... dimensions) {
		return RootbeerGpu.getSharedLong(getIndex(id, getStartsAtForExisting(id), dimensions));
	}

	public static char getChar(short id, short... dimensions) {
		return RootbeerGpu.getSharedChar(getIndex(id, getStartsAtForExisting(id), dimensions));
	}

	public static float getFloat(short id, short... dimensions) {
		return RootbeerGpu.getSharedFloat(getIndex(id, getStartsAtForExisting(id), dimensions));
	}

	public static double getDouble(short id, short... dimensions) {
		return RootbeerGpu.getSharedDouble(getIndex(id, getStartsAtForExisting(id), dimensions));
	}

	public static void setByte(short id, byte value, short... dimensions) {
		RootbeerGpu.setSharedByte(getIndex(id, getStartsAtForExisting(id), dimensions), value);
	}

	public static void setUnsignedByte(short id, short value, short... dimensions) {
		RootbeerGpu.setSharedByte(getIndex(id, getStartsAtForExisting(id), dimensions), toUnsignedByte(value));
	}

	public static void setShort(short id, short value, short... dimensions) {
		RootbeerGpu.setSharedShort(getIndex(id, getStartsAtForExisting(id), dimensions), value);
	}

	public static void setUnsignedShort(short id, int value, short... dimensions) {
		RootbeerGpu.setSharedShort(getIndex(id, getStartsAtForExisting(id), dimensions), toUnsignedShort(value));
	}

	public static void setInt(short id, int value, short... dimensions) {
		RootbeerGpu.setSharedInteger(getIndex(id, getStartsAtForExisting(id), dimensions), value);
	}

	public static void setUnsignedInt(short id, long value, short... dimensions) {
		RootbeerGpu.setSharedInteger(getIndex(id, getStartsAtForExisting(id), dimensions), toUnsignedInt(value));
	}

	public static void setLong(short id, long value, short... dimensions) {
		RootbeerGpu.setSharedLong(getIndex(id, getStartsAtForExisting(id), dimensions), value);
	}

	public static void setChar(short id, char value, short... dimensions) {
		RootbeerGpu.setSharedChar(getIndex(id, getStartsAtForExisting(id), dimensions), value);
	}

	public static void setFloat(short id, float value, short... dimensions) {
		RootbeerGpu.setSharedFloat(getIndex(id, getStartsAtForExisting(id), dimensions), value);
	}

	public static void setDouble(short id, double value, short... dimensions) {
		RootbeerGpu.setSharedDouble(getIndex(id, getStartsAtForExisting(id), dimensions), value);
	}

	private static short getDimensionLength(int index) {
		return RootbeerGpu.getSharedShort(index);
	}

	private static void setLengthDimension(int nextFree, short length) {
		RootbeerGpu.setSharedShort(nextFree, length);
	}

	private static byte getType(byte metaData) {
		return getData(metaData, TYPE_OFFSET_IN_BIT, TYPE_MASK);
	}

	private static byte getNbDimensions(byte metaData) {
		return getData(metaData, NB_DIMENSIONS_OFFSET_IN_BIT, NB_DIMENSIONS_MASK);
	}

	private static byte getData(byte data, byte offsetInBit, byte mask) {
		return (byte) ((data >>> offsetInBit) & mask);
	}

	private static byte getMetaData(byte type, byte nbDimensions) {
		if (nbDimensions > NB_MAX_DIMENSIONS) {
			throw new InvalidDimensionsNumber(NB_MAX_DIMENSIONS, nbDimensions);
		}

		byte metaData = type;
		metaData <<= NB_DIMENSIONS_SIZE_IN_BIT;
		metaData |= nbDimensions;

		return metaData;
	}

	private static byte toUnsignedByte(short s) {
		if (s < UNSIGNED_BYTE_MIN_VALUE || s > UNSIGNED_BYTE_MAX_VALUE) {
			throw new InvalidUnsignedByte(s);
		}
		return (byte) s;
	}

	private static short fromUnsignedByte(byte b) {
		return (short) (b & 0xFF);
	}

	private static short toUnsignedShort(int i) {
		if (i < UNSIGNED_SHORT_MIN_VALUE || i > UNSIGNED_SHORT_MAX_VALUE) {
			throw new InvalidUnsignedShort(i);
		}
		return (short) i;
	}

	private static int fromUnsignedShort(short s) {
		return s & 0xFFFF;
	}

	private static int toUnsignedInt(long l) {
		if (l < UNSIGNED_INT_MIN_VALUE || l > UNSIGNED_INT_MAX_VALUE) {
			throw new InvalidUnsignedInt(l);
		}
		return (int) l;
	}

	private static long fromUnsignedInt(int i) {
		return i & 0xFFFFFFFFL;
	}
}