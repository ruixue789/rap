/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.internal.service.ContextProvider;


public class ClientSelector {

  // TODO: [if] This constant is made public only to fake different clients in tests
  public static final String SELECTED_CLIENT = ClientSelector.class.getName() + ".selected";
  private final List<ClientProvider> clients = new ArrayList<ClientProvider>();
  private boolean activated = false;

  public void addClientProvider( ClientProvider clientProvider ) {
    checkNotActivated();
    clients.add( clientProvider );
  }

  public void selectClient( HttpServletRequest request ) {
    ClientProvider provider = findClientProvider( request );
    bufferClient( provider.getClient() );
  }

  public Client getSelectedClient() {
    return getBufferedClient();
  }

  public void activate() {
    checkNotActivated();
    clients.add( new WebClientProvider() );
    activated = true;
  }

  private void checkNotActivated() {
    if( activated ) {
      throw new IllegalStateException( "ClientSelector already activated" );
    }
  }

  private ClientProvider findClientProvider( HttpServletRequest request ) {
    for( ClientProvider provider : clients ) {
      if( provider.accept( request ) ) {
        return provider;
      }
    }
    throw new IllegalStateException( "No client provider found for request" );
  }

  private void bufferClient( Client client ) {
    ContextProvider.getSessionStore().setAttribute( SELECTED_CLIENT, client );
  }

  private Client getBufferedClient() {
    return ( Client )ContextProvider.getSessionStore().getAttribute( SELECTED_CLIENT );
  }

}
