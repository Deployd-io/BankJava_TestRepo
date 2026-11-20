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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GeolocationServiceImpl implements GeolocationService {
	private static final Logger log = LoggerFactory.getLogger(GeolocationServiceImpl.class);
    @Value("${geo.api.url}")
    private String apiUrl;

    @Value("${geo.api.key}")
    private String apiKey;

    @Override
    @Async
    public CompletableFuture<GeolocationResponse> getGeolocation(String ip) {
		log.error(">>> Entering getGeolocation(ip={})", ip);
		long start = System.currentTimeMillis();
        val future = new CompletableFuture<GeolocationResponse>();

		log.error("getGeolocation(ip={}): future → {}", ip, future);
        try {
            // Validate IP address
            InetAddress.getByName(ip);

            // Call geolocation API
            val url = String.format("%s/%s/?token=%s", apiUrl, ip, apiKey);
			log.error("getGeolocation(ip={}): url → {}", ip, url);
            val response = new RestTemplate()
                    .getForObject(url, GeolocationResponse.class);

			log.error("getGeolocation(ip)={}: external service call {} took {} ms", ip, url, (System.currentTimeMillis() - start));
            if (response == null) {
                future.completeExceptionally(new GeolocationException(
                        "Failed to get geolocation for IP: " + ip));
            } else {
                future.complete(response);
            }

        } catch (UnknownHostException e) {
			log.error("Exception in getGeolocation(ip={}): {}", ip, e.getMessage(), e);
            future.completeExceptionally(e);

        } catch (RestClientException e) {
            future.completeExceptionally(e);
        }

		log.warn("<<< Exiting getGeolocation(ip={})", ip);
        return future;
    }

}
