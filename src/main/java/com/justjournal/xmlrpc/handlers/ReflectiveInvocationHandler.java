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

package com.justjournal.xmlrpc.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import com.justjournal.xmlrpc.XmlRpcException;
import com.justjournal.xmlrpc.XmlRpcInvocationHandler;
import com.justjournal.xmlrpc.XmlRpcMessageBundle;
import lombok.Setter;

/**
 *  Note that methods that are to be called over XML-RPC need to be public, or the
 *  client will receive a fault message.
 *
 *  @author  Greger Olsson
 */

public class ReflectiveInvocationHandler implements XmlRpcInvocationHandler
{
    /**
     *  Constructs the handler and sets the current object as the target. That
     *  is, use 'this' as the target to receive the invocations.
     */

    public ReflectiveInvocationHandler()
    {
        target = this;
    }


    /**
     *  Constructs the handler and sets the supplied object as the invocation target.
     *  This is used when wrapping an object in a reflective handler. Wrapping is
     *  useful if you want to publish an object that is not a ReflectiveInvocationHander.
     *
     *  @param target The object to wrap up.
     */

    public ReflectiveInvocationHandler( Object target )
    {
        this.target = target;
    }


    /**
     *  Constructs the handler and sets the supplied objects as the invocation target.
     *  This is used when wrapping an object in a reflective handler. The user may also
     *  specify a list of methods that should be available in the target.
     *
     *  @param target The object to wrap up.
     *
     *  @param entryPoints A list of method names that should be available for invocation.
     */

    public ReflectiveInvocationHandler( Object target, String[] entryPoints )
    {
        this.target = target;
        this.entryPoints = entryPoints;
    }


    /**
     *  Called by the XmlRpcServer when a method is to be invoked. This implementation
     *  tries to locate the supplied method in the target object using Java Reflection.
     *  If the invocation handler keeps a list of published methods, this is first checked
     *  to see if the call should be carried through.
     *
     *  @param methodName The name of the method to call.
     *
     *  @param arguments A vector containting all arguments required by the method.
     */
    public Object invoke(
        String methodName,
        List arguments )
        throws Throwable
    {
        // Is this handler restricted? If so, is methodName published? Throws an exception if not.

        if ( entryPoints != null )
            checkEntryPoint( methodName );

        // Determine types of inbound parameters

        Class[] argClasses = null;
        Object[] argValues =  null;

        if( arguments != null )
        {
            argValues = arguments.toArray();
            argClasses = new Class[ argValues.length ];

            for ( int i = 0; i < argValues.length; ++i )
            {
                argClasses[ i ] = argValues[ i ].getClass();
            }
        }

        return execute( methodName, argClasses, argValues );
    }


    /**
     * Locates and invokes a method with the given signature using reflection.
     * <p>
     * This method uses an internal algorithm to find a matching method where the parameters
     * only need to be assignment compatible with the arguments. This allows for more
     * flexible method matching compared to the strict matching of java.lang.Class.getMethod().
     * <p>
     * <b>Note:</b> For optimal performance, overloaded methods in the handler class should
     * be listed with increasing parameter type specialization.
     *
     * @param methodName The name of the method to be invoked
     * @param argClasses An array of Class objects representing the types of the method arguments
     * @param argValues An array of Object instances containing the actual argument values
     * @return The result of the method invocation
     * @throws Throwable If an error occurs during method lookup or invocation
     */
    protected Object execute(
        String methodName,
        Class[] argClasses,
        Object[] argValues ) throws Throwable
    {
        Method[] methods = target.getClass().getMethods();

        outer: for ( int i = 0; i < methods.length; ++i )
        {
            Method method = methods[ i ];

            if ( method.getName().equals( methodName ) )
            {
                Class[] parameterTypes = method.getParameterTypes();

                if ( parameterTypes.length == argClasses.length )
                {
                    for ( int j = 0; j < parameterTypes.length; ++j )
                    {
                        Class type = parameterTypes[ j ];

                        if ( type.isPrimitive() )
                        {
                            if ( type.getName().equals( "D" ) && argClasses[ j ] != Double.class )
                                continue outer;

                            if ( type.getName().equals( "I" ) && argClasses[ j ] != Integer.class )
                                continue outer;

                            if ( type.getName().equals( "Z" ) && argClasses[ j ] != Boolean.class )
                                continue outer;
                        }
                        else
                        {
                            if ( !parameterTypes[ j ].isAssignableFrom( argClasses[ j ] ) )
                            {
                                continue outer;
                            }
                        }
                    }

                    try
                    {
                        return method.invoke( target, argValues );
                    }
                    catch ( InvocationTargetException it_e )
                    {
                        throw it_e.getTargetException();
                    }
                }
            }
        }

        // Include all class names of the arguments in the error response
        // to make it easier to debug. The invocation handler tries to find
        // a method matching these classes, and one could not be found.
        
        StringBuffer error = new StringBuffer( 128 );
        error.append( XmlRpcMessageBundle.getString( "ReflectiveInvocationHandler.MethodDontExist" ) );

        for ( int i = 0; i < argClasses.length; ++i )
        {
            error.append( argClasses[ i ].getName() );
            error.append( ' ' );
        }
        
        throw new XmlRpcException( error.toString() );
    }


    /**
     *  Checks if the supplied methodName is one of the public entry points. If not,
     *  this method throws an exception that will eventually reach the caller.
     *
     *  @param methodName The name of the method to look for.
     *
     *  @throws XmlRpcException if the method is not one of the published methods.
     */

    private void checkEntryPoint( String methodName ) throws XmlRpcException
    {
        for ( int i = 0; i < entryPoints.length; ++i )
        {
            if ( entryPoints[ i ].equals( methodName ) )
                return;
        }

        throw new XmlRpcException(
            XmlRpcMessageBundle.getString( "ReflectiveInvocationHandler.MethodNotPublished" ) );
    }


    /** The object to reflect upon when locating methods. */
    protected Object target;

    /** An optional list of "public" entry points.
     * -- SETTER --
     *   Assigns a list of method names that are used when invoking methods on this handler.
     *   After calling this method, only methods listed in the supplied entryPoints list
     *   will be available for invocation. Note that for a given method "testMethod", all
     *   overloaded versions of that method will be available. That is, the invocation
     *   mechanism does not take into account parameter lists, just names.<p>
     *   A null entry point list means all public methods are available.
     *
     * @param entryPoints A list of method names that may be invoked on this handler, or
     *                     null if all methods should be available.
     **/
    @Setter
    private String[] entryPoints;
}