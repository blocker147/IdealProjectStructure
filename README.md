# Priorities:
- Improved logging (See API requests/responses in DEBUG logs)

# Optimization
- Logging
- Monitoring

# Automation
- Docker
- Environments introduction (dev, prod)
- Terraform

# Testing strategies
- Performance testing
- Integration testing
- OWASP Top 10 testing

# Security
- JWT (use database for blacklisting)
- Rate Limiting
- Database security
- External API calls security

# Improvements
- Code Quality (KtLint)
- Code Coverage (JaCoCo)
- Admin UI
- fix dependencies in build.gradle
- API versioning
- Strange error message for GlobalExceptionHandler:
```text
 2024-12-31T12:10:24.159+02:00  WARN 14148 --- [nio-8080-exec-7] .m.m.a.ExceptionHandlerExceptionResolver : Failure in @ExceptionHandler com.example.spring.exceptions.GlobalExceptionHandler#handleInternalServerErrorException(Exception)

org.springframework.http.converter.HttpMessageNotWritableException: No converter for [class com.example.spring.exceptions.InternalServerErrorResponse] with preset Content-Type 'text/plain;charset=UTF-8'
	at org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor.writeWithMessageConverters(AbstractMessageConverterMethodProcessor.java:319) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor.handleReturnValue(HttpEntityMethodProcessor.java:245) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite.handleReturnValue(HandlerMethodReturnValueHandlerComposite.java:78) ~[spring-web-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:136) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver.doResolveHandlerMethodException(ExceptionHandlerExceptionResolver.java:413) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver.doResolveException(AbstractHandlerMethodExceptionResolver.java:74) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver.resolveException(AbstractHandlerExceptionResolver.java:141) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.handler.HandlerExceptionResolverComposite.resolveException(HandlerExceptionResolverComposite.java:80) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.DispatcherServlet.processHandlerException(DispatcherServlet.java:1341) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.DispatcherServlet.processDispatchResult(DispatcherServlet.java:1152) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1098) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:974) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1011) ~[spring-webmvc-6.0.12.jar:6.0.12]
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.0.12.jar:6.0.12]
```
