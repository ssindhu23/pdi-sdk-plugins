/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.sdk.samples.carte;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicHeader;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.HttpClientManager;
import org.pentaho.di.core.util.HttpClientUtil;
import org.pentaho.di.www.GetTransImageServlet;

import java.io.FileOutputStream;

public class GetTransImageSample extends AbstractSample {
  public static void main( String[] args ) throws Exception {
    if ( args.length < 6 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
        + "Carte_login Carte_password job_name full_ouput_filepath" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster dummy_job d:\\1.png" );
      System.exit( 1 );
    }
    init( args[ 0 ], Integer.parseInt( args[ 1 ] ), args[ 2 ], args[ 3 ] );
    // building target url
    String realHostname = args[ 0 ];
    String port = args[ 1 ];
    String urlString = "http://" + realHostname + ":" + port + GetTransImageServlet.CONTEXT_PATH + "?name=" + args[ 4 ];
    urlString = Const.replace( urlString, " ", "%20" );

    //building auth token
    String plainAuth = args[ 2 ] + ":" + args[ 3 ];
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );

    sendGetImageRequest( urlString, auth, args[ 5 ] );
  }

  public static void sendGetImageRequest( String urlString, String authentication, String fileName ) throws Exception {
    HttpGet method = new HttpGet( urlString );
    HttpClientContext context = HttpClientUtil.createPreemptiveBasicAuthentication( host, port, user, password );
    //adding authorization token
    if ( authentication != null ) {
      method.addHeader( new BasicHeader( "Authorization", authentication ) );
    }

    //executing method
    HttpClient client = HttpClientManager.getInstance().createDefaultClient();
    HttpResponse httpResponse = context != null ? client.execute( method, context ) : client.execute( method );
    int code = httpResponse.getStatusLine().getStatusCode();
    byte[] response = HttpClientUtil.responseToByteArray( httpResponse );
    method.releaseConnection();
    if ( code >= HttpStatus.SC_BAD_REQUEST ) {
      System.out.println( "Error occurred during getting transformation image." );
    }
    System.out.println( "Image was stored to " + fileName );
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream( fileName );
      fos.write( response );
      fos.flush();
    } finally {
      fos.close();
    }
  }
}
