package com.mintos.weather.service;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class IPAddressResolver {

    public String getIp(ServerHttpRequest serverHttpRequest) {
        return serverHttpRequest.getRemoteAddress().getAddress().getHostAddress();
    }

}
