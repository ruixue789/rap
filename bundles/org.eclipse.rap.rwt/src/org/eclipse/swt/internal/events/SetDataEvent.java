/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.*;


public final class SetDataEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int SET_DATA = SWT.SetData;

  private static final Class LISTENER = SetDataListener.class;

  public Widget item;
  public int index;

  public SetDataEvent( Widget source, Widget item, int index ) {
    super( source, SET_DATA );
    this.item = item;
    this.index = index;
  }

  public SetDataEvent( Event event ) {
    super( event );
    this.item = event.item;
    this.index = event.index;
  }

  protected void dispatchToObserver( Object listener ) {
    switch( getID() ) {
      case SET_DATA:
        ( ( SetDataListener )listener ).update( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  protected boolean allowProcessing() {
    return true;
  }

  public static void addListener( Adaptable adaptable, SetDataListener listener ) {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( Adaptable adaptable, SetDataListener listener ) {
    removeListener( adaptable, LISTENER, listener );
  }

  public static boolean hasListener( Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }

  public static Object[] getListeners( Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
