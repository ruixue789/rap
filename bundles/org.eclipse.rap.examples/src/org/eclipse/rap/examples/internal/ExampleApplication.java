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
package org.eclipse.rap.examples.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.resources.IResource;
import org.eclipse.rap.rwt.resources.IResourceManager.RegisterOptions;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;


public class ExampleApplication implements ApplicationConfiguration {

  public void configure( Application application ) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.PAGE_TITLE, "RAP Examples" );
    properties.put( WebClient.BODY_HTML, readTextFromResource( "resources/body.html", "UTF-8" ) );
    properties.put( WebClient.FAVICON, "icons/favicon.png" );
    application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
    application.addEntryPoint( "/examples", MainUi.class, properties );
    application.addStyleSheet( RWT.DEFAULT_THEME_ID, "theme/theme.css" );
    application.addResource( createResource( "icons/favicon.png" ) );
    application.addResource( createResource( "icons/loading.gif" ) );
    loadClientScriptingResources( application );
  }

  private void loadClientScriptingResources( Application application ) {
    IResource[] resources = getClientScriptingResources();
    for( IResource resource : resources ) {
      application.addResource( resource );
    }
  }

  // TODO [rst] Replace this hack with a proper resource loading mechanism (see bug 369957)
  private IResource[] getClientScriptingResources() {
    IResource[] resources = new IResource[ 0 ];
    Bundle clientScriptingBundle = findBundle( "org.eclipse.rap.clientscripting" );
    if( clientScriptingBundle != null ) {
      String className = "org.eclipse.rap.clientscripting.internal.resources.ClientScriptingResource";
      try {
        Class<?> resourceClass = clientScriptingBundle.loadClass( className );
        Field field = resourceClass.getField( "ALL_RESOURCES" );
        resources = ( IResource[] )field.get( null );
      } catch( Exception exception ) {
        throw new RuntimeException( exception );
      }
    }
    return resources;
  }

  private Bundle findBundle( String symbolicId ) {
    Bundle result = null;
    BundleContext bundleContext = FrameworkUtil.getBundle( getClass() ).getBundleContext();
    for( Bundle bundle : bundleContext.getBundles() ) {
      if( symbolicId.equals( bundle.getSymbolicName() ) ) {
        result = bundle;
      }
    }
    return result;
  }

  private static IResource createResource( final String resourceName ) {
    return new IResource() {

      public boolean isJSLibrary() {
        return false;
      }

      public boolean isExternal() {
        return false;
      }

      public RegisterOptions getOptions() {
        return RegisterOptions.NONE;
      }

      public String getLocation() {
        return resourceName;
      }

      public ClassLoader getLoader() {
        return ExampleApplication.class.getClassLoader();
      }

      public String getCharset() {
        return null;
      }
    };
  }

  private static String readTextFromResource( String resourceName, String charset ) {
    String result;
    try {
      ClassLoader classLoader = ExampleApplication.class.getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream( resourceName );
      if( inputStream == null ) {
        throw new RuntimeException( "Resource not found: " + resourceName );
      }
      try {
        BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
        StringBuilder stringBuilder = new StringBuilder();
        String line = reader.readLine();
        while( line != null ) {
          stringBuilder.append( line );
          stringBuilder.append( '\n' );
          line = reader.readLine();
        }
        result = stringBuilder.toString();
      } finally {
        inputStream.close();
      }
    } catch( IOException e ) {
      throw new RuntimeException( "Failed to read text from resource: " + resourceName );
    }
    return result;
  }
}
