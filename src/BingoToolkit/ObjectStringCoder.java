package BingoToolkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/*
 * Example to Use:
 * 
 * Needed Example Class:
 * 
 *  Test subject. A very simple class.  
 *class SomeClass implements Serializable {
 *
 *   private final static long serialVersionUID = 1; // See Nick's comment below
 *
 *    int i    = Integer.MAX_VALUE;
 *    String s = "ABCDEFGHIJKLMNOP";
 *    Double d = new Double( -1.0 );
 *    public String toString(){
 *        return  "SomeClass instance says: Don't worry, " 
 *              + "I'm healthy. Look, my data is i = " + i  
 *              + ", s = " + s + ", d = " + d;
 *    }
 *}
 * 
 * Main:
 * 
 *   String string = toString( new SomeClass() );
 *   System.out.println(" Encoded serialized version " );
 *   System.out.println( string );
 *   SomeClass some = ( SomeClass ) fromString( string );
 *   System.out.println( "\n\nReconstituted object");
 *   System.out.println( some );
 * 
 * Output in Console:
 * 
 *  "Encoded serialized version 
 *rO0ABXNyABZCaW5nb1Rvb2xraXQuU29tZUNsYXNzAAAAAAAAAAECAANJAAFpTAABZHQAEkxqYXZhL2xhbmcvRG91YmxlO0wAAXN0ABJMamF2YS9sYW5nL1N0cmluZzt4cH////9zcgAQamF2YS5sYW5nLkRvdWJsZYCzwkopa/sEAgABRAAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHC/8AAAAAAAAHQAEEFCQ0RFRkdISUpLTE1OT1A=
 *
 *
 *Reconstituted object
 *SomeClass instance says: Don't worry, I'm healthy. Look, my data is i = 2147483647, s = ABCDEFGHIJKLMNOP, d = -1.0"
 *
 */

public class ObjectStringCoder {

	public ObjectStringCoder() {

	}

	/** Read the object from Base64 string. */
	public static Object stringToObject(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	/** Write the object to a Base64 string. */
	public static String objectToString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}
}