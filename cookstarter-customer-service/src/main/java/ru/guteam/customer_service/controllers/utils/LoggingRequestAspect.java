package ru.guteam.customer_service.controllers.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.guteam.customer_service.entities.utils.SystemCustomer;
import ru.guteam.customer_service.entities.utils.SystemRestaurant;

@Slf4j
@Aspect
@Configuration
public class LoggingRequestAspect {

    // логирование попытки аутентификации пользователей
    @Before("execution(* ru.guteam.customer_service.controllers.AuthController.* (..))")
    public void logAuthRequest(JoinPoint joinPoint) {
        UsernameAndPasswordRequest request = (UsernameAndPasswordRequest) joinPoint.getArgs()[0];
        log.info("Запрос на аутентификацию пользователя с логином: " + request.getUsername() +
                " и паролем: " + request.getPassword());
    }

    // логирование результатов аутентификации пользователей
    @AfterReturning(pointcut = "execution(* ru.guteam.customer_service.controllers.AuthController.* (..))", returning = "result")
    public void logAuthResponse(JoinPoint joinPoint, Object result) {
        ResponseEntity response = (ResponseEntity) result;
        UsernameAndPasswordRequest authRequest = (UsernameAndPasswordRequest) joinPoint.getArgs()[0];
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            log.info("Пользователь с логином: " + authRequest.getUsername() +
                    " и паролем: " + authRequest.getPassword() + " не обнаружен");
        }
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            log.info("Для пользователя с логином: " + authRequest.getUsername() +
                    " и паролем: " + authRequest.getPassword() + " сгенерирован токен: " + response.getBody().toString());
        }
    }

    // логирование попытки регистрации клиентов
    @Before("execution(* ru.guteam.customer_service.controllers.RegistrationController.customerRegistration(..))")
    public void logCustomerRegRequest(JoinPoint joinPoint) {
        SystemCustomer customer = (SystemCustomer) joinPoint.getArgs()[0];
        log.info("Запрос на регистрацию клиента с логином: " + customer.getUsername());
    }

    // логирование попытки регистрации ресторанов
    @Before("execution(* ru.guteam.customer_service.controllers.RegistrationController.restaurantRegistration(..))")
    public void logRestaurantRegRequest(JoinPoint joinPoint) {
        SystemRestaurant restaurant = (SystemRestaurant) joinPoint.getArgs()[0];
        log.info("Запрос на регистрацию ресорана с логином: " + restaurant.getUsername());
    }

    // логирование результатов регистрации ресторанов
    @AfterReturning(pointcut = "execution(* ru.guteam.customer_service.controllers.RegistrationController.customerRegistration (..))", returning = "result")
    public void logCustomerRegResponse(JoinPoint joinPoint, Object result) {
        ResponseEntity response = (ResponseEntity) result;
        SystemCustomer customer = (SystemCustomer) joinPoint.getArgs()[0];
        if (response.getStatusCode().equals(HttpStatus.CONFLICT)) {
            log.info("Невозможно зарегистрировать. Клиент с логином: " + customer.getUsername() + " уже сущесвует");
        }
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            log.info("Пользователь с логином: " + customer.getUsername() +
                    " и паролем: " + customer.getPass1() + " зарегистрирован");
        }
    }

    // логирование результатов регистрации клиентов
    @AfterReturning(pointcut = "execution(* ru.guteam.customer_service.controllers.RegistrationController.customerRegistration (..))", returning = "result")
    public void logRestaurantRegResponse(JoinPoint joinPoint, Object result) {
        ResponseEntity response = (ResponseEntity) result;
        SystemRestaurant restaurant = (SystemRestaurant) joinPoint.getArgs()[0];
        if (response.getStatusCode().equals(HttpStatus.CONFLICT)) {
            log.info("Невозможно зарегистрировать. Клиент с логином: " + restaurant.getUsername() + " уже сущесвует");
        }
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            log.info("Пользователь с логином: " + restaurant.getUsername() +
                    " и паролем: " + restaurant.getPassword() + " зарегистрирован");
        }
    }
}
