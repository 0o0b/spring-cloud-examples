package com.neo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@Autowired
	private ServerProperties serverProperties;

	@RequestMapping("/hello")
	public String index(@RequestParam String name) {
		return "[" + serverProperties.getPort() + "]Hello " + name + ".";
	}
}