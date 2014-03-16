package se.altran.restkurs.webapi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

public class HttpHelper {

	private BasicHttpContext basicHttpContext;
	private HttpHost target;
	private DefaultHttpClient httpClient;
	
	
	public HttpHelper(String site, int port) {
		httpClient = new DefaultHttpClient();
		basicHttpContext = new BasicHttpContext();
		target = new HttpHost(site, port, "http");
	}

	/**
	 * Executes a HTTP method in the current context (site and port)
	 * @param method HTTP method
	 * @return HTTPResponse of the completed request
	 * @throws Exception
	 */
	public HttpResponse executeMethod(HttpRequest method) throws Exception {
		HttpResponse response = httpClient.execute(target, method, basicHttpContext);
		return response;
	}

	/**
	 * Returns the Response Entity as a String
	 * @param response Response Entity from the execution of a HTTPRequest 
	 * @return String data on UTF-8
	 * @throws Exception
	 */
	public static String responseData(HttpResponse response) throws Exception {
		HttpEntity entity = response.getEntity();
		String resource = new String(EntityUtils.toString(entity).getBytes("ISO-8859-1"), "UTF-8");
		
		EntityUtils.consume(entity);
		return resource;
	}

}
