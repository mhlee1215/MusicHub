package com.musichub.web;


import java.net.Inet4Address;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.musichub.core.CapitalizeClient;




@Controller
public class MainController {
	CapitalizeClient client;

	private Logger logger = Logger.getLogger(getClass());
	
	//@Autowired
	//private final UserService userService = null;
	
	@RequestMapping("/index.do")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {

//		String submittedUserId = ServletRequestUtils.getStringParameter(request, "submittedUserId", "");
//		String loginComplete = ServletRequestUtils.getStringParameter(request, "loginComplete", "false");
//		String loginFail = ServletRequestUtils.getStringParameter(request, "loginFail", "false");
//		String logoutComplete = ServletRequestUtils.getStringParameter(request, "logoutComplete", "false");
//		String registerComplete = ServletRequestUtils.getStringParameter(request, "registerComplete", "false");
//		String registerFail = ServletRequestUtils.getStringParameter(request, "registerFail", "false");
//	    String userid = (String)request.getSession().getAttribute("userid");
//	    String user_type = (String) request.getSession().getAttribute("user_type");
//	    
//	   
//	    String language = (String)request.getSession().getAttribute("lang");
		//LanguagePack lang = LanguageServiceImpl.getLangPack(language);
		
		String myIP = Inet4Address.getLocalHost().getHostAddress();
		
		ModelAndView model = new ModelAndView("configure");
		model.addObject("myIP", myIP);
//		
//		
//		
//		model.addObject("active", "index");
				
		return model;
    }
	
	@RequestMapping("/connectToServer.do")
    public @ResponseBody String connectToServer(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String hostIP = ServletRequestUtils.getStringParameter(request, "hostIP", "loalhost");
		System.out.println("connect to Server! :"+hostIP);
		
		if (client == null){
			client = new CapitalizeClient();
		}
		
		client.connectToServer(hostIP);
				
		return "success";
    }
	
	@RequestMapping("/disconnectToServer.do")
    public @ResponseBody String disconnectToServer(HttpServletRequest request, HttpServletResponse response) throws Exception {

//		String hostIP = ServletRequestUtils.getStringParameter(request, "hostIP", "loalhost");
//		System.out.println("connect to Server! :"+hostIP);
//		
//		CapitalizeClient client = new CapitalizeClient();
//		client.connectToServer(hostIP);
		
		if (client != null){
			client.disconnectToServer();
		}
		
		return "success";
    }
}
