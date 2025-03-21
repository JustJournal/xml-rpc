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

import java.io.IOException;
import java.io.Writer;
import com.justjournal.xmlrpc.XmlRpcCustomSerializer;
import com.justjournal.xmlrpc.XmlRpcException;
import com.justjournal.xmlrpc.XmlRpcSerializer;

/**
 *  Serializes arrays of primitive longs. Note that unless
 *  setUseApacheExtension( true ) has been invoked, the longs are demoted to
 *  integers before being serialized into regular XML-RPC &lt;i4>'s, possibly
 *  losing significant bits in the conversion.
 * 
 *  @author Greger Olsson
 */

public class LongArraySerializer implements XmlRpcCustomSerializer
{
    /*  (Documentation inherited)
     *  @see redstone.xmlrpc.XmlRpcCustomSerializer#getSupportedClass()
     */
    
    public Class getSupportedClass()
    {
        return long[].class;
    }

    
    /**
     *  Sets whether to use the &lt;i8> Apache extensions when
     *  serializing longs.
     *
     *  @param useApacheExtension Flag for specifying the Apache extension to be used.
     */

    public void setUseApacheExtension( boolean useApacheExtension )
    {
        this.useApacheExtension = useApacheExtension;
    }
    

    /*  (Documentation inherited)
     *  @see redstone.xmlrpc.XmlRpcCustomSerializer#serialize(java.lang.Object, java.io.Writer, redstone.xmlrpc.XmlRpcSerializer)
     */
    
    public void serialize(
        Object value,
        Writer writer,
        XmlRpcSerializer builtInSerializer )
        throws XmlRpcException, IOException
    {
        writer.write( "<array><data>" );

        long[] array = ( long[] ) value;

        for (long l : array) {
            if (!useApacheExtension) {
                writer.write("<value><i4>");
                writer.write(Integer.toString((int) l));
                writer.write("</i4></value>");
            } else {
                writer.write("<value><i8 xmlns=\"http://ws.apache.org/xmlrpc/namespaces/extensions\">");
                writer.write(Long.toString(l));
                writer.write("</i8></value>");
            }
        }

        writer.write( "</data></array>" );
    }
    
    
    /** Flag indicating whether the Apache &lt;i8> extension should be used. */
    private boolean useApacheExtension;
}