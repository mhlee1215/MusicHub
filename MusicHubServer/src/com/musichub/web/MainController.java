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
import com.musichub.core.CapitalizeServer;
import com.musichub.core.WifiDetector;




@Controller
public class MainController {
	CapitalizeClient client;
	CapitalizeServer server;

	private Logger logger = Logger.getLogger(getClass());
	
	//@Autowired
	//private final UserService userService = null;
	
//	@RequestMapping("/index.do")
//    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
//
////		String submittedUserId = ServletRequestUtils.getStringParameter(request, "submittedUserId", "");
////		String loginComplete = ServletRequestUtils.getStringParameter(request, "loginComplete", "false");
////		String loginFail = ServletRequestUtils.getStringParameter(request, "loginFail", "false");
////		String logoutComplete = ServletRequestUtils.getStringParameter(request, "logoutComplete", "false");
////		String registerComplete = ServletRequestUtils.getStringParameter(request, "registerComplete", "false");
////		String registerFail = ServletRequestUtils.getStringParameter(request, "registerFail", "false");
////	    String userid = (String)request.getSession().getAttribute("userid");
////	    String user_type = (String) request.getSession().getAttribute("user_type");
////	    
////	   
////	    String language = (String)request.getSession().getAttribute("lang");
//		//LanguagePack lang = LanguageServiceImpl.getLangPack(language);
//		
//		String myIP = Inet4Address.getLocalHost().getHostAddress();
//		
//		ModelAndView model = new ModelAndView("configure");
//		model.addObject("myIP", myIP);
////		
////		
////		
////		model.addObject("active", "index");
//				
//		return model;
//    }
	
	
	
	@RequestMapping("/clientPage.do")
    public ModelAndView clientPage(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String myIP = Inet4Address.getLocalHost().getHostAddress();
		
		int wifiSignal = WifiDetector.getSignal();
		String wifi_signal = "";
		if(wifiSignal == WifiDetector.SIGNAL_INIT)
			wifi_signal = "NONE-WIFI";
		else
			wifi_signal = Integer.toString(wifiSignal);
		
		ModelAndView model = new ModelAndView("clientPage");
		model.addObject("myIP", myIP);
		model.addObject("wifi_signal", wifi_signal);
		
				
		return model;
    }
	
	@RequestMapping("/serverPage.do")
    public ModelAndView serverPage(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String myIP = Inet4Address.getLocalHost().getHostAddress();
		
		ModelAndView model = new ModelAndView("serverPage");
		model.addObject("myIP", myIP);
				
		return model;
    }
	
	@RequestMapping("/index.do")
    public ModelAndView indexPage(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView("index");

				
		return model;
    }
	
	
	@RequestMapping("/startServer.do")
    public @ResponseBody String startServer(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String hostIP = ServletRequestUtils.getStringParameter(request, "hostIP", "loalhost");
		System.out.println("connect to Server! :"+hostIP);
		
		if (server == null){
			server = new CapitalizeServer();
		}
		
		server.startServer();
				
		return "success";
    }
	
	@RequestMapping("/stopServer.do")
    public @ResponseBody String stopServer(HttpServletRequest request, HttpServletResponse response) throws Exception {

//		String hostIP = ServletRequestUtils.getStringParameter(request, "hostIP", "loalhost");
//		System.out.println("connect to Server! :"+hostIP);
//		
//		CapitalizeClient client = new CapitalizeClient();
//		client.connectToServer(hostIP);
		
		if (server != null){
			server.stopServer();
		}
		
		return "success";
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
