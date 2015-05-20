package com.musichub.web;


import java.io.IOException;
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
	RegionDetectorDaemon detectorDaemon;
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
		System.out.println("start Server! :"+hostIP);
		
		if (server == null){
			server = new CapitalizeServer();
		}
		//System.out.println("start?");
		server.startServer();
		//System.out.println("start!");
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
		int threshold = ServletRequestUtils.getIntParameter(request, "threshold", -45);
		System.out.println("connect to Server!!! :"+hostIP);
		
		//if (client == null){
			
		//if (detectorDaemon == null){
			detectorDaemon = new RegionDetectorDaemon(hostIP, threshold);
			detectorDaemon.start();
		//}
			
		//}else{
		//	client.resumePlay();
		//}
		return "success";
    }
	
	@RequestMapping("/getWifiSignal.do")
    public @ResponseBody String getWifiSignal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int wifiSignal = WifiDetector.getSignal();
		String wifi_signal = "";
		if(wifiSignal == WifiDetector.SIGNAL_INIT)
			wifi_signal = "NONE-WIFI";
		else
			wifi_signal = Integer.toString(wifiSignal);
		
		return wifi_signal;
    }
	
	
	
	@RequestMapping("/disconnectToServer.do")
    public @ResponseBody String disconnectToServer(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		detectorDaemon.stopWorking();
		detectorDaemon.stop();
		
		return "success";
    }
	
	public class RegionDetectorDaemon extends Thread {
		CapitalizeClient client;
		String hostIP;
		int threshold;
		
		public RegionDetectorDaemon(String hostIP, int threshold){
			this.hostIP = hostIP;
			this.threshold = threshold;
		}
		
		public void stopWorking(){
			if(client != null)
				client.disconnectToServer();
		}
		
		@Override
		public void run() {
			//System.out.println("Detector run!!");
			// TODO Auto-generated method stub
			boolean isPlayed = false;
			while(true){
				int signal = WifiDetector.getSignal();
				
				System.out.println("Current signal :"+signal+", threshold:"+threshold);
				
				if(signal > threshold || signal == WifiDetector.SIGNAL_INIT){
					if(!isPlayed){
						try {
							if(client == null){
								client = new CapitalizeClient();
								client.connectToServer(hostIP);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						isPlayed = true;
					}else{
						//Do nothing.
					}
				}else{
					if (client != null){
						client.disconnectToServer();
						client = null;
					}
					isPlayed = false;
				}
				
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
