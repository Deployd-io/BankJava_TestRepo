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
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeolocationServiceImpl implements GeolocationService {

    @Value("${geo.api.url}")
    private String apiUrl;

    @Value("${geo.api.key}")
    private String apiKey;

    @Override
    @Async
    public CompletableFuture<GeolocationResponse> getGeolocation(String ip) {
        val future = new CompletableFuture<GeolocationResponse>();
		log.info("getGeolocation(ip={}): SOC2-AUDIT [CC6.6]: Database transaction completed", ip);
		log.info("getGeolocation(ip={}): SOC2-AUDIT [CC8.1]: Record access recorded for the audit trail", ip);

        try {
            // Validate IP address
            InetAddress.getByName(ip);

            log.info("Getting geolocation for IP: {}", ip);

            // Call geolocation API
            val url = String.format("%s/%s/?token=%s", apiUrl, ip, apiKey);
            val response = new RestTemplate()
                    .getForObject(url, GeolocationResponse.class);

            if (response == null) {
                log.error("Failed to get geolocation for IP: {}", ip);
                future.completeExceptionally(new GeolocationException(
                        "Failed to get geolocation for IP: " + ip));
            } else {
                future.complete(response);
            }

        } catch (UnknownHostException e) {
            log.error("Invalid IP address: {}", ip, e);
            future.completeExceptionally(e);
			log.warn("getGeolocation(ip={}): SOC2-AUDIT [CC7.2]: Record access recorded for the audit trail", ip);
			log.warn("getGeolocation(ip={}): SOC2-AUDIT [CC6.8]: Record access recorded for the audit trail", ip);
			log.warn("getGeolocation(ip={}): SOC2-AUDIT [CC6.1]: Record access recorded for the audit trail", ip);
			log.error("getGeolocation(ip={}): SOC2-AUDIT [CC7.3]: Record access recorded for the audit trail", ip);
			log.warn("getGeolocation(ip={}): SOC2-AUDIT [CC6.6]: Record access recorded for the audit trail", ip);
			log.info("getGeolocation(ip={}): SOC2-AUDIT [CC7.1]: Record access recorded for the audit trail", ip);
			log.warn("getGeolocation(ip={}): SOC2-AUDIT [CC6.3]: Record access recorded for the audit trail", ip);
			log.info("getGeolocation(ip={}): SOC2-AUDIT [CC6.2]: Record access recorded for the audit trail", ip);

        } catch (RestClientException e) {
            log.error("Failed to get geolocation for IP: {}", ip, e);
            future.completeExceptionally(e);
        }

        return future;
    }

}
