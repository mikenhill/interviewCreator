package uk.co.liss.underwriting.gryphon;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Utility class with helper methods relating to http requests
 *
 * @author peterb
 */
public class HttpHelper {

	private static final String OPERATION = " operation ";
	private static final String REPORTING_PROPERTY_NAME = "reporting";
	private static final Logger logger = Logger.getLogger(HttpHelper.class);

	/**
	 * Overrides the usual validations applying to https certificate validation Tests against a system property <b>WHICH MUST NOT EVER BE
	 * ENABLED SHIPPING TO A CLIENT</b>, this is for internal testing and dev purposes
	 *
	 */
	public static final void overrideSslCertificateValidation(HttpsURLConnection connection) {
		logger.debug("testing for ssl validation chain override");
		
	}

	public static final String sendHttpRequest(String url, String params, String body, String encoding, HttpHelper.HttpOperation operation,
			String contentType, String acceptType) {
		logger.debug("sending get to url " + url);
		String response = null;
		try {
			response = requestToUrl(url, params, body, encoding, operation, contentType, acceptType);
		} catch (Exception e) {
			logger.error("error connecting to url " + url, e);
		}
		return response;
	}

	public static final String sendHttpRequest(String url, String params, String body, String encoding, HttpHelper.HttpOperation operation,
			String contentType, String acceptType, Map<String, Object> httpHeaders) {
		logger.debug("sending get to url " + url);
		String response = null;
		try {
			response = requestToUrl(url, params, body, encoding, operation, contentType, acceptType, httpHeaders);
		} catch (Exception e) {
			logger.error("error connecting to url " + url, e);
		}
		return response;
	}

	@Deprecated
	/**
	 * @Deprecated required as used in ulissia rules,phase out 90 days after release of this code
	 * @param url
	 * @param parameters
	 * @param encoding
	 * @param isPosting
	 * @return
	 */
	public static final String sendHttpRequest(String url, String parameters, String encoding, boolean isPosting) {
		return sendHttpRequest(url, parameters, "", encoding, isPosting ? HttpOperation.POST : HttpOperation.GET, "", "");
	}

	/**
	 *
	 * Takes a system property category name that holds endpoint properties, an operation name (the method to run) the payload which
	 * represents the main body of the soap request, this will have parameter tokens and a parameter map of names and values. this routine
	 * will then map the parameter values into the payload and attempt to send to the service endpoint.
	 *
	 *
	 * <mag:GetAccessToken xmlns:mag=\"MagnumServices\"> <mag:sessionKey>#SESSION_KEY#</mag:sessionKey></mag:GetAccessToken>
	 *
	 * parameter map : #SESSION_KEY# jksdfjldskkf
	 *
	 * result sent to webservice in soap envelope: <mag:GetAccessToken xmlns:mag=\"MagnumServices\">
	 * <mag:sessionKey>jksdfjldskkf</mag:sessionKey></mag:GetAccessToken>
	 *
	 * @param servicePropertyName
	 *            (the name of the system property category containing the endpoint properties)
	 * @param operation
	 *            (the soap operation name in the wsdl e.g. GetWorkflowItemStatus)
	 * @param payload
	 *            (the xml payload that will go in the soap envelope)
	 * @param parameterMap
	 *            (a map of any parameters to use in the payload)
	 * @return
	 */

//	public static final String sendSoapRequest(String servicePropertyName, String operation, String payload,
//			Map<String, String> parameterMap) {
//		logger.debug("sending soap request to " + servicePropertyName + OPERATION + operation);
//		mapParametersIntoPayload(payload, parameterMap);
//		String response = null;
//		try {
//			response = callWebService(servicePropertyName, operation);
//		} catch (Exception e) {
//			logger.error("error connecting to service " + servicePropertyName + OPERATION + operation, e);
//		}
//		return response;
//	}

//	public static String callWebService(String transformedXML, String integrationCategory) throws Exception {
//		// read the service and endpoint properties from system properties
//		String svcQnameNamespaceURI = ResourceHelper.lookupSystemProperty(integrationCategory, "svcQname.namespaceURI");
//		String svcQnameLocalPart = ResourceHelper.lookupSystemProperty(integrationCategory, "svcQname.localPart");
//
//		String portQnameNamespaceURI = ResourceHelper.lookupSystemProperty(integrationCategory, "portQname.namespaceURI");
//		String portQnameLocalPart = ResourceHelper.lookupSystemProperty(integrationCategory, "portQname.localPart");
//		String serviceEndPointAdderss = ResourceHelper.lookupSystemProperty(integrationCategory, "service.endPointAddress");
//		String dispatchSoapActionUri = ResourceHelper.lookupSystemProperty(integrationCategory, "dispatch.SoapActionUri");
//
//		return callWebService(transformedXML, svcQnameNamespaceURI, svcQnameLocalPart, portQnameNamespaceURI, portQnameLocalPart,
//				serviceEndPointAdderss, dispatchSoapActionUri);
//	}

	public static String callWebService(String transformedXML, String svcQnameNamespaceURI, String svcQnameLocalPart,
			String portQnameNamespaceURI, String portQnameLocalPart, String serviceEndPointAddress, String dispatchSoapActionUri)
			throws TransformerException, UnsupportedEncodingException {
		return callWebService(transformedXML, svcQnameNamespaceURI, svcQnameLocalPart, portQnameNamespaceURI, portQnameLocalPart,
				serviceEndPointAddress, dispatchSoapActionUri, SOAPBinding.SOAP11HTTP_BINDING);
	}

	public static String callWebService(String transformedXML, String svcQnameNamespaceURI, String svcQnameLocalPart,
			String portQnameNamespaceURI, String portQnameLocalPart, String serviceEndPointAddress, String dispatchSoapActionUri,
			Map<String, Object> httpHeaders) throws TransformerException, UnsupportedEncodingException {
		return callWebService(transformedXML, svcQnameNamespaceURI, svcQnameLocalPart, portQnameNamespaceURI, portQnameLocalPart,
				serviceEndPointAddress, dispatchSoapActionUri, SOAPBinding.SOAP11HTTP_BINDING, httpHeaders);
	}

	public static final String sendSoapRequest(String payload, String svcQnameNamespaceURI, String svcQnameLocalPart,
			String portQnameNamespaceURI, String portQnameLocalPart, String serviceEndPointAdderss, String dispatchSoapActionUri,
			Map<String, String> parameterMap) {
		logger.debug("sending soap request to " + serviceEndPointAdderss + OPERATION + svcQnameLocalPart);
		String mappedPayload = payload;
		mappedPayload = mapParametersIntoPayload(mappedPayload, parameterMap);
		String response = null;
		try {
			response = callWebService(mappedPayload, svcQnameNamespaceURI, svcQnameLocalPart, portQnameNamespaceURI, portQnameLocalPart,
					serviceEndPointAdderss, dispatchSoapActionUri);
		} catch (Exception e) {
			logger.error("error connecting to service " + serviceEndPointAdderss + OPERATION + svcQnameLocalPart, e);
		}
		return response;
	}

	public static final String sendSoapRequest(String payload, String svcQnameNamespaceURI, String svcQnameLocalPart,
			String portQnameNamespaceURI, String portQnameLocalPart, String serviceEndPointAdderss, String dispatchSoapActionUri,
			Map<String, String> parameterMap, Map<String, Object> httpHeaders) {
		logger.debug("sending soap request to " + serviceEndPointAdderss + OPERATION + svcQnameLocalPart);
		String mappedPayload = payload;
		mappedPayload = mapParametersIntoPayload(mappedPayload, parameterMap);
		String response = null;
		try {
			response = callWebService(mappedPayload, svcQnameNamespaceURI, svcQnameLocalPart, portQnameNamespaceURI, portQnameLocalPart,
					serviceEndPointAdderss, dispatchSoapActionUri, httpHeaders);
		} catch (Exception e) {
			logger.error("error connecting to service " + serviceEndPointAdderss + OPERATION + svcQnameLocalPart, e);
		}
		return response;
	}

	private static String mapParametersIntoPayload(String payload, Map<String, String> parameterMap) {
		String mappedPayload = payload;
		logger.debug("mapping parameters into payload " + mappedPayload);
		for (Entry<String, String> parameter : parameterMap.entrySet()) {
			mappedPayload = StringUtils.replace(mappedPayload, parameter.getKey(), parameter.getValue());
		}
		logger.debug("processed request payload is " + mappedPayload);
		return mappedPayload;
	}

	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		if ((request != null) && (request.getCookies() != null)) {
			for (Cookie c : request.getCookies()) {
				if (StringUtils.equalsIgnoreCase(c.getName(), cookieName)) {
					return c;
				}
			}
		}
		return null;
	}

	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie c = getCookie(request, cookieName);
		return c == null ? null : c.getValue();
	}

	public static void eraseCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
		if (((request != null) & (response != null)) && (request.getCookies() != null)) {
			for (Cookie c : request.getCookies()) {
				if (StringUtils.equalsIgnoreCase(c.getName(), cookieName)) {
					c.setValue(null);
					c.setMaxAge(0);
					response.addCookie(c);
				}
			}
		}
	}

	public static String callWebService(String transformedXML, String svcQnameNamespaceURI, String svcQnameLocalPart,
			String portQnameNamespaceURI, String portQnameLocalPart, String serviceEndPointAdderss, String dispatchSoapActionUri,
			String binding) throws TransformerException, UnsupportedEncodingException {
		return callWebService(transformedXML, svcQnameNamespaceURI, svcQnameLocalPart, portQnameNamespaceURI, portQnameLocalPart,
				serviceEndPointAdderss, dispatchSoapActionUri, binding, null);
	}

	public static String callWebService(String transformedXML, String svcQnameNamespaceURI, String svcQnameLocalPart,
			String portQnameNamespaceURI, String portQnameLocalPart, String serviceEndPointAdderss, String dispatchSoapActionUri,
			String binding, Map<String, Object> httpHeaders) throws TransformerException, UnsupportedEncodingException {
		// create the service and port data for the call
		QName svcQname = new QName(svcQnameNamespaceURI, svcQnameLocalPart);
		QName portQName = new QName(portQnameNamespaceURI, portQnameLocalPart);
		Service svc = Service.create(svcQname);
		svc.addPort(portQName, binding, serviceEndPointAdderss);

		// Create the dynamic invocation object from this service.
		Dispatch<Source> dispatch = svc.createDispatch(portQName, Source.class, Service.Mode.PAYLOAD);

		if(httpHeaders!=null){
			processHeaders(httpHeaders);
			dispatch.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, httpHeaders);
		}

		// Build the message.
		String content = transformedXML;

		ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes("UTF-8"));
		Source input = new StreamSource(bais);

		dispatch.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
		dispatch.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, dispatchSoapActionUri);

		// Invoke the operation.
		logger.debug("requestContent: " + content);
		Source output = dispatch.invoke(input);

		// Process the response.
		StreamResult result = new StreamResult(new ByteArrayOutputStream());
		Transformer trans = TransformerFactory.newInstance().newTransformer();
		trans.transform(output, result);
		ByteArrayOutputStream baos = (ByteArrayOutputStream) result.getOutputStream();

		// Write out the response content.
		String responseContent = new String(baos.toByteArray());
		logger.debug("responseContent: " + responseContent);
		return responseContent;

	}

	public static enum HttpOperation {

		UNSET(""), GET("GET"), POST("POST"), PUT("PUT");

		public final String lissCode;

		private HttpOperation(String lissCode) {
			this.lissCode = lissCode;
		}

		public static HttpOperation lookupCode(String lissCode) {
			for (HttpOperation op : values()) {
				if (op.lissCode.equals(lissCode)) {
					return op;
				}
			}
			throw new IllegalArgumentException("missing status code " + lissCode);
		}
	}

	// FIXME here to help with jexl statement enum access
	public HttpOperation getHttpOperation() {
		return HttpOperation.UNSET;
	}

	public static final String sendSoapRequest(String payload, String svcQnameNamespaceURI, String svcQnameLocalPart,
			String portQnameNamespaceURI, String portQnameLocalPart, String serviceEndPointAdderss, String dispatchSoapActionUri,
			Map<String, String> parameterMap, String soapBinding) {
		logger.debug("sending soap request to " + serviceEndPointAdderss + OPERATION + svcQnameLocalPart);
		String mappedPayload = payload;
		mappedPayload = mapParametersIntoPayload(mappedPayload, parameterMap);
		String response = null;
		try {
			response = callWebService(mappedPayload, svcQnameNamespaceURI, svcQnameLocalPart, portQnameNamespaceURI, portQnameLocalPart,
					serviceEndPointAdderss, dispatchSoapActionUri, soapBinding);
		} catch (Exception e) {
			logger.error("error connecting to service " + serviceEndPointAdderss + OPERATION + svcQnameLocalPart, e);
			response = null;
		}
		return response;
	}

	public static String requestToUrl(String url, String params, String requestBody, String encoding, HttpHelper.HttpOperation operation,
			String contentType, String acceptType) throws Exception {
		return requestToUrl(url, params, requestBody, encoding, operation,contentType, acceptType, null);
	}

	public static String requestToUrl(String url, String params, String requestBody, String encoding, HttpOperation operation,
			String contentType, String acceptType, Map<String, Object> httpHeaders) throws Exception {
		logger.debug("generating encoded url using encoding" + encoding);
		String encodedParams = URLEncoder.encode(params, encoding);
		logger.debug("generated encoded url is :" + url + " request body is " + requestBody);

		URL requestUrl;
		URLConnection conn;

		requestUrl = new URL(url + encodedParams);
		conn = requestUrl.openConnection();

		if (contentType != null) {
			conn.setRequestProperty("Content-Type", contentType);
		}
		if (acceptType != null) {
			conn.setRequestProperty("Accept", acceptType);
		}
		
		if(httpHeaders!=null){
			for (Map.Entry<String, Object> entry : httpHeaders.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
			}
		}

		if (conn instanceof HttpsURLConnection) {
			HttpHelper.overrideSslCertificateValidation((HttpsURLConnection) conn);
			((HttpsURLConnection) conn).setRequestMethod(operation.name());
		} else if (conn instanceof HttpURLConnection) {
			((HttpURLConnection) conn).setRequestMethod(operation.name());
		}

		if ((operation == HttpOperation.POST) || (operation == HttpOperation.PUT)) {
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(requestBody);
			logger.debug("url posted: " + requestBody + " to " + url + encodedParams);
			wr.flush();
			wr.close();
		}

		// Get the response
		String line, response = "";
		try {
			// TODO can we do better than try, catch, there is a bit of
			// duplication ?
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				response += line;
			}
			rd.close();
		} catch (IOException e) {
			// try to get error data from the response if its a httpconnection
			InputStream errorstream;
			if (conn instanceof HttpsURLConnection) {
				errorstream = ((HttpsURLConnection) conn).getErrorStream();
			} else if (conn instanceof HttpURLConnection) {
				errorstream = ((HttpURLConnection) conn).getErrorStream();
			} else {
				throw e;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(errorstream));
			while ((line = br.readLine()) != null) {
				response += line;
			}
			br.close();
			logger.error("error response received : " + response);
		}

		logger.debug("URL response: " + response);
		return response;
	}

	/**
	 * Accept a map of header values and convert any value entries that are not collections to collections
	 * 
	 * @param httpHeaders
	 */
	private static void processHeaders(Map<String, Object> httpHeaders) {
		for (Entry<String, Object> entry : httpHeaders.entrySet()) {
			if (entry.getValue() != null && !(entry.getValue() instanceof Collection)) {
				httpHeaders.put(entry.getKey(), Collections.singletonList(entry.getValue()));
			}
		}

	}

}

