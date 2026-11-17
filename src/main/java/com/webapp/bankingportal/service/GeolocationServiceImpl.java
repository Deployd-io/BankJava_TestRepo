package com.webapp.bankingportal.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.webapp.bankingportal.dto.GeolocationResponse;
import com.webapp.bankingportal.exception.GeolocationException;

import lombok.val;

@Service
public class GeolocationServiceImpl implements GeolocationService {
    @Value("${geo.api.url}")
    private String apiUrl;

    @Value("${geo.api.key}")
    private String apiKey;

    @Override
    @Async
    public CompletableFuture<GeolocationResponse> getGeolocation(String ip) {
        val future = new CompletableFuture<GeolocationResponse>();

        try {
            // Validate IP address
            InetAddress.getByName(ip);

            // Call geolocation API
            val url = String.format("%s/%s/?token=%s", apiUrl, ip, apiKey);
            val response = new RestTemplate()
                    .getForObject(url, GeolocationResponse.class);

            if (response == null) {
                future.completeExceptionally(new GeolocationException(
                        "Failed to get geolocation for IP: " + ip));
            } else {
                future.complete(response);
            }

        } catch (UnknownHostException e) {
            future.completeExceptionally(e);

        } catch (RestClientException e) {
            future.completeExceptionally(e);
        }

        return future;
    }

}
