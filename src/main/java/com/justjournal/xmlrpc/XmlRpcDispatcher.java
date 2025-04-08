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

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;

/**
 *  Objects of the XmlRpcDispatcher class perform the parsing of inbound XML-RPC
 *  messages received by an XmlRpcServer and are responsible for invoking handlers and
 *  dealing with their return values and exceptions.
 *
 *  @author  Greger Olsson
 */
@Slf4j
public class XmlRpcDispatcher extends XmlRpcParser
{
    /** The default name for the default handler */
    public static String DEFAULT_HANDLER_NAME = "__default__";
    /** The current call sequence for traceability */
    private static int callSequence;
    /** The XmlRpcServer this dispatcher is working for */
    private final XmlRpcServer server;
    /**
     * The IP address of the client being dispatched.
     * <p>
     * This field stores the IP address of the client that initiated the XML-RPC request.
     * It can be used for logging, access control, or other client-specific operations.
     *
     * @return the IP address of the client making the XML-RPC call
     */
    @Getter
    private final String callerIp;
    /** The arguments for the method */
    private final List arguments = new ArrayList( 6 );
    /** The name of the method the client wishes to call */
    private String methodName;
    /** Holds the XML-RPC response as it is built up */
    private Writer writer;


    /**
     * Creates a dispatcher and associates it with an XmlRpcServer and the IP address
     * of the calling client.
     *
     * @param server The XmlRpcServer instance that this dispatcher will work for.
     *               It is used to handle XML-RPC requests and manage invocation handlers.
     * @param callerIp The IP address of the client making the XML-RPC call.
     *                 This is stored for potential use in logging or access control.
     */
    public XmlRpcDispatcher( XmlRpcServer server, String callerIp )
    {
        this.server = server;
        this.callerIp = callerIp;
    }

    /**
     * Dispatches inbound XML-RPC messages to the appropriate handlers.
     * This method parses the incoming XML message, locates the appropriate invocation handler,
     * and processes the request through pre-processing, invocation, and post-processing stages.
     * <p>
     * The method performs the following steps:
     * 1. Parses the inbound XML-RPC message.
     * 2. Determines the handler and method name from the parsed message.
     * 3. Retrieves the appropriate invocation handler.
     * 4. Creates an XmlRpcInvocation object if there are any invocation interceptors.
     * 5. Executes pre-processing, invocation, and post-processing steps.
     * 6. Handles any exceptions that occur during the process.
     * 7. Writes the response or error message to the output.
     *
     * @param xmlInput The InputStream containing the XML-RPC request message to be processed.
     * @param xmlOutput The Writer to which the XML-RPC response will be written.
     * @throws XmlRpcException When the inbound XML message cannot be parsed due to no
     *                         available SAX driver, or when an invalid message was received.
     *                         All other exceptions are caught and encoded within the
     *                         XML-RPC writer.
     */
    public void dispatch( InputStream xmlInput, Writer xmlOutput ) throws XmlRpcException
    {
        // Parse the inbound XML-RPC message. May throw an exception.

        parse( xmlInput );

        // Response is written directly to the Writer supplied by the XmlRpcServer.

        this.writer = xmlOutput;

        // Exceptions will from hereon be encoded in the XML-RPC response.

        int separator = methodName.lastIndexOf( "." );

        if ( separator == -1 )
        {
            methodName = DEFAULT_HANDLER_NAME + "." + methodName;
            separator = DEFAULT_HANDLER_NAME.length();
        }

        final String handlerName = methodName.substring( 0, separator );
        methodName = methodName.substring( separator + 1 );

        XmlRpcInvocationHandler handler = server.getInvocationHandler( handlerName );

        if ( handler != null )
        {
            final int callId = ++callSequence;
            XmlRpcInvocation invocation = null;

            if (!server.getInvocationInterceptors().isEmpty()) {
                invocation = new XmlRpcInvocation(
                    callId,
                    handlerName,
                    methodName,
                    handler,
                    arguments,
                    writer );
            }

            try
            {
                // Invoke the method, which may throw any kind of exception. If any of the
                // preProcess calls thinks the invocation should be cancelled, we do so.

                if ( !preProcess( invocation ) )
                {
                    writeError( -1, XmlRpcMessageBundle.getString( "XmlRpcDispatcher.InvocationCancelled" ) );
                }
                else
                {
                    Object returnValue = handler.invoke( methodName, arguments );
                    returnValue = postProcess( invocation, returnValue );

                    // If the return value wasn't intercepted by any of the interceptors,
                    // write the response using the current serlialization mechanism.

                    if ( returnValue != null )
                    {
                        writeValue( returnValue );
                    }
                }
            }
            catch ( Throwable t )
            {
                processException( invocation, t );

                int code = -1;
                if ( t instanceof XmlRpcFault )
                {
                    code = ( (XmlRpcFault) t ).getErrorCode();
                }

                writeError( code, t.getClass().getName() + ": " + t.getMessage() );
            }
        }
        else
        {
            writeError( -1, XmlRpcMessageBundle.getString( "XmlRpcDispatcher.HandlerNotFound" ) );
        }
    }

  /**
   * Override the endElement() method of the XmlRpcParser class, and catch the method name element.
   * The method name element is unique for XML-RPC calls, and belongs here in the server.
   *
   * @param uri      The XML namespace URI.
   * @param name      The local name of the element.
   * @param qualifiedName The qualified name of the element.
   */
  @Override
  public void endElement(String uri, String name, String qualifiedName) throws SAXException {
        if ( name.equals( "methodName" ) )
        {
            methodName = this.consumeCharData();
        }
        else
        {
            super.endElement( uri, name, qualifiedName );
        }
    }

    /**
     *  Implementation of abstract method introduced in XmlRpcParser. It will
     *  be called whenever a value is parsed during a parse() call. In this
     *  case, the parsed values represent arguments to be sent to the invocation
     *  handler of the call.
     *
     * @param value The parsed value.
     */
    protected void handleParsedValue( Object value )
    {
        arguments.add( value );
    }

    /**
     *  Invokes all processor objects registered with the XmlRpcServer this dispatcher is
     *  working for.
     *
     *  @todo Determine a way for a preProcess call to indicate the reason for cancelling
     *        the invocation.
     *  @param invocation The invocation being processed.
     *  @return true if the invocation should continue, or false if the invocation should
     *          be cancelled for some reason.
     */
    private boolean preProcess( XmlRpcInvocation invocation )
    {
        XmlRpcInvocationInterceptor p;

        for ( int i = 0; i < server.getInvocationInterceptors().size(); ++i )
        {
            p = server.getInvocationInterceptors().get( i );

            if ( !p.before( invocation ) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Invokes all interceptor objects registered with the XmlRpcServer this dispatcher is
     * working for. This method is responsible for post-processing the invocation result.
     *
     * @param invocation The XmlRpcInvocation object representing the current invocation.
     * @param returnValue The initial return value from the invocation handler.
     * @return The final processed return value, or null if an interceptor has taken
     *         responsibility for writing the response directly to the client.
     */
    private Object postProcess( XmlRpcInvocation invocation, Object returnValue )
    {
        XmlRpcInvocationInterceptor p;

        for ( int i = 0; i < server.getInvocationInterceptors().size(); ++i )
        {
            p = server.getInvocationInterceptors().get( i );
            returnValue = p.after( invocation, returnValue );

            // If the interceptor intercepts the return value completely and takes
            // responsibility for writing a response directly to the client, break
            // the interceptor chain and return immediately.

            if ( returnValue == null )
            {
                return null;
            }
        }

        return returnValue;
    }

    /**
     *  Invokes all processor objects registered with the XmlRpcServer this dispatcher is
     *  working for.
     *
     * @param invocation The XmlRpcInvocation object representing the current invocation.
     * @param exception The exception that occurred during the invocation.
     *
     */
    private void processException(
        XmlRpcInvocation invocation,
        Throwable exception )
    {
        XmlRpcInvocationInterceptor p;

        for ( int i = 0; i < server.getInvocationInterceptors().size(); ++i )
        {
            p = server.getInvocationInterceptors().get( i );

            p.onException( invocation, exception );
        }
    }
    
    /**
     *  Writes a return value to the XML-RPC writer.
     *
     *  @param value The value to be encoded into the writer.
     *  @throws IOException serialization error.
     */
    private void writeValue( Object value ) throws IOException
    {
        server.getSerializer().writeEnvelopeHeader( value, writer );

        if ( value != null )
        {
            server.getSerializer().serialize( value , writer );
        }

        server.getSerializer().writeEnvelopeFooter( value, writer );
    }
    
    /**
     *  Creates an XML-RPC fault struct and puts it into the writer buffer.
     *
     *  @param code The fault code.
     *  @param message The fault string.
     */

    private void writeError( int code, String message )
    {
        try
        {
            log.warn( message );
            this.server.getSerializer().writeError( code, message, writer );
        }
        catch ( IOException ignore )
        {
            // If an exception occurs at this point there is no way to recover.
            // We are already trying to send a fault to the client. We swallow
            // the exception and trace it to the console.

            log.error(
                XmlRpcMessageBundle.getString( "XmlRpcDispatcher.ErrorSendingFault" ),
                ignore );
        }
    }
}
