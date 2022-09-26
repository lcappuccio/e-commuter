package org.systemexception.ecommuter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.systemexception.ecommuter.enums.Endpoints;

@Controller
@RequestMapping(value = Endpoints.CONTEXT)
public class ViewController {

	@GetMapping
	public String viewAll() {
		return "index";
	}
}
