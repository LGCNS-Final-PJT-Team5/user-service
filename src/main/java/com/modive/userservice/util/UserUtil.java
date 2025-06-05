package com.modive.userservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
public class UserUtil {
    public String getUserId() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        System.out.println(request.getHeader("X-MEMBER-ID"));
        System.out.println(request.getHeader("X-USER-ID"));
        return request.getHeader("X-USER-ID");
    }

    public String getUserRole() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader("X-USER-ROLE");
    }
}
