package com.neo.hystrix;

import javax.servlet.annotation.WebServlet;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

@WebServlet(name = "HystrixMetricsStreamServlet", urlPatterns = { "/hystrix.stream", "/actuator/hystrix.stream" }, loadOnStartup = 1)
public class HystrixServlet extends HystrixMetricsStreamServlet {

	private static final long serialVersionUID = -4001299453161835127L;
}