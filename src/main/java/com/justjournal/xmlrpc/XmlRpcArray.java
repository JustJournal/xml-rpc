/*
    Copyright (c) 2005 Redstone Handelsbolag

    This library is free software; you can redistribute it and/or modify it under the terms
    of the GNU Lesser General Public License as published by the Free Software Foundation;
    either version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License along with this
    library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
    Boston, MA  02111-1307  USA
*/

package com.justjournal.xmlrpc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * An Array represents an XML-RPC array in its Java form. Essentially, it's just a
 * java.util.ArrayList with utility methods for extracting members of the data types supported by
 * the XML-RPC library. The class does not introduce any new field members which makes it no more
 * expensive than a regular ArrayList.
 *
 * <p>To extract nested values from the list, use the new simplified accessors which perform the
 * casting for you:
 *
 * <pre>
 *  boolean boolean = myList.getStruct( 0 ).getBoolean( "someBoolean" );
 *  </pre>
 *
 * @author Greger Olsson
 */
public class XmlRpcArray extends ArrayList<Object> {
  /**
   * Retrieves the String value at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the String stored at the given index in the array. It performs
   * a cast to String, which may throw a ClassCastException if the value at the specified
   * index is not actually a String.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The String value at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not a String.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public String getString(int index) {
    return (String) get(index);
  }

  /**
   * Retrieves the boolean value at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the boolean value stored at the given index in the array.
   * It performs a cast to Boolean and then unboxes it to a primitive boolean.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The boolean value at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not a Boolean.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public boolean getBoolean(int index) {
    return (Boolean) get(index);
  }

  /**
   * Retrieves the Boolean wrapper object at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the Boolean object stored at the given index in the array.
   * It performs a cast to Boolean, which may throw a ClassCastException if the value
   * at the specified index is not actually a Boolean.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The Boolean wrapper object at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not a Boolean.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public Boolean getBooleanWrapper(int index) {
    return (Boolean) get(index);
  }

  /**
   * Retrieves the integer value at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the integer value stored at the given index in the array.
   * It performs a cast to Integer and then unboxes it to a primitive int.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The integer value at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not an Integer.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public int getInteger(int index) {
    return (Integer) get(index);
  }

  /**
   * Retrieves the Integer wrapper object at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the Integer object stored at the given index in the array.
   * It performs a cast to Integer, which may throw a ClassCastException if the value
   * at the specified index is not actually an Integer.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The Integer wrapper object at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not an Integer.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public Integer getIntegerWrapper(int index) {
    return (Integer) get(index);
  }

  /**
   * Retrieves the double value at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the double value stored at the given index in the array.
   * It performs a cast to Double and then unboxes it to a primitive double.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The double value at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not a Double.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public double getDouble(int index) {
    return (Double) get(index);
  }

  /**
   * Retrieves the Double wrapper object at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the Double object stored at the given index in the array.
   * It performs a cast to Double, which may throw a ClassCastException if the value
   * at the specified index is not actually a Double.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The Double wrapper object at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not a Double.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public Double getDoubleWrapper(int index) {
    return (Double) get(index);
  }

  /**
   * Retrieves the XmlRpcArray at the specified index in this XmlRpcArray.
   *
   * <p>This method returns the XmlRpcArray stored at the given index in the array.
   * It performs a cast to XmlRpcArray, which may throw a ClassCastException if the value
   * at the specified index is not actually an XmlRpcArray.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The XmlRpcArray at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not an XmlRpcArray.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public XmlRpcArray getArray(int index) {
    return (XmlRpcArray) get(index);
  }

  /**
   * Retrieves the XmlRpcStruct at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the XmlRpcStruct stored at the given index in the array.
   * It performs a cast to XmlRpcStruct, which may throw a ClassCastException if the value
   * at the specified index is not actually an XmlRpcStruct.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The XmlRpcStruct at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not an XmlRpcStruct.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public XmlRpcStruct getStruct(int index) {
    return (XmlRpcStruct) get(index);
  }

  /**
   * Retrieves the Date object at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the Date object stored at the given index in the array.
   * It performs a cast to Date, which may throw a ClassCastException if the value
   * at the specified index is not actually a Date object.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The Date object at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not a Date object.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public Date getDate(int index) {
    return (Date) get(index);
  }

  /**
   * Retrieves the byte array at the specified index in the XmlRpcArray.
   *
   * <p>This method returns the byte array stored at the given index in the array.
   * It performs a cast to byte[], which may throw a ClassCastException if the value
   * at the specified index is not actually a byte array.
   *
   * @param index The zero-based index of the element to retrieve from the array.
   * @return The byte array at the specified index in the array.
   * @throws ClassCastException if the value at the given index is not a byte array.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public byte[] getBinary(int index) {
    return (byte[]) get(index);
  }

  /**
   * Retrieves the byte array at the specified index in the XmlRpcArray as an InputStream.
   *
   * <p>This method converts the byte array stored at the given index into a ByteArrayInputStream,
   * allowing for convenient streaming access to the binary data.
   *
   * @param index The index of the byte array in the XmlRpcArray to be converted to an InputStream.
   *     The index is zero-based.
   * @return An InputStream (specifically, a ByteArrayInputStream) containing the binary data stored
   *     at the specified index.
   * @throws ClassCastException if the value at the given index is not a byte array.
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;=
   *     size()).
   */
  public InputStream getBinaryAsStream(int index) {
    return new ByteArrayInputStream((byte[]) get(index));
  }

  /** Serial version UID. */
  private static final long serialVersionUID = 3256446889107863860L;
}
