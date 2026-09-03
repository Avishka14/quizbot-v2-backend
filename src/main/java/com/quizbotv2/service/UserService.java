package com.quizbotv2.service;


import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final OpenRouterService openRouterService;

    public UserService(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }


}
