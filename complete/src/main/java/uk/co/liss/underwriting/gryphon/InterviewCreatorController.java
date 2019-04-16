package uk.co.liss.underwriting.gryphon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InterviewCreatorController {
	
	@Value("${server.dns}")
	private String server_dns;
	
	@Value("${adviser.externalToken}")
	private String externalToken;
	
	@Value("${rulebook.underwriting}")
	private String rulebookUw;
	
	@Value("${rulebook.newBusiness}")
	private String rulebookNb;
	
	@Value("${facade.endpoint}")
	private String facadeEndpoint;
	
	@GetMapping("/createInterviewJoint")
    public String createInterviewJoint(
    		@RequestParam(name="uwInterviewId", required=true, defaultValue="") String uwInterviewId, 
    		@RequestParam(name="uwInterviewToken", required=true, defaultValue="") String uwInterviewToken,
    		@RequestParam(name="uwInterviewIdSa", required=true, defaultValue="") String uwInterviewIdSa, 
    		@RequestParam(name="uwInterviewTokenSa", required=true, defaultValue="") String uwInterviewTokenSa,
    		@RequestParam(name="numberOfInterviews", required=false, defaultValue="10") String numberOfInterviews,
    		Model model) {
    	
    	InterviewCreator ic = new InterviewCreator(server_dns, externalToken, facadeEndpoint, rulebookNb, rulebookUw);
    	String interviewId = ic.processInterviewJoint(uwInterviewId, uwInterviewToken, uwInterviewIdSa, uwInterviewTokenSa);
    	
    	//ic.refreshCache();
    	
        model.addAttribute("interviewId", interviewId);
        
        return "greeting";
    }

    @GetMapping("/createInterviews")
    public String createInterviews(@RequestParam(name="uwInterviewId", required=true, defaultValue="") String uwInterviewId, 
    					   @RequestParam(name="uwInterviewToken", required=true, defaultValue="") String uwInterviewToken,
    					   @RequestParam(name="numberOfInterviews", required=false, defaultValue="10") String numberOfInterviews,
    					   Model model) {
    	
    	InterviewCreator ic = new InterviewCreator(server_dns, externalToken, facadeEndpoint, rulebookNb, rulebookUw);
    	String interviewId = ic.processInterview(uwInterviewId, uwInterviewToken);
    	
    	//ic.refreshCache();
    	
        model.addAttribute("interviewId", interviewId);
        
        return "greeting";
    }
    
    @GetMapping("/createInterviewUnderwriting")
    public String createInterviewUnderwriting(Model model) {
    	
    	InterviewCreator ic = new InterviewCreator(server_dns, externalToken, facadeEndpoint, rulebookNb, rulebookUw);
    	String interviewId = ic.createInterviewUnderwriting();
    	
    	//ic.refreshCache();
    	
        model.addAttribute("interviewId", interviewId);
        
        return "createInterviewUnderwriting";
    }

}
