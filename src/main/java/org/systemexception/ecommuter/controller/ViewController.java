package org.systemexception.ecommuter.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.systemexception.ecommuter.enums.Endpoints;

@Controller
@RequestMapping(value = Endpoints.CONTEXT)
public class ViewController {

	@RequestMapping(method = RequestMethod.GET)
	public String viewAll() {
		return "index";
	}
}
