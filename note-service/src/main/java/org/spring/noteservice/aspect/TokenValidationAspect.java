package org.spring.noteservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.spring.noteservice.client.UserClient;
import org.spring.noteservice.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TokenValidationAspect {

    @Autowired
    private UserClient userClient;

    @Around("@annotation(org.spring.noteservice.annotations.RequiresAuthorization) && args(token, ..)")
    public Object validateToken(ProceedingJoinPoint joinPoint, String token) throws Throwable {
        if (token == null || !userClient.validateToken(token)) {
            throw new InvalidTokenException("Invalid token");
        }

        return joinPoint.proceed();
    }
}
