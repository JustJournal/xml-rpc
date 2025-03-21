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
 *  Serializes arrays of booleans.
 * 
 *  @author Greger Olsson
 */

public class BooleanArraySerializer implements XmlRpcCustomSerializer
{
    /*  (Documentation inherited)
     *  @see redstone.xmlrpc.XmlRpcCustomSerializer#getSupportedClass()
     */
    
    public Class getSupportedClass()
    {
        return boolean[].class;
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

        boolean[] array = ( boolean[] ) value;

        for (boolean b : array) {
            writer.write("<value><boolean>");
            writer.write(b ? "1" : "0");
            writer.write("</boolean></value>");
        }

        writer.write( "</data></array>" );
    }
}