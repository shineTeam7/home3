package com.home.shine.net.ssl;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class MTrustAllManager extends X509ExtendedTrustManager
{
	private static X509Certificate[] _arr=new X509Certificate[0];
	
	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates,String s,Socket socket) throws CertificateException
	{
	
	}
	
	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates,String s,Socket socket) throws CertificateException
	{
	
	}
	
	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates,String s,SSLEngine sslEngine) throws CertificateException
	{
	
	}
	
	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates,String s,SSLEngine sslEngine) throws CertificateException
	{
	
	}
	
	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates,String s) throws CertificateException
	{
	
	}
	
	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates,String s) throws CertificateException
	{
	
	}
	
	@Override
	public X509Certificate[] getAcceptedIssuers()
	{
		return _arr;
	}
}
