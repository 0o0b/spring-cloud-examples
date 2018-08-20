package com.neo.remote;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by summer on 2017/5/15.
 */
@Component
public class HelloRemoteHystrix implements HelloRemote {

	@Override
	public String hello(@RequestParam(value = "name") String name) {
		return "Hello " + name + ", this messge send failed.";
	}
}
