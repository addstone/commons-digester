/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/plugins/LogUtils.java,v 1.2 2004/01/10 17:41:26 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2004/01/10 17:41:26 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
package org.apache.commons.digester.plugins;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;

/**
 * Simple utility class to assist in logging.
 * <p>
 * The Digester module has an interesting approach to logging:
 * all logging should be done via the Log object stored on the
 * digester instance that the object *doing* the logging is associated
 * with.
 * <p>
 * This is done because apparently some "container"-type applications
 * such as Avalon and Tomcat need to be able to configure different logging
 * for different <i>instances</i> of the Digester class which have been
 * loaded from the same ClassLoader [info from Craig McClanahan]. 
 * Not only the logging of the Digester instance should be affected; all 
 * objects associated with that Digester instance should obey the 
 * reconfiguration of their owning Digester instance's logging. The current 
 * solution is to force all objects to output logging info via a single 
 * Log object stored on the Digester instance they are associated with.
 * <p>
 * Of course this causes problems if logging is attempted before an
 * object <i>has</i> a valid reference to its owning Digester. The 
 * getLogging method provided here resolves this issue by returning a
 * Log object which silently discards all logging output in this
 * situation.
 * <p>
 * And it also implies that logging filtering can no longer be applied
 * to subcomponents of the Digester, because all logging is done via
 * a single Log object (a single Category). C'est la vie...
 * 
 * @author Simon Kitching
 */

public class LogUtils {
    
  /**
   * Get the Log object associated with the specified Digester instance,
   * or a "no-op" logging object if the digester reference is null.
   * <p>
   * You should use this method instead of digester.getLogger() in
   * any situation where the digester might be null.
   */
  public static Log getLogger(Digester digester) {
    if (digester == null) {
        return new org.apache.commons.logging.impl.NoOpLog();
    }
    
    return digester.getLogger();
  }
}