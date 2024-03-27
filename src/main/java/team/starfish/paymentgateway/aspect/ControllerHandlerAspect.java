package team.starfish.paymentgateway.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import team.starfish.paymentgateway.error.*;
import team.starfish.paymentgateway.utils.HttpUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class ControllerHandlerAspect {

    @Around("execution(* team.starfish.paymentgateway.controller.*.*(..))")
    public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestMethod = request.getMethod();
        String requestURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        String methodName = joinPoint.getSignature().toShortString();
        String args = Arrays.toString(joinPoint.getArgs());

        try {
            Object response = joinPoint.proceed();
            log.info("[{}] {}?{} - {}({}) - response: {}",
                    requestMethod, requestURL, queryString, methodName, args, response);
            return response;
        } catch (BadRequestException ex) {
            Object response = HttpUtils.respond(HttpStatus.BAD_REQUEST, false, ex.getMessage());
            log.warn("[{}] {}?{} - {}({}) - response: {}",
                    requestMethod, requestURL, queryString, methodName, args, response, ex);
            return response;
        } catch (BadDataException ex) {
            Object response = HttpUtils.respond(HttpStatus.NOT_ACCEPTABLE, false, ex.getMessage());
            log.warn("[{}] {}?{} - {}({}) - response: {}",
                    requestMethod, requestURL, queryString, methodName, args, response, ex);
            return response;
        } catch (DataNotFoundException ex) {
            Object response = HttpUtils.respond(HttpStatus.NOT_FOUND, false, ex.getMessage());
            log.warn("[{}] {}?{} - {}({}) - response: {}",
                    requestMethod, requestURL, queryString, methodName, args, response, ex);
            return response;
        } catch (DuplicatedDataException ex) {
            Object response = HttpUtils.respond(HttpStatus.CONFLICT, false, ex.getMessage());
            log.warn("[{}] {}?{} - {}({}) - response: {}",
                    requestMethod, requestURL, queryString, methodName, args, response, ex);
            return response;
        } catch (ExternalApiException | IOException ex) {
            Object response = HttpUtils.respond(HttpStatus.BAD_GATEWAY, false, ex.getMessage());
            log.warn("[{}] {}?{} - {}({}) - response: {}",
                    requestMethod, requestURL, queryString, methodName, args, response, ex);
            return response;
        } catch (Exception ex) {
            Object response = HttpUtils.respond(HttpStatus.INTERNAL_SERVER_ERROR, false, ex.getMessage());
            log.error("[{}] {}?{} - {}({}) - response: {}",
                    requestMethod, requestURL, queryString, methodName, args, response, ex);
            return response;
        }
    }

}