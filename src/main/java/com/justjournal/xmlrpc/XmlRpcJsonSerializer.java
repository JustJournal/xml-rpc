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

import com.justjournal.xmlrpc.serializers.json.*;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The XmlRpcJsonSerializer class converts Java objects to their JSON counterparts. It inherently
 * supports basic object types like String, Integer, Double, Float, Boolean, Date, and byte arrays.
 * For other types of objects, custom serializers need to be registered. The Redstone XML-RPC
 * library comes with a set of useful serializers for collections and other types of objects. @see
 * the redstone.xmlrpc.serializers.json .
 *
 * <p>Although this is not what you would expect to find in an XML-RPC library, implementing a JSON
 * serializer required very little effort and gives great value for JavaScript clients that wants to
 * use XML-RPC services in their AJAX implementations. It is easy to create XML-RPC compatible
 * messages in JavaScript but less easy to parse the response which is not required using this
 * format, just use eval( responseText ) to get a JavaScript object.
 *
 * <p>TODO Change synchronization of global dateFormatter to prevent bottleneck.
 *
 * @author Greger Olsson
 */
public class XmlRpcJsonSerializer extends XmlRpcSerializer {
  /** Shared date formatter shared. */
  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-dd-mm HH:mm:ss");

  /** Constructor adding all core custom serializers. */
  public XmlRpcJsonSerializer() {
    this(true);
  }

  /**
   * Constructor that may add all the custom serializers in the library (which is almost always what
   * you want).
   *
   * @param addCustomSerializers Indicates if the core custom serializers should be added.
   */
  public XmlRpcJsonSerializer(boolean addCustomSerializers) {
    super(false);

    if (addCustomSerializers) {
      customSerializers.add(new MapSerializer());
      customSerializers.add(new ListSerializer());
      customSerializers.add(new CollectionSerializer());
      customSerializers.add(new ObjectArraySerializer());
      customSerializers.add(new IntArraySerializer());
      customSerializers.add(new FloatArraySerializer());
      customSerializers.add(new DoubleArraySerializer());
      customSerializers.add(new BooleanArraySerializer());
      customSerializers.add(new IntrospectingSerializer());
    }
  }

  /**
   * Writes the opening character of a JSON envelope.
   * This method overrides the default serializing mechanism to use JSON format instead of XML.
   *
   * @param value The object to be serialized (not used in this implementation).
   * @param writer The Writer object to which the JSON envelope header will be written.
   * @throws IOException If an I/O error occurs while writing to the Writer.
   */
  @Override
  public void writeEnvelopeHeader(Object value, Writer writer) throws IOException {
    writer.write('(');
  }

  /**
   * Writes the closing character of a JSON envelope.
   * This method overrides the default serializing mechanism to use JSON format instead of XML.
   *
   * @param value The object being serialized (not used in this implementation).
   * @param writer The Writer object to which the JSON envelope footer will be written.
   * @throws IOException If an I/O error occurs while writing to the Writer.
   */
  @Override
  public void writeEnvelopeFooter(Object value, Writer writer) throws IOException {
    writer.write(')');
  }

  /**
   * Writes an error message in JSON format. This method overrides the default serializing mechanism
   * to use JSON format instead.
   *
   * @param message The error message to be written.
   * @param writer The Writer object to which the JSON-formatted error message will be written.
   * @throws IOException If an I/O error occurs while writing to the Writer.
   */
  public void writeError(String message, Writer writer) throws IOException {
    writer.write('\'');
    writer.write(message);
    writer.write('\'');
  }

  /**
   * Serializes an object to JSON format.
   * This method overrides the default serializing mechanism to use JSON format instead of XML.
   * It handles basic types (String, Character, Number, Boolean, Calendar, Date) directly,
   * and uses custom serializers for other types.
   *
   * @param value  The object to be serialized. Can be of various types including String,
   *               Character, Number, Boolean, Calendar, Date, or custom types.
   * @param writer The Writer object to which the JSON-formatted output will be written.
   * @throws XmlRpcException If the object type is not supported and no custom serializer is found.
   * @throws IOException     If an I/O error occurs while writing to the Writer.
   */
  @Override
  public void serialize(Object value, Writer writer) throws XmlRpcException, IOException {
    if (value instanceof String || value instanceof Character) {
      writer.write('\'');
      writer.write(value.toString());
      writer.write('\'');
    } else if (value instanceof Number || value instanceof Boolean) {
      writer.write(value.toString());
    } else if (value instanceof java.util.Calendar) {
      writer.write("new Date('");
      synchronized (dateFormatter) {
        writer.write(dateFormatter.format(((Calendar) value).getTime()));
      }
      writer.write("')");
    } else if (value instanceof java.util.Date) {
      writer.write("new Date('");
      synchronized (dateFormatter) {
        writer.write(dateFormatter.format((Date) value));
      }
      writer.write("')");
    } else {
      // Value was not of basic type, see if there's a custom serializer
      // registered for it.

      for (XmlRpcCustomSerializer customSerializer : customSerializers) {

          if (customSerializer.getSupportedClass().isInstance(value)) {
          customSerializer.serialize(value, writer, this);
          return;
        }
      }

      throw new XmlRpcException(
          XmlRpcMessageBundle.getString("XmlRpcSerializer.UnsupportedType") + value.getClass());
    }
  }
}
