/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.internal.junit.runtime;

import org.eclipse.core.runtime.Platform;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rap.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.testing.ITestHarness;
import org.eclipse.ui.testing.TestableObject;


/**
 * This class controls all aspects of the application's execution and is
 * contributed through the plugin.xml.
 */
@SuppressWarnings("restriction")
public class Application implements IEntryPoint, ITestHarness {

  private TestableObject fTestableObject;

  public int createUI() {
    UICallBack.activate( Application.class.getName() );

    fTestableObject = PlatformUI.getTestableObject();
    fTestableObject.setTestHarness( this );
    return createAndRunWorkbench();
  }

  private int createAndRunWorkbench() {
    int result;
    String entryPointName = getEntryPointName();
    if( entryPointName != null ) {
      IEntryPoint entryPoint = getEntryPoint( entryPointName );
      result = entryPoint.createUI();
    } else {
      result = createAndRunEmptyWorkbench();
    }
    return result;
  }

  private int createAndRunEmptyWorkbench() {
    Display display = PlatformUI.createDisplay();
    WorkbenchAdvisor workbenchAdvisor = new WorkbenchAdvisor(){
      @Override
      public String getInitialWindowPerspectiveId() {
        return "org.eclipse.rap.junit.runtime.emptyPerspective";
      }
    };
    return PlatformUI.createAndRunWorkbench( display, workbenchAdvisor );
  }

  private String getEntryPointName() {
    String parameter = RWT.getRequest().getParameter( "testentrypoint" );
    String result = null;
    if( !"rapjunit".equals( parameter ) && !"".equals( parameter ) ) {
      result = parameter;
    }
    return result;
  }

  private IEntryPoint getEntryPoint( String entryPointName ) {
    EntryPointManager entryPointManager = RWTFactory.getEntryPointManager();
    IEntryPointFactory factory = entryPointManager.getFactoryByName( entryPointName );
    if( factory == null ) {
      throw new IllegalArgumentException( entryPointName );
    }
    return factory.create();
  }

  public void runTests() {
    fTestableObject.testingStarting();
    fTestableObject.runTest( new Runnable() {
      public void run() {
        RemotePluginTestRunner.main( Platform.getCommandLineArgs() );
      }
    } );
    fTestableObject.testingFinished();
  }
}
