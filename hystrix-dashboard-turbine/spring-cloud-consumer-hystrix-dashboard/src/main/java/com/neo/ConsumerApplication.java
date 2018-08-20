package com.neo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationListener;

import com.neo.env.RandomMapPropertySource;

@SpringBootApplication
@ServletComponentScan
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrixDashboard
@EnableCircuitBreaker
public class ConsumerApplication {

	public static void main(String[] args) {
		while (true)
			try {
				SpringApplication app = new SpringApplication(ConsumerApplication.class);
				app.addListeners(new ApplicationListener<ApplicationEnvironmentPreparedEvent>() {
					@Override
					public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
						RandomMapPropertySource.addToEnvironment(event.getEnvironment());
					}
				});
				app.run(args);
				break;
			} catch (ConnectorStartFailedException e) {
				// 端口被占用，则根据application.yml中的server.port配置重新生成随机端口
				System.out.println("\n************************************************************\n" + e.getMessage()
						+ "\n************************************************************\n");
			}
	}
}