package vn.co.taxinet.mobile.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Base64;

import vn.co.taxinet.mobile.exception.SystemException;
import vn.co.taxinet.mobile.exception.TNException;

/**
 * Used to encode an object to a BASE64 encoded string so it can be easily
 * displayed in a field on web page <B>Modification history:</B><br>
 * 
 * @author
 * @version 1.0 - 01 June 2015
 * @since TaxiNet version 1.0
 */

public abstract class ObjectEncoder {

	private static final String THIS = "ObjectEncoder";

	private static final String CHARSET = "ISO-8859-1";

	/**
	 * Convert a byte array to an object
	 * 
	 * @param bytes
	 *            [] - an array encapsulates data of the object to be created
	 * @return Object - newly created object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object bytesToObject(byte bytes[]) throws IOException,
			ClassNotFoundException {
		ObjectInputStream in = null;
		ByteArrayInputStream bs = null;
		try {
			bs = new ByteArrayInputStream(bytes);
			in = new ObjectInputStream(bs);
			return in.readObject();
		} finally {
			if (in != null) {
				in.close();
			}
			if (bs != null) {
				bs.close();
			}
		}
	}

	/**
	 * Convert an object to a byte array
	 * 
	 * @param Object
	 *            - Object that want to be convert to byte array
	 * @return bytes array - encapsulates data of the object
	 * @throws IOException
	 */
	public static byte[] objectToBytes(Object object) throws IOException {
		ObjectOutputStream out = null;
		ByteArrayOutputStream bs = null;
		try {
			bs = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bs);
			out.writeObject(object);
			return bs.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
			if (bs != null) {
				bs.close();
			}
		}
	}

	/**
	 * Convert an object to a BASE64 encoded String
	 * 
	 * @param Object
	 * @return String
	 * @throws IOException
	 */
	public static String objectToString(Object object) throws TNException {
		ObjectOutputStream out = null;
		ByteArrayOutputStream bs = null;
		try {
			bs = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bs);
			out.writeObject(object);
			String result = new String(Base64.encodeBase64(bs.toByteArray()));
			out.close();
			bs.close();
			return result;
		} catch (Exception e) {
			throw new SystemException(THIS + "::objectToString()", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ioe) {
				}
			}
			if (bs != null) {
				try {
					bs.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	/**
	 * Convert a BASE64 encoded String to an Object
	 * 
	 * @param String
	 * @return Object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object stringToObject(String string) throws TNException {
		ObjectInputStream in = null;
		ByteArrayInputStream bs = null;
		try {
			bs = new ByteArrayInputStream(
					Base64.decodeBase64(string.getBytes()));
			in = new ObjectInputStream(bs);
			Object result = in.readObject();
			in.close();
			bs.close();
			return result;
		} catch (Exception e) {
			throw new SystemException(THIS + "::stringToObject()", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
				}
			}
			if (bs != null) {
				try {
					bs.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	public static byte[] stringToByteArray(String string) throws IOException {
		return Base64.decodeBase64(string.getBytes());
	}

	private static String compress(String original) throws TNException {
		String LOCATION = THIS + "::compress()";
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DeflaterOutputStream dos = new DeflaterOutputStream(bos);
			ObjectOutputStream oos = new ObjectOutputStream(dos);
			oos.writeObject(original);
			oos.flush();
			oos.close();

			return bos.toString(CHARSET);
		} catch (Exception e) {
			throw new SystemException(LOCATION, e);
		}
	}

	private static String uncompress(String compressed) throws TNException {
		String LOCATION = THIS + "::uncompress()";
		try {
			byte[] tmp = compressed.getBytes(CHARSET);

			ByteArrayInputStream bais = new ByteArrayInputStream(tmp);
			ObjectInputStream ois = new ObjectInputStream(
					new InflaterInputStream(bais));
			String inputString = (String) ois.readObject();
			ois.close();

			return inputString;
		} catch (Exception e) {
			throw new SystemException(LOCATION, e);
		}
	}

	public static byte[] stringToBytes(String json) throws IOException {
		OutputStream out = null;
		ByteArrayOutputStream bs = null;
		try {
			bs = new ByteArrayOutputStream();
			out = new ByteArrayOutputStream();
			out.write(json.getBytes());
			return bs.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
			if (bs != null) {
				bs.close();
			}
		}
	}

	public static String bytesToString(byte bytes[]) throws IOException,
			ClassNotFoundException {
		InputStream in = null;
		ByteArrayInputStream bs = null;
		try {
			bs = new ByteArrayInputStream(bytes);
			in = new ByteArrayInputStream(bytes);
			return in.toString();
		} finally {
			if (in != null) {
				in.close();
			}
			if (bs != null) {
				bs.close();
			}
		}
	}
}