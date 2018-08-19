package com.neo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;

import com.neo.env.RandomMapPropertySource;

@SpringBootApplication
@EnableDiscoveryClient
public class ProducerApplication {

	public static void main(String[] args) {
		while (true)
			try {
				SpringApplication app = new SpringApplication(ProducerApplication.class);
				app.addListeners(new ApplicationListener<ApplicationEnvironmentPreparedEvent>() {
					@Override
					public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
						RandomMapPropertySource.addToEnvironment(event.getEnvironment());
						// event.getEnvironment().getPropertySources().addLast(new RandomMapPropertySource());
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

	// @Bean
	// public TomcatServletWebServerFactory servletContainer() {
	// return new TomcatServletWebServerFactory(9950);
	// }
}