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

package com.justjournal.xmlrpc.serializers;

import com.justjournal.xmlrpc.XmlRpcCustomSerializer;
import com.justjournal.xmlrpc.XmlRpcException;
import com.justjournal.xmlrpc.XmlRpcSerializer;
import java.io.IOException;
import java.io.Writer;

/**
 *  Serializes a Long wrapper. Note that unless setUseApacheExtension( true )
 *  has been invoked, the longs are demoted to integers before being serialized
 *  into regular XML-RPC &lt;i4&gt;'s, possibly losing significant bits in the
 *  conversion.
 * 
 *  @author Greger Olsson
 */
public class LongWrapperSerializer implements XmlRpcCustomSerializer
{
    /*  (Documentation inherited)
     *  @see redstone.xmlrpc.XmlRpcCustomSerializer#getSupportedClass()
     */
    
    /** Flag indicating whether the Apache i8 extension should be used. */
    private boolean useApacheExtension;

    public Class getSupportedClass()
    {
        return Long.class;
    }

    
    /*  (Documentation inherited)
     *  @see redstone.xmlrpc.XmlRpcCustomSerializer#serialize(java.lang.Object, java.io.Writer, redstone.xmlrpc.XmlRpcSerializer)
     */

    /**
     * Sets whether to use the &lt;i8&gt; Apache extensions when serializing longs.
     * <p>
     * This method configures the serializer to use the Apache XML-RPC extension
     * for long integers. When enabled, it allows for serialization of 64-bit
     * integers using the &lt;i8&gt; tag instead of the standard &lt;i4&gt; tag,
     * which is limited to 32-bit integers.
     *
     * @param useApacheExtension A boolean flag indicating whether to use the Apache
     *                           extension. If true, the serializer will use the
     *                           &lt;i8&gt; tag for longs. If false, it will use
     *                           the standard &lt;i4&gt; tag, potentially losing
     *                           precision for values outside the 32-bit range.
     */
    public void setUseApacheExtension( boolean useApacheExtension )
    {
        this.useApacheExtension = useApacheExtension;
    }

    /**
     * Serializes a Long value into XML-RPC format.
     * <p>
     * This method converts a Long object into its XML-RPC representation. If the Apache
     * extension is not enabled, the long value is converted to an integer and serialized
     * as an &lt;i4&gt; element. If the Apache extension is enabled, the long value is
     * serialized as an &lt;i8&gt; element, preserving its full 64-bit precision.
     *
     * @param value The Long object to be serialized. Must not be null.
     * @param writer The Writer object to which the XML-RPC representation will be written.
     * @param builtInSerializer The XmlRpcSerializer that can be used for serializing nested objects.
     *                          Not used in this implementation but required by the interface.
     * @throws XmlRpcException If there's an error during the XML-RPC serialization process.
     * @throws IOException If there's an error writing to the output.
     */
    public void serialize(
        Object value,
        Writer writer,
        XmlRpcSerializer builtInSerializer )
        throws XmlRpcException, IOException
    {
        Long longValue = ( Long ) value;

        if ( !useApacheExtension )
        {
            writer.write( "<i4>" );
            writer.write( Integer.toString( longValue.intValue() ) );
            writer.write( "</i4>" );
        }
        else
        {
            writer.write( "<i8 xmlns=\"http://ws.apache.org/xmlrpc/namespaces/extensions\">" );
            writer.write( Long.toString(longValue) );
            writer.write( "</i8>" );
        }
    }
}