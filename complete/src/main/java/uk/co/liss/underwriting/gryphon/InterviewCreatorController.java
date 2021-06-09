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
	


    @GetMapping("/createInterviews")
    public String createInterviews(@RequestParam(name="env", required=true) String env, @RequestParam(name="single", required=true) String single, 
    		@RequestParam(name="spt", required=true) String spt ,
    					   Model model) {
    	
    	InterviewCreator ic = new InterviewCreator(server_dns, externalToken, facadeEndpoint, rulebookNb, rulebookUw, env, single, spt);
    	String interviewId = ic.processInterview();
    	
    	//ic.refreshCache();
    	
        model.addAttribute("interviewId", interviewId);
        
        return "greeting";
    }
    
//    @GetMapping("/createInterviewUnderwriting")
//    public String createInterviewUnderwriting(Model model) {
//    	
//    	InterviewCreator ic = new InterviewCreator(server_dns, externalToken, facadeEndpoint, rulebookNb, rulebookUw);
//    	String interviewId = ic.createInterviewUnderwriting();
//    	
//    	//ic.refreshCache();
//    	
//        model.addAttribute("interviewId", interviewId);
//        
//        return "createInterviewUnderwriting";
//    }

}
