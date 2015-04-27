package com.musichub.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;




@Controller
public class MainController {

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
		
		
		ModelAndView model = new ModelAndView("configure");
//		//model.addObject("page_title", lang.getStringHazardReportingSystem());
//		model.addObject("loginComplete", loginComplete);
//		model.addObject("loginFail", loginFail);
//		model.addObject("logoutComplete", logoutComplete);
//		model.addObject("registerComplete", registerComplete);
//		model.addObject("registerFail", registerFail);
//		model.addObject("submittedUserId", submittedUserId);
//		model.addObject("isUseController", "true");
//		model.addObject("user_type", user_type);
//		
//		
//		
//		model.addObject("active", "index");
				
		return model;
    }
}
