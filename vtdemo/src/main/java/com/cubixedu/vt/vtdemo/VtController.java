package com.cubixedu.vt.vtdemo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class VtController {

    private final RestClient restClient; // ezzel hívjuk meg a másik oldalt (remote-service)


    public VtController(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://localhost:8081").build(); //majd a remote-service -be hív
    }

    @GetMapping("/api/callBlocking/{seconds}")
    public String callBlocking(@PathVariable int seconds){ //ezt hívjuk a load tester-el
        ResponseEntity<Void> response = restClient //nem reaktív...
                .get()
                .uri("block/" + seconds)
                .retrieve() //DelayController hívás (remote-service)
                .toBodilessEntity();

        System.out.format("Got response with status %d on %s%n",
                response.getStatusCode().value(),
                Thread.currentThread().getName());

        return Thread.currentThread().getName();
    }
}
