/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.internal.server;


import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;


public class DelegatingServletEngine implements IServletEngine {
  
  private final IServletEngine delegate;
  private boolean running;
  
  public DelegatingServletEngine( IServletEngine delegate ) {
    this.delegate = delegate;
  }
  
  public IServletEngine getDelegate() {
    return delegate;
  }
  
  public void start( Class<? extends IEntryPoint> entryPointClass ) throws Exception {
    checkNotNull( entryPointClass );
    checkNotRunning();
    delegate.start( entryPointClass );
    running = true;
  }

  public void stop() throws Exception {
    stop( 0 );
  }

  public void stop( int timeout ) throws Exception {
    if( running ) {
      delegate.stop( timeout );
      running = false;
    }
  }
  
  public int getPort() {
    return delegate.getPort();
  }

  public HttpSession[] getSessions() {
    if( !running ) {
      String msg = "Sessions can only be accessed while the servlet engine is running.";
      throw new IllegalStateException( msg );
    } 
    return delegate.getSessions();
  }

  private void checkNotNull( Class<? extends IEntryPoint> entryPointClass ) {
    if( entryPointClass == null ) {
      throw new NullPointerException( "entryPointClass" );
    }
  }

  private void checkNotRunning() {
    if( running ) {
      throw new IllegalStateException( "Servlet engine was already started: " + this );
    }
  }
}
