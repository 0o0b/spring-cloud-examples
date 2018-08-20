    注：以下所述的环境均为原项目升级至spring boot 2.0.4.RELEASE后

Hystrix Dashboard
=================
须添加servlet映射：
>'/hystrix.stream' => com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet

（见`com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet`的注释）

其中一种实现方式见spring-cloud-consumer-hystrix-dashboard：
```java
@WebServlet(name = "HystrixMetricsStreamServlet", urlPatterns = "/hystrix.stream", loadOnStartup = 1)
public class HystrixServlet extends HystrixMetricsStreamServlet {}
```
并在Application类添加`@ServletComponentScan`自动扫描。

Turbine
=================
Turbine把多个`hystrix.stream`的内容聚合为一个数据源供Dashboard展示，因此监控的服务须启用Hystrix Dashboard，即`hystrix-dashboard-turbine`监控的服务应为`spring-cloud-consumer-hystrix-dashboard`。
若提供Turbine服务的项目中的turbine配置为
```yaml
turbine:
  appConfig: spring-cloud-consumer-hystrix-dashboard
  aggregator:
    clusterConfig: default
  clusterNameExpression: new String("default")
  instanceUrlSuffix: hystrix.stream
```
则该项目中，Turbine获取数据的方式是请求服务名（受监控服务配置：`spring.application.name`）为`spring-cloud-consumer-hystrix-dashboard`的服务暴露的`/hystrix.stream`接口（Turbine服务配置：`turbine.instanceUrlSuffix`）。

若Turbine服务未配置`turbine.instanceUrlSuffix`，则获取数据的默认接口为`/actuator/hystrix.stream`，须在受监控服务提供对应接口，如：
```java
@WebServlet(name = "HystrixMetricsStreamServlet", urlPatterns = { "/hystrix.stream", "/actuator/hystrix.stream" }, loadOnStartup = 1)
public class HystrixServlet extends HystrixMetricsStreamServlet {}
```
