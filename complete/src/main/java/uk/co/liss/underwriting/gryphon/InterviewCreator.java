package uk.co.liss.underwriting.gryphon;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class InterviewCreator {

	
	int uniqueId = 1;
	String createInterviewInterviewId = null;
	String createInterviewInterviewToken = null;
	String submitPageTemplate = "<submitPage xmlns:xsi=\"xsi\" xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><submitPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">m3CVzfN6LuzIzgb7R55b</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">8217</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber><pageNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">26</pageNumber><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>#ANSWER#</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">#QUESTION_CODE#</code></questions></submitPageRequest></submitPage>";
	private String server_dns;
	private String externalToken;
	private String SG_FACADE_ENDPOINT;
	private String rulebookNb;
	private String rulebookUw;
	
	
	public InterviewCreator (String server_dns, String externalToken, String facadeEndpoint, String rulebookNb, String rulebookUw) {
		this.server_dns = server_dns;
		this.externalToken = externalToken;
		this.SG_FACADE_ENDPOINT = facadeEndpoint;
		this.rulebookNb = rulebookNb;
		this.rulebookUw = rulebookUw;
	}
	
	private void setUp() {
		
		uniqueId = 1;
		createInterviewInterviewId = null;
		createInterviewInterviewToken = null;
	}
	
//	public static String readFileForTest(String type, String filename) throws IOException {
//		String fullPath = getFileNameForTest(type, filename);
//		return new String(Files.readAllBytes(Paths.get(fullPath)));
//	}
//	
//	public static String getFileNameForTest(String type, String fileName) {
//		
//		//Type will be of the form dir\dir
//		System.out.println(type);
//		String directory = type.substring(0, type.indexOf("\\"));
//		type = type.substring(type.indexOf("\\")+1);
//		
//		String fullPath = Paths.get("","src\test","resources\\"+directory,type,fileName + "."+type).toAbsolutePath().toString();
//		return fullPath;
//	}
	
	public String createInterviewUnderwriting () {
		try {
			
			setUp();
			Map<String, String> mAnswers = new HashMap<String, String>();
			
			// create an interview
			String response = createInterview();
			System.out.println("createInterview - " + response);

			// submitPage_SingleOrDual
			mAnswers.put("QD_APP_NUM", "single");
			response = getNextPage();
			String submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
			response = submitPageNew(response, submitPageXml);
			System.out.println("Single or dual - " + response);
			
					
			// submitPage_ClientDetails
			mAnswers = new HashMap<String, String>();
			mAnswers.put("QD_POLDATA_PA_FNAME", "First name UW");
			mAnswers.put("QD_POLDATA_PA_LNAME", "Last name UW");
			mAnswers.put("QD_POLDATA_PA_GENDER", "male");
			mAnswers.put("QD_POLDATA_PA_DOB", "12/12/1998");
			mAnswers.put("QD_POLDATA_PA_SMOKER", "noneinthelast5");
			mAnswers.put("QD_POLDATA_PA_POSTCODE", "WD17 1DA");
			mAnswers.put("QD_POLDATA_PA_OCC", "businessanalyst");
			mAnswers.put("QD_POLDATA_PA_SALARY", "23456");
			mAnswers.put("QD_POLDATA_PA_WAIST", "changetoinches");
			mAnswers.put("QD_POLDATA_PA_WEIGHT", "changetostlbs");
			response = getNextPage();
			submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
			
			response = submitPageNew(response, submitPageXml);
			System.out.println("Client - " + response);
			
			

			// submitPage_createQuote
			mAnswers = new HashMap<String, String>();
			mAnswers.put("QD_PRODSEL_QUOTEBY", "quotebysum");
			mAnswers.put("QD_PRODSEL_PA_TL_TOC_INSTANCE1", "level");
			mAnswers.put("QD_PRODSEL_PA_TL_TERM_INSTANCE1", "12");
			mAnswers.put("QD_PRODSEL_PA_TL_SUM_INSTANCE1", "100000");
			mAnswers.put("QD_PRODSEL_PA_TL_ESC_INSTANCE1", "yes");
			mAnswers.put("QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_CI_ESC_INSTANCE1", "yes");
			mAnswers.put("QD_PRODSEL_PA_REMOVE_CI_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_REMOVE_IP_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_REMOVE_FRACTURE", "no");
			mAnswers.put("QD_PRODSEL_REMOVE_CHILDCI", "no");
			mAnswers.put("QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1", "yes");
			mAnswers.put("QD_PRODSEL_PA_SELECT_CI_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_SELECT_IP_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_SELECT_FRACTURE", "no");
			mAnswers.put("QD_PRODSEL_SELECT_CHILDCI", "no");
			mAnswers.put("QD_PRODSEL_COMM_INDEM_PCT_1", "100");
			mAnswers.put("QD_PRODSEL_COMM_SACRIFICE_PCT", "0");
			mAnswers.put("H_QD_PRODSEL_FLAG_RECALC", "N/A");
			mAnswers.put("QD_SAQ", "N/A");
			mAnswers.put("QD_QUOTE_VALIDITY", "22/12/2018");
					
			response = getNextPage();
			submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
			
			response = submitPageNew(response, submitPageXml);
			System.out.println("Create q - " + response);
			
			
			// submitPage_createQuote
			mAnswers = new HashMap<String, String>();
			mAnswers.put("QD_PRODSEL_QUOTEBY", "quotebysum");
			mAnswers.put("QD_PRODSEL_PA_TL_TOC_INSTANCE1", "level");
			mAnswers.put("QD_PRODSEL_PA_TL_TERM_INSTANCE1", "12");
			mAnswers.put("QD_PRODSEL_PA_TL_SUM_INSTANCE1", "100000");
			mAnswers.put("QD_PRODSEL_PA_TL_ESC_INSTANCE1", "yes");
			mAnswers.put("QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_CI_ESC_INSTANCE1", "yes");
			mAnswers.put("QD_PRODSEL_PA_REMOVE_CI_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_REMOVE_IP_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_REMOVE_FRACTURE", "no");
			mAnswers.put("QD_PRODSEL_REMOVE_CHILDCI", "no");
			mAnswers.put("QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1", "yes");
			mAnswers.put("QD_PRODSEL_PA_SELECT_CI_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_SELECT_IP_INSTANCE1", "no");
			mAnswers.put("QD_PRODSEL_PA_SELECT_FRACTURE", "no");
			mAnswers.put("QD_PRODSEL_SELECT_CHILDCI", "no");
			mAnswers.put("QD_PRODSEL_COMM_INDEM_PCT_1", "100");
			mAnswers.put("QD_PRODSEL_COMM_SACRIFICE_PCT", "0");
			mAnswers.put("H_QD_PRODSEL_FLAG_RECALC", "N/A");
			mAnswers.put("QD_SAQ", "N/A");
			mAnswers.put("QD_QUOTE_VALIDITY", "22/12/2018");
					
			response = getNextPage();
			submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
			
			response = submitPageNew(response, submitPageXml);	
			System.out.println("Quote - " + response);
			
			

			
			//Submit Apply page
			mAnswers = new HashMap<String, String>();
			mAnswers.put("APP_PA_TITLE", "mr");
			mAnswers.put("APP_PA_EMAIL", "dd@ss.com");
			mAnswers.put("APP_PA_CONFIRM_EMAIL", "dd@ss.com");
			mAnswers.put("APP_PA_PHONE", "09888776655");
			mAnswers.put("APP_PA_FINDADD", "123");
			mAnswers.put("APP_PA_ADDRESS1", "26 DummyData Street");
			mAnswers.put("APP_PA_ADDRESS2", "Townsville");
			mAnswers.put("APP_PA_TOWN", "Castleford");
			mAnswers.put("APP_PA_COUNTY", "West Yorkshire");
			mAnswers.put("APP_PA_PCODE", "WF10 4AU");
			mAnswers.put("APP_PA_EXISTING_LIFE_COVER", "no");
			mAnswers.put("APP_PA_EXISTING_CI_COVER", "no");
			mAnswers.put("APP_MORTGAGE", "no");
			response = getNextPage();		
			submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
			response = submitPageNew(response, submitPageXml);
			System.out.println("Apply - " + response);
			
			mAnswers = new HashMap<String, String>();
			response = getNextPage();
			submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
			response = submitPageNew(response, submitPageXml);
			System.out.println("UW_LINKED - " + response);
			
			//=======================================================
			//Decision
			mAnswers = new HashMap<String, String>();
			response = getNextPage();		
			submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
			response = submitPageNew(response, submitPageXml);
			System.out.println("Decision - " + response);
//			
//			//Must update cache here
//			String XML_TEMPLATE = "<pol:setApplicationDetailsForAdviser xmlns:pol=\"http://policy.service.gghltd.co.uk\"><pol:args0>#ADVISER_ID#</pol:args0><pol:args1>#INTERVIEW_ID#</pol:args1><pol:args2>#INTERVIEW_TOKEN#</pol:args2></pol:setApplicationDetailsForAdviser>";
//
//			String dataToSend = XML_TEMPLATE.replace("#ADVISER_ID#", externalToken)
//			                                                .replace("#INTERVIEW_ID#", createInterviewInterviewId)
//			                                                .replace("#INTERVIEW_TOKEN#", createInterviewInterviewToken);
//			
//			String serviceNamespace = "http://policy.service.gghltd.co.uk";
//			String portNamespace = "http://policy.service.gghltd.co.uk";
//			String portLocalName = "PolicyServiceSoap12Binding";
//			String endpointAddress = SG_FACADE_ENDPOINT + "/axis2/services/PolicyService/";
//			String serviceLocalName = "setApplicationDetailsForAdviser";
//			String soapAction = "http://policy.service.gghltd.co.uk/setApplicationDetailsForAdviser";
//			
//			response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
//					portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
//					SOAPBinding.SOAP11HTTP_BINDING);
//			System.out.println("Refesh  - " + response);
			
			int gg = 0;
			
			} catch (IOException ioe) {
				
			}
			return createInterviewInterviewId;			
			
	}
	
	public String processInterviewJoint(String uwInterviewId, String uwInterviewToken, String uwInterviewIdSa, String uwInterviewTokenSa) {
		
		try {
		
		setUp();
		Map<String, String> mAnswers = new HashMap<String, String>();
		
		// create an interview
		String response = createInterview();
		System.out.println("createInterview - " + response);
		refreshCache();

		
		// submitPage_SingleOrDual
		mAnswers.put("QD_APP_NUM", "joint");
		response = getNextPage();
		String submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);		
		response = submitPageNew(response, submitPageXml);
		System.out.println("Single or dual - " + response);
		
				
		// submitPage_ClientDetails
		mAnswers = new HashMap<String, String>();
		mAnswers.put("QD_POLDATA_PA_FNAME", "First name 1");
		mAnswers.put("QD_POLDATA_PA_LNAME", "Last name 1");
		mAnswers.put("QD_POLDATA_PA_GENDER", "male");
		mAnswers.put("QD_POLDATA_PA_DOB", "12/12/1998");
		mAnswers.put("QD_POLDATA_PA_SMOKER", "noneinthelast5");
		mAnswers.put("QD_POLDATA_PA_POSTCODE", "WD17 1DA");
		mAnswers.put("QD_POLDATA_PA_OCC", "businessanalyst");
		mAnswers.put("QD_POLDATA_PA_SALARY", "23456");
		mAnswers.put("QD_POLDATA_PA_WAIST", "changetoinches");
		mAnswers.put("QD_POLDATA_PA_WEIGHT", "changetostlbs");
		
		mAnswers.put("QD_POLDATA_SA_FNAME", "First name 2");
		mAnswers.put("QD_POLDATA_SA_LNAME", "Last name 2");
		mAnswers.put("QD_POLDATA_SA_GENDER", "male");
		mAnswers.put("QD_POLDATA_SA_DOB", "12/12/1998");
		mAnswers.put("QD_POLDATA_SA_SMOKER", "noneinthelast5");
		mAnswers.put("QD_POLDATA_SA_POSTCODE", "WD17 1DA");
		mAnswers.put("QD_POLDATA_SA_OCC", "businessanalyst");
		mAnswers.put("QD_POLDATA_SA_SALARY", "23456");
		mAnswers.put("QD_POLDATA_SA_WAIST", "changetoinches");
		mAnswers.put("QD_POLDATA_SA_WEIGHT", "changetostlbs");
		response = getNextPage();
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
		response = submitPageNew(response, submitPageXml);
		System.out.println("Client details - " + response);
		
		

		// submitPage_createQuote
		mAnswers = new HashMap<String, String>();
		mAnswers.put("QD_PRODSEL_QUOTEBY", "quotebysum");
		mAnswers.put("QD_PRODSEL_PA_TL_TOC_INSTANCE1", "level");
		mAnswers.put("QD_PRODSEL_PA_TL_TERM_INSTANCE1", "12");
		mAnswers.put("QD_PRODSEL_PA_TL_SUM_INSTANCE1", "100000");
		mAnswers.put("QD_PRODSEL_PA_TL_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_CI_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_REMOVE_CHILDCI", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_SELECT_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_SELECT_CHILDCI", "no");
		mAnswers.put("QD_PRODSEL_COMM_INDEM_PCT_1", "100");
		mAnswers.put("QD_PRODSEL_COMM_SACRIFICE_PCT", "0");
		mAnswers.put("H_QD_PRODSEL_FLAG_RECALC", "N/A");
		mAnswers.put("QD_SAQ", "N/A");
		mAnswers.put("QD_QUOTE_VALIDITY", "22/12/2018");
		
		mAnswers.put("QD_PRODSEL_SA_TL_TOC_INSTANCE1", "level");
		mAnswers.put("QD_PRODSEL_SA_TL_TERM_INSTANCE1", "12");
		mAnswers.put("QD_PRODSEL_SA_TL_SUM_INSTANCE1", "100000");
		mAnswers.put("QD_PRODSEL_SA_TL_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_SA_REMOVE_LIFE_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_CI_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_SA_REMOVE_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_REMOVE_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_REMOVE_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_SA_SELECT_LIFE_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_SA_SELECT_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_SELECT_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_SELECT_FRACTURE", "no");
				
		response = getNextPage();
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);		
		response = submitPageNew(response, submitPageXml);
		System.out.println("Create q - " + response);
		
		
		// Quote
		mAnswers = new HashMap<String, String>();
		mAnswers.put("QD_PRODSEL_QUOTEBY", "quotebysum");
		mAnswers.put("QD_PRODSEL_PA_TL_TOC_INSTANCE1", "level");
		mAnswers.put("QD_PRODSEL_PA_TL_TERM_INSTANCE1", "12");
		mAnswers.put("QD_PRODSEL_PA_TL_SUM_INSTANCE1", "100000");
		mAnswers.put("QD_PRODSEL_PA_TL_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_CI_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_REMOVE_CHILDCI", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_SELECT_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_SELECT_CHILDCI", "no");
		mAnswers.put("QD_PRODSEL_COMM_INDEM_PCT_1", "100");
		mAnswers.put("QD_PRODSEL_COMM_SACRIFICE_PCT", "0");
		mAnswers.put("H_QD_PRODSEL_FLAG_RECALC", "N/A");
		mAnswers.put("QD_SAQ", "N/A");
		mAnswers.put("QD_QUOTE_VALIDITY", "22/12/2018");
		
		mAnswers.put("QD_PRODSEL_SA_TL_TOC_INSTANCE1", "level");
		mAnswers.put("QD_PRODSEL_SA_TL_TERM_INSTANCE1", "12");
		mAnswers.put("QD_PRODSEL_SA_TL_SUM_INSTANCE1", "100000");
		mAnswers.put("QD_PRODSEL_SA_TL_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_SA_REMOVE_LIFE_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_CI_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_SA_REMOVE_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_REMOVE_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_REMOVE_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_SA_SELECT_LIFE_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_SA_SELECT_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_SELECT_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_SA_SELECT_FRACTURE", "no");
				
		response = getNextPage();
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
		response = submitPageNew(response, submitPageXml);		
		System.out.println("Quote - " + response);
		
		refreshCache();
		
//		//=======================================================
//		//Submit quote for Apply page
//		mAnswers = new HashMap<String, String>();
//		response = getNextPage();		
//		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
//		response = submitPageNew(response, submitPageXml);
//		System.out.println("Apply - " + response);
		
		//Set UW questions as complete
				String responseUpdateInterviewScores = updateInterviewScores("SG_PA_UW_QUESTIONS_COM", "true");
				responseUpdateInterviewScores = updateInterviewScores("SG_TRIGGERED_UW_INTERVIEW_ID", uwInterviewId);
				responseUpdateInterviewScores = updateInterviewScores("SG_TRIGGERED_UW_INTERVIEW_TOKEN", uwInterviewToken);
				
				responseUpdateInterviewScores = updateInterviewScores("SG_SA_UW_QUESTIONS_COM", "true");
				responseUpdateInterviewScores = updateInterviewScores("SG_TRIGGERED_SA_UW_INTERVIEW_ID", uwInterviewIdSa);
				responseUpdateInterviewScores = updateInterviewScores("SG_TRIGGERED_SA_UW_INTERVIEW_TOKEN", uwInterviewTokenSa);		
		
		//Submit Apply page
		mAnswers = new HashMap<String, String>();
		mAnswers.put("APP_PA_TITLE", "mr");
		mAnswers.put("APP_PA_EMAIL", "dd@ss.com");
		mAnswers.put("APP_PA_CONFIRM_EMAIL", "dd@ss.com");
		mAnswers.put("APP_PA_PHONE", "09888776655");
		mAnswers.put("APP_PA_FINDADD", "123");
		mAnswers.put("APP_PA_ADDRESS1", "26 DummyData Street");
		mAnswers.put("APP_PA_ADDRESS2", "Townsville");
		mAnswers.put("APP_PA_TOWN", "Castleford");
		mAnswers.put("APP_PA_COUNTY", "West Yorkshire");
		mAnswers.put("APP_PA_PCODE", "WF10 4AU");
		mAnswers.put("APP_PA_EXISTING_LIFE_COVER", "no");
		mAnswers.put("APP_PA_EXISTING_CI_COVER", "no");
		mAnswers.put("APP_MORTGAGE", "no");
		
		mAnswers.put("APP_SA_TITLE", "mr");
		mAnswers.put("APP_SA_EMAIL", "dd@ss.com");
		mAnswers.put("APP_SA_CONFIRM_EMAIL", "dd@ss.com");
		mAnswers.put("APP_SA_PHONE", "09888776655");
		mAnswers.put("APP_SA_FINDADD", "123");
		mAnswers.put("APP_SA_ADDRESS1", "26 DummyData Street");
		mAnswers.put("APP_SA_ADDRESS2", "Townsville");
		mAnswers.put("APP_SA_TOWN", "Castleford");
		mAnswers.put("APP_SA_COUNTY", "West Yorkshire");
		mAnswers.put("APP_SA_PCODE", "WF10 4AU");
		mAnswers.put("APP_SA_EXISTING_LIFE_COVER", "no");
		mAnswers.put("APP_SA_EXISTING_CI_COVER", "no");
		mAnswers.put("APP_MORTGAGE", "no");
		
		response = getNextPage();		
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
		response = submitPageNew(response, submitPageXml);
		System.out.println("Apply - " + response);
		
		
		
		mAnswers = new HashMap<String, String>();
		response = getNextPage();
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
		response = submitPageNew(response, submitPageXml);
		System.out.println("UW_LINKED - " + response);
		
//		//Decision
//		mAnswers = new HashMap<String, String>();
//		response = getNextPage();
//		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
//		response = submitPageNew(response, submitPageXml);
//		System.out.println("Decision - " + response);
//		
//		//Must update cache here
//		refreshCache();
		
		int gg = 0;
		
		} catch (IOException ioe) {
			int ff = 0;
		}
		return createInterviewInterviewId;
	}
	
	public String processInterview(String uwInterviewId, String uwInterviewToken) {
		
		try {
		
		setUp();
		Map<String, String> mAnswers = new HashMap<String, String>();
		
		// create an interview
		String response = createInterview();
		System.out.println("createInterview - " + response);
		refreshCache();

		
		// submitPage_SingleOrDual
		mAnswers.put("QD_APP_NUM", "single");
		response = getNextPage();
		String submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);		
		response = submitPageNew(response, submitPageXml);
		System.out.println("Single or dual - " + response);
		
				
		// submitPage_ClientDetails
		mAnswers = new HashMap<String, String>();
		mAnswers.put("QD_POLDATA_PA_FNAME", "First name 1");
		mAnswers.put("QD_POLDATA_PA_LNAME", "Last name 1");
		mAnswers.put("QD_POLDATA_PA_GENDER", "male");
		mAnswers.put("QD_POLDATA_PA_DOB", "12/12/1998");
		mAnswers.put("QD_POLDATA_PA_SMOKER", "noneinthelast5");
		mAnswers.put("QD_POLDATA_PA_POSTCODE", "WD17 1DA");
		mAnswers.put("QD_POLDATA_PA_OCC", "businessanalyst");
		mAnswers.put("QD_POLDATA_PA_SALARY", "23456");
		mAnswers.put("QD_POLDATA_PA_WAIST", "changetoinches");
		mAnswers.put("QD_POLDATA_PA_WEIGHT", "changetostlbs");
		response = getNextPage();
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
		response = submitPageNew(response, submitPageXml);
		System.out.println("Client details - " + response);
		
		

		// submitPage_createQuote
		mAnswers = new HashMap<String, String>();
		mAnswers.put("QD_PRODSEL_QUOTEBY", "quotebysum");
		mAnswers.put("QD_PRODSEL_PA_TL_TOC_INSTANCE1", "level");
		mAnswers.put("QD_PRODSEL_PA_TL_TERM_INSTANCE1", "12");
		mAnswers.put("QD_PRODSEL_PA_TL_SUM_INSTANCE1", "100000");
		mAnswers.put("QD_PRODSEL_PA_TL_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_CI_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_REMOVE_CHILDCI", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_SELECT_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_SELECT_CHILDCI", "no");
		mAnswers.put("QD_PRODSEL_COMM_INDEM_PCT_1", "100");
		mAnswers.put("QD_PRODSEL_COMM_SACRIFICE_PCT", "0");
		mAnswers.put("H_QD_PRODSEL_FLAG_RECALC", "N/A");
		mAnswers.put("QD_SAQ", "N/A");
		mAnswers.put("QD_QUOTE_VALIDITY", "22/12/2018");
				
		response = getNextPage();
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);		
		response = submitPageNew(response, submitPageXml);
		System.out.println("Create q - " + response);
		
		
		// Quote
		mAnswers = new HashMap<String, String>();
		mAnswers.put("QD_PRODSEL_QUOTEBY", "quotebysum");
		mAnswers.put("QD_PRODSEL_PA_TL_TOC_INSTANCE1", "level");
		mAnswers.put("QD_PRODSEL_PA_TL_TERM_INSTANCE1", "12");
		mAnswers.put("QD_PRODSEL_PA_TL_SUM_INSTANCE1", "100000");
		mAnswers.put("QD_PRODSEL_PA_TL_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_CI_ESC_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_REMOVE_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_REMOVE_CHILDCI", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1", "yes");
		mAnswers.put("QD_PRODSEL_PA_SELECT_CI_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_IP_INSTANCE1", "no");
		mAnswers.put("QD_PRODSEL_PA_SELECT_FRACTURE", "no");
		mAnswers.put("QD_PRODSEL_SELECT_CHILDCI", "no");
		mAnswers.put("QD_PRODSEL_COMM_INDEM_PCT_1", "100");
		mAnswers.put("QD_PRODSEL_COMM_SACRIFICE_PCT", "0");
		mAnswers.put("H_QD_PRODSEL_FLAG_RECALC", "N/A");
		mAnswers.put("QD_SAQ", "N/A");
		mAnswers.put("QD_QUOTE_VALIDITY", "22/12/2018");
				
		response = getNextPage();
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
		response = submitPageNew(response, submitPageXml);		
		System.out.println("Quote - " + response);
		
		
//		//=======================================================
//		//Submit quote for Apply page
//		mAnswers = new HashMap<String, String>();
//		response = getNextPage();		
//		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
//		response = submitPageNew(response, submitPageXml);
//		System.out.println("Apply - " + response);
		
		//Set UW questions as complete
				String responseUpdateInterviewScores = updateInterviewScores("SG_PA_UW_QUESTIONS_COM", "true");
				responseUpdateInterviewScores = updateInterviewScores("SG_TRIGGERED_UW_INTERVIEW_ID", uwInterviewId);
				responseUpdateInterviewScores = updateInterviewScores("SG_TRIGGERED_UW_INTERVIEW_TOKEN", uwInterviewToken);
		
		//Submit Apply page
		mAnswers = new HashMap<String, String>();
		mAnswers.put("APP_PA_TITLE", "mr");
		mAnswers.put("APP_PA_EMAIL", "dd@ss.com");
		mAnswers.put("APP_PA_CONFIRM_EMAIL", "dd@ss.com");
		mAnswers.put("APP_PA_PHONE", "09888776655");
		mAnswers.put("APP_PA_FINDADD", "123");
		mAnswers.put("APP_PA_ADDRESS1", "26 DummyData Street");
		mAnswers.put("APP_PA_ADDRESS2", "Townsville");
		mAnswers.put("APP_PA_TOWN", "Castleford");
		mAnswers.put("APP_PA_COUNTY", "West Yorkshire");
		mAnswers.put("APP_PA_PCODE", "WF10 4AU");
		mAnswers.put("APP_PA_EXISTING_LIFE_COVER", "no");
		mAnswers.put("APP_PA_EXISTING_CI_COVER", "no");
		mAnswers.put("APP_MORTGAGE", "no");
		response = getNextPage();		
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
		response = submitPageNew(response, submitPageXml);
		System.out.println("Apply - " + response);
		
		
		
		mAnswers = new HashMap<String, String>();
		response = getNextPage();
		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
		response = submitPageNew(response, submitPageXml);
		System.out.println("UW_LINKED - " + response);
		
//		//Decision
//		mAnswers = new HashMap<String, String>();
//		response = getNextPage();
//		submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
//		response = submitPageNew(response, submitPageXml);
//		System.out.println("Decision - " + response);
//		
//		//Must update cache here
//		refreshCache();
		
		int gg = 0;
		
		} catch (IOException ioe) {
			
		}
		return createInterviewInterviewId;
	}
	
	private void refreshCache() {
		String XML_TEMPLATE = "<pol:setApplicationDetailsForAdviser xmlns:pol=\"http://policy.service.gghltd.co.uk\"><pol:args0>#ADVISER_ID#</pol:args0><pol:args1>#INTERVIEW_ID#</pol:args1><pol:args2>#INTERVIEW_TOKEN#</pol:args2></pol:setApplicationDetailsForAdviser>";

		String dataToSend = XML_TEMPLATE.replace("#ADVISER_ID#", externalToken)
		                                                .replace("#INTERVIEW_ID#", createInterviewInterviewId)
		                                                .replace("#INTERVIEW_TOKEN#", createInterviewInterviewToken);
		
		String serviceNamespace = "http://policy.service.gghltd.co.uk";
		String portNamespace = "http://policy.service.gghltd.co.uk";
		String portLocalName = "PolicyServiceSoap12Binding";
		String endpointAddress = SG_FACADE_ENDPOINT + "/axis2/services/PolicyService/";
		String serviceLocalName = "setApplicationDetailsForAdviser";
		String soapAction = "http://policy.service.gghltd.co.uk/setApplicationDetailsForAdviser";
		
		String response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
				SOAPBinding.SOAP11HTTP_BINDING);
	}
	
private void decisionTest_TwoRecalcsAndThenSetUp () throws IOException {
		
		Map<String, String> mAnswers = new HashMap<String, String>();
	
	//Test 3.0.9-1a The first GNP will bring back all questions that are not guarded by SG_DEC_QUOTE_DONE
	String response = getNextPage();
	mAnswers = new HashMap<String, String>();			
	String submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
	response = submitPageNew(response, submitPageXml);
	

	//Test 3.0.9-2b Modify a value and we should re-run the quote and update questions that use the
	//quote response
	mAnswers = new HashMap<String, String>();
	mAnswers.put("H_DEC_QUOTE_FLAG_FOR_RECALC", "N/A-2");
	mAnswers.put("DECISION_PA_TL_SUM_INSTANCE1", "200000");	
	response = getNextPage();
	submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
	response = submitPageNew(response, submitPageXml);		
	
	mAnswers = new HashMap<String, String>();
	mAnswers.put("H_DEC_QUOTE_FLAG_FOR_RECALC", "N/A-3");
	mAnswers.put("DECISION_PA_TL_SUM_INSTANCE1", "300000");
	response = getNextPage();
	submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
	response = submitPageNew(response, submitPageXml);		
	
	//Final part - submit with no changes in order to get to Set up
	response = getNextPage();
	mAnswers = new HashMap<String, String>();		
	//mAnswers.put("H_DEC_QUOTE_FLAG_FOR_RECALC", "");
	submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
	response = submitPageNew(response, submitPageXml);	
}

	private void decisionTest_StraightThroughToSetUp () throws IOException {
		
		Map<String, String> mAnswers = new HashMap<String, String>();
		
		//Test 3.0.9-1a The first GNP will bring back all questions that are not guarded by SG_DEC_QUOTE_DONE
		String response = getNextPage();
				mAnswers = new HashMap<String, String>();			
				String submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
				response = submitPageNew(response, submitPageXml);
				
				//Test 3.0.9-1b - The submit above will have resulted in SG_DEC_QUOTE_DONE becomming true and therefore DECISION_TOT_PREM will become
				//unguarded and will not be returned from the GNP below. So the second submit here will submit DECISION_TOT_PREM as unchanged and the page will
				//therefore be COMPLETED and we will go to Setup.
				response = getNextPage();
				mAnswers = new HashMap<String, String>();
				submitPageXml = generateSubmitXml(response, submitPageTemplate, mAnswers);
				response = submitPageNew(response, submitPageXml);	
	}
	

	
//	private String getPageNumber(String xml) {
//		String pageNumber = org.apache.commons.lang3.StringUtils.substringBetween(xml, ">",
//				"</ax283:interviewPageNumber>");
//		pageNumber = pageNumber.substring(pageNumber.lastIndexOf(">") + 1);
//		return pageNumber;
//	}
	
	public String getInterviewId (String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		String interviewId="";
		
		try {
			builder = factory.newDocumentBuilder();
            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();            
            // Create XPath object
            XPath xpath = xpathFactory.newXPath();
            
            //File file = new File("C:\\temp\\xmlTestFiles\\createInterviewResponse.xml");
            //FileInputStream fis = new FileInputStream(file);

            InputSource is = new InputSource(new StringReader(xml));
            doc = builder.parse(is);
            
            xpath.setNamespaceContext(new UniversalNamespaceResolver(doc));
            
            XPathExpression expr = xpath.compile
            		("/createInterviewResponse/return/interviewId/text()");
            Node interviewIdNode = (Node)expr.evaluate(doc, XPathConstants.NODE);
           		              
            if (interviewIdNode != null) {
            	interviewId = interviewIdNode.getNodeValue();
            }
            
		} catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
		} catch (XPathExpressionException e) {
            e.printStackTrace();
        }           
		return interviewId;
	}	
	
	public String getInterviewToken (String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		String interviewToken="";
		
		try {
			builder = factory.newDocumentBuilder();
            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();            
            // Create XPath object
            XPath xpath = xpathFactory.newXPath();
            
            //File file = new File("C:\\temp\\xmlTestFiles\\createInterviewResponse.xml");
            //FileInputStream fis = new FileInputStream(file);

            InputSource is = new InputSource(new StringReader(xml));
            doc = builder.parse(is);
            
            xpath.setNamespaceContext(new UniversalNamespaceResolver(doc));
            
            XPathExpression expr = xpath.compile
            		("/createInterviewResponse/return/interviewToken/text()");
            Node interviewTokenNode = (Node)expr.evaluate(doc, XPathConstants.NODE);
           		              
            if (interviewTokenNode != null) {
            	interviewToken = interviewTokenNode.getNodeValue();
            }
            
		} catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
		} catch (XPathExpressionException e) {
            e.printStackTrace();
        }           
		return interviewToken;
	}
	
	public String getPageNumber (String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		String pageNumber="";
		
		try {
			builder = factory.newDocumentBuilder();
            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();            
            // Create XPath object
            XPath xpath = xpathFactory.newXPath();
            
            //File file = new File("C:\\temp\\xmlTestFiles\\createInterviewResponse.xml");
            //FileInputStream fis = new FileInputStream(file);

            InputSource is = new InputSource(new StringReader(xml));
            doc = builder.parse(is);
            
            xpath.setNamespaceContext(new UniversalNamespaceResolver(doc));
            
            XPathExpression expr = xpath.compile
            		("/getNextPageResponse/return/questionnairePage/interviewPageNumber/text()");
            Node pageNumberNode = (Node)expr.evaluate(doc, XPathConstants.NODE);
           		              
            if (pageNumberNode != null) {
            	pageNumber = pageNumberNode.getNodeValue();
            }
            
		} catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
		} catch (XPathExpressionException e) {
            e.printStackTrace();
        }           
		return pageNumber;
	}	
	
//	private String savePage(String sumAssured, String previousResponse) {
//		String XML_TEMPLATE = "<submitPage xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><submitPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_TOKEN#</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_ID#</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber><pageNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#PAGE_NUMBER#</pageNumber><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>quotebysum</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_QUOTEBY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>G000000515</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_TOT_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>114.13</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_COMMISSION</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_DISCOUNT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>13/09/2018</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_VALIDITY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_TEXT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;ns2:WssquotecallResponse xmlns:ns2=\"http://tempuri.org/Wssquote/\"&gt;&lt;WssquoteExport command=\"\" exitState=\"0\" exitStateType=\"3\" exitStateMsg=\"\"&gt;&lt;ExpGrpSum2app&gt;&lt;row&gt;&lt;ExpItmQuoteRefSum2appWebService&gt;&lt;QuoteRef/&gt;&lt;BenLine&gt;1&lt;/BenLine&gt;&lt;/ExpItmQuoteRefSum2appWebService&gt;&lt;ExpItmPrltypeDescSum2appWorkspace&gt;&lt;Text1&gt;Life&lt;/Text1&gt;&lt;/ExpItmPrltypeDescSum2appWorkspace&gt;&lt;ExpItmPrlSum2appProductLines&gt;&lt;Type&gt;2&lt;/Type&gt;&lt;/ExpItmPrlSum2appProductLines&gt;&lt;ExpItmPcpSum2appPolicyPremiums&gt;&lt;ModalPremium&gt;5.18&lt;/ModalPremium&gt;&lt;CombDiscount&gt;0.00&lt;/CombDiscount&gt;&lt;JointLifeDiscount&gt;0.00&lt;/JointLifeDiscount&gt;&lt;/ExpItmPcpSum2appPolicyPremiums&gt;&lt;/row&gt;&lt;row&gt;&lt;ExpItmQuoteRefSum2appWebService&gt;&lt;QuoteRef/&gt;&lt;BenLine/&gt;&lt;/ExpItmQuoteRefSum2appWebService&gt;&lt;ExpItmPrltypeDescSum2appWorkspace&gt;&lt;Text1/&gt;&lt;/ExpItmPrltypeDescSum2appWorkspace&gt;&lt;ExpItmPrlSum2appProductLines&gt;&lt;Type&gt;0&lt;/Type&gt;&lt;/ExpItmPrlSum2appProductLines&gt;&lt;ExpItmPcpSum2appPolicyPremiums&gt;&lt;ModalPremium&gt;5.18&lt;/ModalPremium&gt;&lt;CombDiscount&gt;0.00&lt;/CombDiscount&gt;&lt;JointLifeDiscount&gt;0.00&lt;/JointLifeDiscount&gt;&lt;/ExpItmPcpSum2appPolicyPremiums&gt;&lt;/row&gt;&lt;/ExpGrpSum2app&gt;&lt;ExpGrpErrors/&gt;&lt;ExpGrpCliCprRtn&gt;&lt;row&gt;&lt;ExpItmCprBlneRtnWebService&gt;&lt;BenLine&gt;1&lt;/BenLine&gt;&lt;Primaryprdid/&gt;&lt;QuoteRef/&gt;&lt;/ExpItmCprBlneRtnWebService&gt;&lt;ExpItmCprPrlRtnProductLines&gt;&lt;PrlId&gt;TLL         &lt;/PrlId&gt;&lt;/ExpItmCprPrlRtnProductLines&gt;&lt;ExpItmCprTypeRtnCliPolRelationships&gt;&lt;Type&gt;4&lt;/Type&gt;&lt;/ExpItmCprTypeRtnCliPolRelationships&gt;&lt;ExpItmCliRtnClients&gt;&lt;DateOfBirth&gt;19981212&lt;/DateOfBirth&gt;&lt;Sex&gt;2&lt;/Sex&gt;&lt;Smoker&gt;3&lt;/Smoker&gt;&lt;CliId&gt;0&lt;/CliId&gt;&lt;/ExpItmCliRtnClients&gt;&lt;/row&gt;&lt;/ExpGrpCliCprRtn&gt;&lt;ExpGrpCommissions&gt;&lt;row&gt;&lt;ExpItmComEarningPeriodPolicyCommissions&gt;&lt;InitialPeriodMonths&gt;48&lt;/InitialPeriodMonths&gt;&lt;/ExpItmComEarningPeriodPolicyCommissions&gt;&lt;ExpItmComIepPolicyCommissions&gt;&lt;InitialPeriodMonths&gt;49&lt;/InitialPeriodMonths&gt;&lt;/ExpItmComIepPolicyCommissions&gt;&lt;ExpItmComQrefWebService&gt;&lt;QuoteRef/&gt;&lt;BenLine&gt;1&lt;/BenLine&gt;&lt;Primaryprdid/&gt;&lt;/ExpItmComQrefWebService&gt;&lt;ExpItmComPasPolicyAgentShares&gt;&lt;Agent&gt;A00081  &lt;/Agent&gt;&lt;/ExpItmComPasPolicyAgentShares&gt;&lt;ExpItmComPrlPolicyCoverages&gt;&lt;ProductLine&gt;TLL         &lt;/ProductLine&gt;&lt;/ExpItmComPrlPolicyCoverages&gt;&lt;ExpItmComAmtAccountTransactions&gt;&lt;Amount&gt;0.13&lt;/Amount&gt;&lt;/ExpItmComAmtAccountTransactions&gt;&lt;ExpItmComIcePolicyCommissions&gt;&lt;TotalIce&gt;0.0&lt;/TotalIce&gt;&lt;/ExpItmComIcePolicyCommissions&gt;&lt;/row&gt;&lt;/ExpGrpCommissions&gt;&lt;ExpGrpCvgs&gt;&lt;row&gt;&lt;ExpItmMthsBrkvnWorkspace&gt;&lt;Integer1&gt;0&lt;/Integer1&gt;&lt;/ExpItmMthsBrkvnWorkspace&gt;&lt;ExpItmCvgsPrlProductLines&gt;&lt;Type&gt;2&lt;/Type&gt;&lt;PrimaryCoverage&gt;1&lt;/PrimaryCoverage&gt;&lt;/ExpItmCvgsPrlProductLines&gt;&lt;ExpItmCvgQuoteRefWebService&gt;&lt;QuoteRef/&gt;&lt;BenLine&gt;1&lt;/BenLine&gt;&lt;Contributions&gt;144&lt;/Contributions&gt;&lt;Primaryprdid/&gt;&lt;FsatotalpremiumsYearly&gt;0.0&lt;/FsatotalpremiumsYearly&gt;&lt;Fsatotalpremium&gt;0.0&lt;/Fsatotalpremium&gt;&lt;RiskBenefit/&gt;&lt;/ExpItmCvgQuoteRefWebService&gt;&lt;ExpItmPcpCvgsPolicyPremiums&gt;&lt;ModalPremium&gt;5.18&lt;/ModalPremium&gt;&lt;Frequency&gt;5&lt;/Frequency&gt;&lt;UnratedModalPremium&gt;5.18&lt;/UnratedModalPremium&gt;&lt;ArpmPremium&gt;0.00&lt;/ArpmPremium&gt;&lt;JointLifeDiscount&gt;0.00&lt;/JointLifeDiscount&gt;&lt;CombDiscount&gt;0.00&lt;/CombDiscount&gt;&lt;CombDiscountPc&gt;0.00000000&lt;/CombDiscountPc&gt;&lt;/ExpItmPcpCvgsPolicyPremiums&gt;&lt;ExpItmPocCvgsPolicyCoverages&gt;&lt;InitialSumAssured&gt;100000.0&lt;/InitialSumAssured&gt;&lt;ProductLine&gt;TLL         &lt;/ProductLine&gt;&lt;QuotationDate&gt;20180814&lt;/QuotationDate&gt;&lt;QuotationTime&gt;151005&lt;/QuotationTime&gt;&lt;Term&gt;12.0&lt;/Term&gt;&lt;PttYrs&gt;0.0&lt;/PttYrs&gt;&lt;Coverage&gt;1&lt;/Coverage&gt;&lt;MajorCoverage&gt;0&lt;/MajorCoverage&gt;&lt;SoldBasis&gt;0&lt;/SoldBasis&gt;&lt;CoverLevel&gt;   &lt;/CoverLevel&gt;&lt;LivesType&gt;1&lt;/LivesType&gt;&lt;TermAge&gt;0&lt;/TermAge&gt;&lt;Valuation&gt;0.0&lt;/Valuation&gt;&lt;PptAge&gt;0.0&lt;/PptAge&gt;&lt;Currency&gt;GBP  &lt;/Currency&gt;&lt;ProductVersion&gt;0&lt;/ProductVersion&gt;&lt;QuoteAgeDate&gt;20180814&lt;/QuoteAgeDate&gt;&lt;EscOptionAtStart&gt;2&lt;/EscOptionAtStart&gt;&lt;/ExpItmPocCvgsPolicyCoverages&gt;&lt;ExpItmPcmCvgsPolicyCommissions&gt;&lt;Style&gt;INDEM   &lt;/Style&gt;&lt;IndemnificationPercent&gt;0.00000000&lt;/IndemnificationPercent&gt;&lt;TotalIce&gt;0.0&lt;/TotalIce&gt;&lt;CommissionablePremium&gt;62.16&lt;/CommissionablePremium&gt;&lt;TotalIcSacrifice&gt;0.0&lt;/TotalIcSacrifice&gt;&lt;InitialPeriodMonths&gt;48&lt;/InitialPeriodMonths&gt;&lt;DiscountedTotalIce&gt;114.13&lt;/DiscountedTotalIce&gt;&lt;IndemnifiedValue&gt;134.27&lt;/IndemnifiedValue&gt;&lt;NonIndemnifiedValue&gt;0.00&lt;/NonIndemnifiedValue&gt;&lt;/ExpItmPcmCvgsPolicyCommissions&gt;&lt;ExpItmPpcCvgsPolicyPremiumChanges&gt;&lt;ChangeIndex&gt;0.0&lt;/ChangeIndex&gt;&lt;/ExpItmPpcCvgsPolicyPremiumChanges&gt;&lt;/row&gt;&lt;/ExpGrpCvgs&gt;&lt;ExpGrpPrmSumByref&gt;&lt;row&gt;&lt;ExpItmPrimAdjpremPolicyPremiums&gt;&lt;ModalPremium&gt;5.18&lt;/ModalPremium&gt;&lt;/ExpItmPrimAdjpremPolicyPremiums&gt;&lt;ExpItmPrimDiscountPolicyPremiums&gt;&lt;ModalPremium&gt;0.0&lt;/ModalPremium&gt;&lt;/ExpItmPrimDiscountPolicyPremiums&gt;&lt;ExpItmPrmSumPrlidProductLines&gt;&lt;PrlId&gt;TLL         &lt;/PrlId&gt;&lt;/ExpItmPrmSumPrlidProductLines&gt;&lt;ExpItmPrmSumAswcalcq&gt;&lt;Sumpremium&gt;5.18&lt;/Sumpremium&gt;&lt;/ExpItmPrmSumAswcalcq&gt;&lt;ExpItmPsumQrefWebService&gt;&lt;QuoteRef/&gt;&lt;BenLine&gt;1&lt;/BenLine&gt;&lt;Primaryprdid/&gt;&lt;/ExpItmPsumQrefWebService&gt;&lt;/row&gt;&lt;/ExpGrpPrmSumByref&gt;&lt;ExpGrpPlcRtn/&gt;&lt;ExpGrpWarningMsg/&gt;&lt;ExpGrpComTot&gt;&lt;row&gt;&lt;ExpItmComEarningPeriodTotPolicyCommissions&gt;&lt;InitialPeriodMonths&gt;48&lt;/InitialPeriodMonths&gt;&lt;/ExpItmComEarningPeriodTotPolicyCommissions&gt;&lt;ExpItmComIepTotPolicyCommissions&gt;&lt;InitialPeriodMonths&gt;49&lt;/InitialPeriodMonths&gt;&lt;/ExpItmComIepTotPolicyCommissions&gt;&lt;ExpItmComQrefTotWebService&gt;&lt;QuoteRef/&gt;&lt;BenLine&gt;1&lt;/BenLine&gt;&lt;Primaryprdid/&gt;&lt;/ExpItmComQrefTotWebService&gt;&lt;ExpItmComPrlTotPolicyCoverages&gt;&lt;ProductLine&gt;TLL         &lt;/ProductLine&gt;&lt;/ExpItmComPrlTotPolicyCoverages&gt;&lt;ExpItmComRcTotAccountTransactions&gt;&lt;Amount&gt;0.13&lt;/Amount&gt;&lt;/ExpItmComRcTotAccountTransactions&gt;&lt;ExpItmComIceTotPolicyCommissions&gt;&lt;TotalIce&gt;0.0&lt;/TotalIce&gt;&lt;/ExpItmComIceTotPolicyCommissions&gt;&lt;/row&gt;&lt;/ExpGrpComTot&gt;&lt;ExpGrpTotIndemnity/&gt;&lt;ExpGrpFsaTot/&gt;&lt;ExpGrpPremDetails/&gt;&lt;ExpGrpPremDetailsTot/&gt;&lt;/WssquoteExport&gt;&lt;/ns2:WssquotecallResponse&gt;&lt;winningEntryKey&gt;&lt;/winningEntryKey&gt;&lt;termChildCi&gt;&lt;/termChildCi&gt;</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_ERROR</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_EXT_THIRD_PARTY_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>key-facts.pdf</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_KEY_FACTS</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>G000000515-quote.pdf</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_QUOTE_DOC</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_KEY_FACTS_DISABLE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_QUOTE_DOC_DISABLE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_TOTAL_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_BLANK</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>level</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>15</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>100000</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_SUM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_ESC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_SUM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_ESC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_CI_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_DP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_DP2_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_IP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_FRACTURE_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_FRACTURE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_FRACTURE_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_BLANK</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_SUM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.0</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_TERM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_TEXT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_REMOVE_CHILDCI</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_CI_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_IP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_FRACTURE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_SELECT_CHILDCI</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"> QD_PRODSEL_COMM_PREM_ORIGINAL</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_COMM_ORIGINAL</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_PREM_REVISED</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>114.13</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_COMM_REVISED</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>100</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_INDEM_PCT_1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_INDEMNITY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_SACRIFICE_PCT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>N/A</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC</code></questions></submitPageRequest></submitPage>";
//
//		uniqueId ++;
//		
//		String interviewId = getInterviewId(XML_TEMPLATE);
//		String interviewToken = getInterviewToken(XML_TEMPLATE);
//		String pageNumber = getPageNumber(previousResponse);
//		
//		String dataToSend = XML_TEMPLATE.replace(createInterviewInterviewId, interviewId)
//				.replace(createInterviewInterviewToken, interviewToken).replace("#PAGE_NUMBER#", pageNumber)
//				.replace("#SUM_ASSURED#", sumAssured)
//				.replace(
//						"<value>N/A</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC",
//						"<value>" + String.valueOf(uniqueId)
//								+ "</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC");
//		
//		
//		
//		dataToSend = dataToSend.replace("xmlns=\"http://service.underwriting.webservice.liss.co.uk\"",
//				"xmlns=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsi=\"xsi\" ");
//
//		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
//		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
//		String portLocalName = "UnderwritingServiceSoap11Binding";
//		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
//		String serviceLocalName = "savePage";
//		String soapAction = "http://service.underwriting.webservice.liss.co.uk/savePage";
//
//		String response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
//				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
//				SOAPBinding.SOAP11HTTP_BINDING);
//		return response;
//
//	}	

//	private String submitPage_apply(String sumAssured, String unique, String previousResponse) throws IOException {
//	//	String XML_TEMPLATE = "<submitPage xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><submitPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_TOKEN#</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_ID#</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber><pageNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#PAGE_NUMBER#</pageNumber><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>quotebysum</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_QUOTEBY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>G000000534</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_TOT_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>114.13</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_COMMISSION</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_DISCOUNT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>13/09/2018</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_VALIDITY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_TEXT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_EXT_THIRD_PARTY_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>key-facts.pdf</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_KEY_FACTS</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>G000000534-quote.pdf</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_QUOTE_DOC</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_KEY_FACTS_DISABLE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_QUOTE_DOC_DISABLE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_TOTAL_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_BLANK</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>level</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>12</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>100000</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_SUM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_ESC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_SUM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_ESC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_CI_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_DP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_DP2_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_IP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_FRACTURE_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_FRACTURE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_FRACTURE_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_BLANK</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_SUM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.0</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_TERM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_TEXT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_REMOVE_CHILDCI</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_CI_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_IP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_FRACTURE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_SELECT_CHILDCI</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"> QD_PRODSEL_COMM_PREM_ORIGINAL</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_COMM_ORIGINAL</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_PREM_REVISED</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>114.13</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_COMM_REVISED</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>100</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_INDEM_PCT_1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_INDEMNITY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_SACRIFICE_PCT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>N/A</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC</code></questions></submitPageRequest></submitPage>";
//
//		String XML_TEMPLATE = readFileForTest("interviewTester\\xml", "quoteApply");
//		
//		String interviewId = getInterviewId(XML_TEMPLATE);
//		String interviewToken = getInterviewToken(XML_TEMPLATE);
//		String pageNumber = getPageNumber(previousResponse);
//		
//		String dataToSend = XML_TEMPLATE.replace(createInterviewInterviewId, interviewId)
//				.replace(createInterviewInterviewToken, interviewToken).replace("#PAGE_NUMBER#", pageNumber)
//				.replace("#SUM_ASSURED#", sumAssured).replace(
//						"<value>N/A</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC",
//						"<value>" + String.valueOf(unique)
//								+ "</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC");
//		;
//		dataToSend = dataToSend.replace("xmlns=\"http://service.underwriting.webservice.liss.co.uk\"",
//				"xmlns=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsi=\"xsi\" ");
//		
//		System.out.println("submitPage_apply dataToSend = " + dataToSend);
//
//		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
//		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
//		String portLocalName = "UnderwritingServiceSoap11Binding";
//		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
//		String serviceLocalName = "submitPage";
//		String soapAction = "http://service.underwriting.webservice.liss.co.uk/submitPage";
//
//		String response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
//				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
//				SOAPBinding.SOAP11HTTP_BINDING);
//		return response;
//
//	}
	
	private String submitPage_recalcQuote(String sumAssured, String previousResponse) {
		String XML_TEMPLATE = "<submitPage xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><submitPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_TOKEN#</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_ID#</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber><pageNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#PAGE_NUMBER#</pageNumber><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>quotebysum</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_QUOTEBY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>G000000534</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_TOT_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>114.13</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_COMMISSION</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_DISCOUNT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>13/09/2018</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_VALIDITY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_TEXT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_EXT_THIRD_PARTY_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>key-facts.pdf</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_KEY_FACTS</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>G000000534-quote.pdf</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_QUOTE_DOC</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_KEY_FACTS_DISABLE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_QUOTE_DOC_DISABLE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_TOTAL_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_BLANK</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>level</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>12</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>100000</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_SUM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_ESC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_SUM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_ESC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_CI_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_DP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_DP2_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_IP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_PA_FRACTURE_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_FRACTURE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.00</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_QUOTE_SA_FRACTURE_PREM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_BLANK</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_SUM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0.0</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_TERM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_TEXT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_REMOVE_CHILDCI</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_CI_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_IP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_FRACTURE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_SELECT_CHILDCI</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"> QD_PRODSEL_COMM_PREM_ORIGINAL</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_COMM_ORIGINAL</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>5.18</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_PREM_REVISED</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>114.13</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_COMM_REVISED</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>100</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_INDEM_PCT_1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_INDEMNITY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_SACRIFICE_PCT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>N/A</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC</code></questions></submitPageRequest></submitPage>";

		uniqueId ++;
		
		String interviewId = getInterviewId(XML_TEMPLATE);
		String interviewToken = getInterviewToken(XML_TEMPLATE);
		String pageNumber = getPageNumber(previousResponse);
		
		String dataToSend = XML_TEMPLATE.replace(createInterviewInterviewId, interviewId)
				.replace(createInterviewInterviewToken, interviewToken).replace("#PAGE_NUMBER#", pageNumber)
				.replace("#SUM_ASSURED#", sumAssured).replace(
						"<value>N/A</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC",
						"<value>" + String.valueOf(uniqueId)
								+ "</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC");
		;
		dataToSend = dataToSend.replace("xmlns=\"http://service.underwriting.webservice.liss.co.uk\"",
				"xmlns=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsi=\"xsi\" ");
		
		System.out.println("submitPage_recalcQuote dataToSend = " + dataToSend);

		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portLocalName = "UnderwritingServiceSoap11Binding";
		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
		String serviceLocalName = "submitPage";
		String soapAction = "http://service.underwriting.webservice.liss.co.uk/submitPage";

		String response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
				SOAPBinding.SOAP11HTTP_BINDING);
		return response;

	}

	private String submitPage_createQuote(String previousResponse) {
		String XML_TEMPLATE = "<submitPage xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><submitPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_TOKEN#</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_ID#</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber><pageNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#PAGE_NUMBER#</pageNumber><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>quotebysum</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_QUOTEBY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_EXT_THIRD_PARTY_REF</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_BLANK</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>level</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>12</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>100000</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_SUM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_TL_ESC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_LIFE_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_SUM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_CI_ESC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_CI_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_TERM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_YEARS_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_TOC_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_DP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_DP2_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_MB_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_IP_PREM_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_IP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_REMOVE_FRACTURE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_BLANK</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_SUM</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_CHILDCI_TEXT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_REMOVE_CHILDCI</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>yes</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_LIFE_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_CI_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_IP_INSTANCE1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_PA_SELECT_FRACTURE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>no</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_SELECT_CHILDCI</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>100</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_INDEM_PCT_1</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_INDEMNITY</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>0</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_PRODSEL_COMM_SACRIFICE_PCT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>N/A</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">H_QD_PRODSEL_FLAG_RECALC</code></questions></submitPageRequest></submitPage>";

		String interviewId = getInterviewId(XML_TEMPLATE);
		String interviewToken = getInterviewToken(XML_TEMPLATE);
		String pageNumber = getPageNumber(previousResponse);
		
		String dataToSend = XML_TEMPLATE.replace(createInterviewInterviewId, interviewId)
				.replace(createInterviewInterviewToken, interviewToken).replace("#PAGE_NUMBER#", pageNumber);
		dataToSend = dataToSend.replace("xmlns=\"http://service.underwriting.webservice.liss.co.uk\"",
				"xmlns=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsi=\"xsi\" ");

		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portLocalName = "UnderwritingServiceSoap11Binding";
		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
		String serviceLocalName = "submitPage";
		String soapAction = "http://service.underwriting.webservice.liss.co.uk/submitPage";

		String response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
				SOAPBinding.SOAP11HTTP_BINDING);
		return response;

	}

	private String submitPage_ClientDetails(String previousResponse) {
		String XML_TEMPLATE = "<submitPage xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><submitPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_TOKEN#</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_ID#</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber><pageNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#PAGE_NUMBER#</pageNumber><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_INSURED</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_TITLE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>mike</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_FNAME</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>boboo</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_LNAME</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>male</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_GENDER</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>12/12/1998</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_DOB</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>noneinthelast5</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_SMOKER</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_HEIGHT_CM_VALUE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_HEIGHT_FT_VALUE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_HEIGHT_IN_VALUE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>changetoft</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_HEIGHT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_WEIGHT_KG_VALUE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_WEIGHT_ST_VALUE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_WEIGHT_LBS_VALUE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>changetostlbs</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_WEIGHT</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_WAIST_CM_VALUE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_WAIST_IN_VALUE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>changetoinches</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_WAIST</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value/></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_DRESS</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>hp43nw</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_POSTCODE</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>businessadviser</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_OCC</code></questions><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>23456</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_POLDATA_PA_SALARY</code></questions></submitPageRequest></submitPage>";

		String interviewId = getInterviewId(XML_TEMPLATE);
		String interviewToken = getInterviewToken(XML_TEMPLATE);
		String pageNumber = getPageNumber(previousResponse);
		
		String dataToSend = XML_TEMPLATE.replace(createInterviewInterviewId, interviewId)
				.replace(createInterviewInterviewToken, interviewToken).replace("#PAGE_NUMBER#", pageNumber);
		dataToSend = dataToSend.replace("xmlns=\"http://service.underwriting.webservice.liss.co.uk\"",
				"xmlns=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsi=\"xsi\" ");

		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portLocalName = "UnderwritingServiceSoap11Binding";
		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
		String serviceLocalName = "submitPage";
		String soapAction = "http://service.underwriting.webservice.liss.co.uk/submitPage";

		String response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
				SOAPBinding.SOAP11HTTP_BINDING);
		return response;

	}
	
private String updateInterviewScores(String scoreGroupName, String scoreGroupValue) throws IOException {
		
		String XML_TEMPLATE = "<ser:updateInterviewScores xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsd=\"http://request.core.webservice.liss.co.uk/xsd\" xmlns:xsd1=\"http://domain.core.webservice.liss.co.uk/xsd\" xmlns:xsd2=\"http://request.underwriting.webservice.liss.co.uk/xsd\" xmlns:xsd3=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><ser:updateInterviewScoresRequest><xsd:auditData><xsd1:ghostUserId>3</xsd1:ghostUserId><xsd1:pageCode>4</xsd1:pageCode><xsd1:userId>5</xsd1:userId></xsd:auditData><xsd2:interviewToken>lBM6vx9RMPj4sJt8QNDK</xsd2:interviewToken><xsd2:interviewId>11259</xsd2:interviewId><xsd2:lifeNumber>1</xsd2:lifeNumber><!--Zero or more repetitions:--><xsd2:score><xsd3:scoreGroupName>SG_PA_UW_QUESTIONS_COM</xsd3:scoreGroupName><xsd3:value>true</xsd3:value></xsd2:score></ser:updateInterviewScoresRequest></ser:updateInterviewScores>";
		
		String dataToSend = XML_TEMPLATE;
		dataToSend = updateXml(dataToSend, createInterviewInterviewId
				, "/updateInterviewScores/updateInterviewScoresRequest/interviewId/text()");
		
		dataToSend = updateXml(dataToSend, createInterviewInterviewToken
				, "/updateInterviewScores/updateInterviewScoresRequest/interviewToken/text()");
		
		dataToSend = updateXml(dataToSend, scoreGroupName
				, "/updateInterviewScores/updateInterviewScoresRequest/score/scoreGroupName/text()");
		
		dataToSend = updateXml(dataToSend, scoreGroupValue
				, "/updateInterviewScores/updateInterviewScoresRequest/score/value/text()");
		
		dataToSend = dataToSend.replace("xmlns=\"http://service.underwriting.webservice.liss.co.uk\"",
				"xmlns=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsi=\"xsi\" ")
				.replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "");

		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portLocalName = "UnderwritingServiceSoap11Binding";
		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
		String serviceLocalName = "updateInterviewScores";
		String soapAction = "http://service.underwriting.webservice.liss.co.uk/updateInterviewScores";

		return HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
				SOAPBinding.SOAP11HTTP_BINDING);


	}

private String submitPageNew(String previousResponse, String xml) throws IOException {
	
	String pageNumber = getPageNumber(previousResponse);
	
	String dataToSend = xml;
	dataToSend = updateXml(dataToSend, createInterviewInterviewId
			, "/submitPage/submitPageRequest/interviewId/text()");
	
	dataToSend = updateXml(dataToSend, createInterviewInterviewToken
			, "/submitPage/submitPageRequest/interviewToken/text()");
	
	dataToSend = updateXml(dataToSend, pageNumber
			, "/submitPage/submitPageRequest/pageNumber/text()");
	
	dataToSend = dataToSend.replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "");		
	

	String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
	String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
	String portLocalName = "UnderwritingServiceSoap11Binding";
	String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
	String serviceLocalName = "submitPage";
	String soapAction = "http://service.underwriting.webservice.liss.co.uk/submitPage";

	String response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
			portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
			SOAPBinding.SOAP11HTTP_BINDING);
	
	if (response == null) {
		throw new IOException("Failed to call submitPage");
	} else {
		//Check for application errors
		int ff = 9;
	}

	return response;
	

}	


//private String savePage(String previousResponse, String xml) throws IOException {
//	
//	String pageNumber = getPageNumber(previousResponse);
//	
//	String dataToSend = xml;
//	dataToSend = updateXml(dataToSend, createInterviewInterviewId
//			, "/submitPage/submitPageRequest/interviewId/text()");
//	
//	dataToSend = updateXml(dataToSend, createInterviewInterviewToken
//			, "/submitPage/submitPageRequest/interviewToken/text()");
//	
//	dataToSend = updateXml(dataToSend, pageNumber
//			, "/submitPage/submitPageRequest/pageNumber/text()");
//	
//	dataToSend = dataToSend.replace("xmlns=\"http://service.underwriting.webservice.liss.co.uk\"",
//			"xmlns=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsi=\"xsi\" ")
//			.replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "");		
//	
//
//	String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
//	String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
//	String portLocalName = "UnderwritingServiceSoap11Binding";
//	String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
//	String serviceLocalName = "submitPage";
//	String soapAction = "http://service.underwriting.webservice.liss.co.uk/submitPage";
//
//	return HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
//			portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
//			SOAPBinding.SOAP11HTTP_BINDING);
//
//
//}	


//	
//	private String submitPage(String previousResponse, String xmlFile) throws IOException {
//		return submitPage(previousResponse, xmlFile, 0);
//	}

//	private String savePage(String previousResponse, String xmlFile) throws IOException {
//		
//		String XML_TEMPLATE = readFileForTest("interviewTester\\xml", xmlFile);
//		
//		String pageNumber = getPageNumber(previousResponse);
//		
//		String dataToSend = XML_TEMPLATE;
//		dataToSend = updateXml(dataToSend, createInterviewInterviewId
//				, "/savePage/savePageRequest/interviewId/text()");
//		
//		dataToSend = updateXml(dataToSend, createInterviewInterviewToken
//				, "/savePage/savePageRequest/interviewToken/text()");
//		
//		dataToSend = updateXml(dataToSend, pageNumber
//				, "/savePage/savePageRequest/pageNumber/text()");
//		
//		dataToSend = dataToSend.replace("xmlns=\"http://service.underwriting.webservice.liss.co.uk\"",
//				"xmlns=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsi=\"xsi\" ")
//				.replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "");		
//		
//
//		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
//		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
//		String portLocalName = "UnderwritingServiceSoap11Binding";
//		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
//		String serviceLocalName = "savePage";
//		String soapAction = "http://service.underwriting.webservice.liss.co.uk/savePage";
//
//		return HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
//				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
//				SOAPBinding.SOAP11HTTP_BINDING);
//
//
//	}	

	private String submitPage_SingleOrDual(String previousResponse) throws IOException {
		
		String XML_TEMPLATE = "<submitPage xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><submitPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">m3CVzfN6LuzIzgb7R55b</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">8217</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber><pageNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">2</pageNumber><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>single</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_APP_NUM</code></questions></submitPageRequest></submitPage>";
		
		//String XML_TEMPLATE = "<submitPage xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><submitPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_TOKEN#</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#INTERVIEW_ID#</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber><pageNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">2</pageNumber><questions xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><answers xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\"><value>single</value></answers><code xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">QD_APP_NUM</code></questions></submitPageRequest></submitPage>";

		String pageNumber = getPageNumber(previousResponse);
		
		String dataToSend = XML_TEMPLATE;
		dataToSend = updateXml(dataToSend, createInterviewInterviewId
				, "/submitPage/submitPageRequest/interviewId/text()");
		
		dataToSend = updateXml(dataToSend, createInterviewInterviewToken
				, "/submitPage/submitPageRequest/interviewToken/text()");
		
		dataToSend = updateXml(dataToSend, pageNumber
				, "/submitPage/submitPageRequest/pageNumber/text()");
		
		dataToSend = dataToSend.replace("xmlns=\"http://service.underwriting.webservice.liss.co.uk\"",
				"xmlns=\"http://service.underwriting.webservice.liss.co.uk\" xmlns:xsi=\"xsi\" ")
				.replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "");

		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portLocalName = "UnderwritingServiceSoap11Binding";
		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
		String serviceLocalName = "submitPage";
		String soapAction = "http://service.underwriting.webservice.liss.co.uk/submitPage";

		return HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
				SOAPBinding.SOAP11HTTP_BINDING);


	}

	/////////////////////////// Static Below Here

	private String getNextPage() throws IOException {
		//String XML_TEMPLATE = "<getNextPage xmlns:xsi=\"xsi\" xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><getNextPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">token</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1234</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber></getNextPageRequest></getNextPage>";
		
		String XML_TEMPLATE = "<getNextPage xmlns:xsi=\"xsi\" xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><getNextPageRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xsi:nil=\"true\" xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\"/></auditData><interviewToken xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">m3CVzfN6LuzIzgb7R55b</interviewToken><interviewId xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">8217</interviewId><lifeNumber xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">1</lifeNumber></getNextPageRequest></getNextPage>";
		
		String dataToSend = XML_TEMPLATE;
		dataToSend = updateXml(dataToSend, createInterviewInterviewId
				, "/getNextPage/getNextPageRequest/interviewId/text()");
		
		dataToSend = updateXml(dataToSend, createInterviewInterviewToken
				, "/getNextPage/getNextPageRequest/interviewToken/text()");
		
		dataToSend = dataToSend.replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "");

		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portLocalName = "UnderwritingServiceSoap11Binding";
		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
		String serviceLocalName = "getNextPage";
		String soapAction = "http://service.underwriting.webservice.liss.co.uk/getNextPage";

		String response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
				SOAPBinding.SOAP11HTTP_BINDING);
		
		if (response == null) {
			throw new IOException("Failed to call getNextPage");
		} 
		return response;

	}

	private String createInterview() throws IOException {
		
		String XML_TEMPLATE = "<createInterview xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://service.underwriting.webservice.liss.co.uk\"><createInterviewRequest><auditData xmlns=\"http://request.core.webservice.liss.co.uk/xsd\"><ghostUserId xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><pageCode xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\">service</pageCode><userId xmlns=\"http://domain.core.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/></auditData><interviewScores xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><initialValue xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><scoreCategory xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><scoreDescription xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><scoreGroupName xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">EXTERNAL_TOKEN</scoreGroupName><value xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">#EXTERNAL_TOKEN#</value></interviewScores><interviewScores xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><initialValue xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><scoreCategory xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><scoreDescription xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><scoreGroupName xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">SG_INDEM_ALLOWED</scoreGroupName><value xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">Y</value></interviewScores><lives xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><clientId xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">1</clientId></lives><reference xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><rulebookName xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\">#RULEBOOK_NB#</rulebookName><windToPageCode xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><interviewScores xmlns=\"http://request.underwriting.webservice.liss.co.uk/xsd\"><initialValue xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><scoreCategory xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><scoreDescription xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\" xsi:nil=\"true\"/><scoreGroupName xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">UW_RULEBOOK_NAME</scoreGroupName><value xmlns=\"http://domain.underwriting.webservice.liss.co.uk/xsd\">#RULEBOOK_UW#</value></interviewScores></createInterviewRequest></createInterview>";
		
		String dataToSend = XML_TEMPLATE.replace("#EXTERNAL_TOKEN#", externalToken)
										.replace("#RULEBOOK_NB#", rulebookNb)
										.replace("#RULEBOOK_UW#", rulebookUw);

		String serviceNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portNamespace = "http://service.underwriting.webservice.liss.co.uk";
		String portLocalName = "UnderwritingServiceSoap11Binding";
		String endpointAddress = "https://"+server_dns+":8543/ulissia-webservices/services/UnderwritingService.UnderwritingServiceHttpsSoap11Endpoint/";
		String serviceLocalName = "createInterview";
		String soapAction = "http://service.underwriting.webservice.liss.co.uk/createInterview";

		String response = HttpHelper.sendSoapRequest(dataToSend, serviceNamespace, serviceLocalName, portNamespace,
				portLocalName, endpointAddress, soapAction, new HashMap<String, String>(),
				SOAPBinding.SOAP11HTTP_BINDING);
		
		createInterviewInterviewId = getInterviewId(response);
		createInterviewInterviewToken = getInterviewToken(response);
		
		return response;
	}
	
	private String generateSubmitXml (String xml, String templateFile, Map<String, String> mAnswers) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document docGetNextPage = null;
		
		Document docSubmitPage = null;
		
		String updatedXml="";
		try {
			builder = factory.newDocumentBuilder();
            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();            
            // Create XPath object
            

            //Xpath prep the getNextPage response XML
            XPath xpathGetNextPage = xpathFactory.newXPath();
            InputSource isGetNextPage = new InputSource(new StringReader(xml));
            docGetNextPage = builder.parse(isGetNextPage);
            xpathGetNextPage.setNamespaceContext(new UniversalNamespaceResolver(docGetNextPage));
            XPathExpression exprGetNextPage = xpathGetNextPage.compile("/getNextPageResponse/return/questionnairePage/sections/columns/questions/code");
            
            
            //Xpath prep the template - this is the submitPage template
            String XML_TEMPLATE = templateFile;
            XPath xpathSubmitPage = xpathFactory.newXPath();
            InputSource isSubmitPage = new InputSource(new StringReader(XML_TEMPLATE));
            
            docSubmitPage = builder.parse(isSubmitPage);
            xpathSubmitPage.setNamespaceContext(new UniversalNamespaceResolver(docSubmitPage));       
            XPathExpression exprSubmitPage = xpathGetNextPage.compile("/submitPage/submitPageRequest/questions");
            Node templateQuestionsAndAnswerNode = (Node)exprSubmitPage.evaluate(docSubmitPage, XPathConstants.NODE);
            
            //Below is the node onto which we will hang new answers.
            XPathExpression exprParent = xpathSubmitPage.compile("/submitPage/submitPageRequest");
            Node copyTo = (Node)exprParent.evaluate(docSubmitPage, XPathConstants.NODE);
            
	        NodeList nodesGetNextPage = (NodeList) exprGetNextPage.evaluate(docGetNextPage, XPathConstants.NODESET);
	        for (int i = 0; i < nodesGetNextPage.getLength(); i++) {
	        	
                Node codeNode = nodesGetNextPage.item(i);
                
                String answerString = codeNode.getPreviousSibling() == null ? null : codeNode.getPreviousSibling().getFirstChild().getTextContent();
                                	
                String actualAnswer = "";
                if (answerString != null) {
                	//System.out.println("Code : " + code.getTextContent() +" -> " + answer.getTextContent());
                	actualAnswer = answerString;
                } else {
                	//System.out.println("Code : " + code.getTextContent() +" -> " + "null");
                }
                
                //Check to see if we have an override answer
                if (mAnswers != null && mAnswers.containsKey(codeNode.getTextContent())) {
                	actualAnswer = mAnswers.get(codeNode.getTextContent());
                }
                
                //Now build an answer node on the template
                Node newQuestionsAndAnswerNode = docSubmitPage.importNode(templateQuestionsAndAnswerNode, true);
                copyTo.appendChild(newQuestionsAndAnswerNode);
                
                //Now update the question code and answer
              //Update the name of the score group
                for (int j = 0; j < newQuestionsAndAnswerNode.getChildNodes().getLength(); j++) {
                	if (newQuestionsAndAnswerNode.getChildNodes().item(j).getNodeName().equals("code")) {
                		newQuestionsAndAnswerNode.getChildNodes().item(j).getFirstChild().setTextContent(codeNode.getTextContent());
                	}
                	if (newQuestionsAndAnswerNode.getChildNodes().item(j).getNodeName().equals("answers")) {
                		
                		newQuestionsAndAnswerNode.getChildNodes().item(j).getFirstChild().setTextContent(actualAnswer);
                		
                	}                	
                }
                
                
                
	        }
	        
	        //Remove the template answer node
	        copyTo.removeChild(templateQuestionsAndAnswerNode);
	        
	        updatedXml = printXmlDocument(docSubmitPage);
            
		} catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
		} catch (XPathExpressionException e) {
            e.printStackTrace();
        }            
        return updatedXml ;   
	}	
	
//	private String generateSubmitXml (String xml, String templateFile, Map<String, String> mAnswers) {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder;
//		Document docGetNextPage = null;
//		
//		Document docSubmitPage = null;
//		
//		String updatedXml="";
//		try {
//			builder = factory.newDocumentBuilder();
//            // Create XPathFactory object
//            XPathFactory xpathFactory = XPathFactory.newInstance();            
//            // Create XPath object
//            
//
//            //Xpath prep the getNextPage response XML
//            XPath xpathGetNextPage = xpathFactory.newXPath();
//            InputSource isGetNextPage = new InputSource(new StringReader(xml));
//            docGetNextPage = builder.parse(isGetNextPage);
//            xpathGetNextPage.setNamespaceContext(new UniversalNamespaceResolver(docGetNextPage));
//            XPathExpression exprGetNextPage = xpathGetNextPage.compile("/getNextPageResponse/return/questionnairePage/sections/columns/questions");
//            
//            
//            //Xpath prep the template - this is the submitPage template
//            String XML_TEMPLATE = readFileForTest("interviewTester\\xml", templateFile);
//            XPath xpathSubmitPage = xpathFactory.newXPath();
//            InputSource isSubmitPage = new InputSource(new StringReader(XML_TEMPLATE));
//            
//            docSubmitPage = builder.parse(isSubmitPage);
//            xpathSubmitPage.setNamespaceContext(new UniversalNamespaceResolver(docSubmitPage));       
//            XPathExpression exprSubmitPage = xpathGetNextPage.compile("/submitPage/submitPageRequest/questions");
//            Node templateQuestionsAndAnswerNode = (Node)exprSubmitPage.evaluate(docSubmitPage, XPathConstants.NODE);
//            
//            //Below is the node onto which we will hang new answers.
//            XPathExpression exprParent = xpathSubmitPage.compile("/submitPage/submitPageRequest");
//            Node copyTo = (Node)exprParent.evaluate(docSubmitPage, XPathConstants.NODE);
//            
//	        NodeList nodesGetNextPage = (NodeList) exprGetNextPage.evaluate(docGetNextPage, XPathConstants.NODESET);
//	        for (int i = 0; i < nodesGetNextPage.getLength(); i++) {
//	        	
//                Node questionNode = nodesGetNextPage.item(i);
//                                	
//            	//Get the answer
//            	XPath xpathAnswer  = xpathFactory.newXPath();
//                Node answer = (Node) xpathAnswer.evaluate(".//answers/value/text()", questionNode, XPathConstants.NODE);
//            	
//                //Get the answer
//            	XPath xpathCode  = xpathFactory.newXPath();
//                Node code = (Node) xpathCode.evaluate(".//code/text()", questionNode, XPathConstants.NODE);
//                
//                //
//                String actualAnswer = "";
//                if (answer != null) {
//                	//System.out.println("Code : " + code.getTextContent() +" -> " + answer.getTextContent());
//                	actualAnswer = answer.getTextContent();
//                } else {
//                	//System.out.println("Code : " + code.getTextContent() +" -> " + "null");
//                }
//                
//                //Check to see if we have an override answer
//                if (mAnswers != null && mAnswers.containsKey(code.getTextContent())) {
//                	actualAnswer = mAnswers.get(code.getTextContent());
//                }
//                
//                //Now build an answer node on the template
//                Node newQuestionsAndAnswerNode = docSubmitPage.importNode(templateQuestionsAndAnswerNode, true);
//                copyTo.appendChild(newQuestionsAndAnswerNode);
//                
//                //Now update the question code and answer
//              //Update the name of the score group
//                for (int j = 0; j < newQuestionsAndAnswerNode.getChildNodes().getLength(); j++) {
//                	if (newQuestionsAndAnswerNode.getChildNodes().item(j).getNodeName().equals("code")) {
//                		newQuestionsAndAnswerNode.getChildNodes().item(j).getFirstChild().setTextContent(code.getTextContent());
//                	}
//                	if (newQuestionsAndAnswerNode.getChildNodes().item(j).getNodeName().equals("answers")) {
//                		
//                		newQuestionsAndAnswerNode.getChildNodes().item(j).getFirstChild().setTextContent(actualAnswer);
//                		
//                	}                	
//                }
//                
//                
//                
//	        }
//	        
//	        //Remove the template answer node
//	        copyTo.removeChild(templateQuestionsAndAnswerNode);
//	        
//	        updatedXml = printXmlDocument(docSubmitPage);
//            
//		} catch (ParserConfigurationException | SAXException | IOException e) {
//            e.printStackTrace();
//		} catch (XPathExpressionException e) {
//            e.printStackTrace();
//        }            
//        return updatedXml ;   
//	}
	
	private String updateXml (String xml, String newInterviewId, String xpathStr) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		String updatedXml="";
		try {
			builder = factory.newDocumentBuilder();
            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();            
            // Create XPath object
            XPath xpath = xpathFactory.newXPath();
            
            
            
            //File file = new File("C:\\temp\\xmlTestFiles\\getNextPage.xml");
            //FileInputStream fis = new FileInputStream(file);

            InputSource is = new InputSource(new StringReader(xml));
            doc = builder.parse(is);
            xpath.setNamespaceContext(new UniversalNamespaceResolver(doc));
		
            XPathExpression expr1 = xpath.compile(xpathStr);
            
            
	        NodeList nodes1 = (NodeList) expr1.evaluate(doc, XPathConstants.NODESET);
	        nodes1.item(0).setNodeValue(newInterviewId);
	        
	        updatedXml = printXmlDocument(doc);
	        System.out.println(updatedXml);
            
		} catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
		} catch (XPathExpressionException e) {
            e.printStackTrace();
        }            
        return updatedXml ;   
	}
	
	public static String printXmlDocument(Document document) {
		StringWriter sw = null;
		try {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		sw = new StringWriter();
		trans.transform(new DOMSource(document), new StreamResult(sw));
		} catch (Exception ex) {
			
		}
		return sw.toString();
	}
	
}
