package uk.co.liss.underwriting.gryphon;

import java.util.HashMap;

import javax.xml.ws.soap.SOAPBinding;

public class InterviewCreatorFunctionInvocator {

	
	
public static void main (String args[] ) {
	InterviewCreatorFunctionInvocator cit = new InterviewCreatorFunctionInvocator();
	cit.createInterview();
}

private String createInterview() {

	String XML_TEMPLATE = "<createInterview xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://service.underwriting.webservice.liss.co.uk\">"
			+ "<createInterviewRequest>"
			+ "<auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\">"
			+ "<ghostUserId xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			+ "<pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode>"
			+ "<userId xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			+ "</auditData>"
			
			+ "<interviewScores xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">"
			+ 	"<initialValue xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			+ 	"<scoreCategory xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			+ 	"<scoreDescription xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			+ 	"<scoreGroupName xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">FUNCTION_NAME</scoreGroupName>"
			+ 	"<value xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">select dbo.FS_EL_GRYP_SOF_DATA_XML2(?)</value>"
			+ "</interviewScores>"
			
			+ "<interviewScores xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">"
			+ 	"<initialValue xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			+ 	"<scoreCategory xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			+ 	"<scoreDescription xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			+ 	"<scoreGroupName xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">FUNCTION_PARAMS</scoreGroupName>"
			+ 	"<value xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">16163</value>"
			+ "</interviewScores>"
			
+ "<interviewScores xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">"
+ 	"<initialValue xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
+ 	"<scoreCategory xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
+ 	"<scoreDescription xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
+ 	"<scoreGroupName xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">FUNCTION_DATABASE</scoreGroupName>"
+ 	"<value xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">jdbc/REPORTING</value>"
+ "</interviewScores>"
			
			+ "<lives xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">"
			+ 	"<clientId xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">1</clientId>"
			+ "</lives><reference xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			+ "<rulebookName xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">FUNCTION_INVOCATOR</rulebookName>"
			+ "<windToPageCode xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/>"
			
			+ "</createInterviewRequest>"
			+ "</createInterview>";
	
	

	String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
	String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
	String portLocalName = "UnderwritingServiceSoap11Binding";
	String endpointAddress = "https://vm-dev-sb2-al3.gghldevint.co.uk:8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
	String serviceLocalName = "createInterview";
	String soapAction = "http://service.underwriting.webservice.liss.co.uk/createInterview";

	String response = HttpHelper.sendSoapRequest(XML_TEMPLATE, serviceNamespace, serviceLocalName, portNamespace,
			portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
			SOAPBinding.SOAP11HTTP_BINDING);
	
	System.out.println(response);
	
	System.out.println("Char length = " + response.length() );
	byte[] theBytes = response.getBytes();
	System.out.println("Byte length = " + theBytes.length );
	for (int i = 0; i < theBytes.length; i++) {
		System.out.println("Byte " + i + " = " + theBytes[i]);
	}
	for (int i = 0; i < response.length(); i++) {
		System.out.println("Char " + i + " = " + response.charAt(i));
	}
	
	return response;
}
	
}
