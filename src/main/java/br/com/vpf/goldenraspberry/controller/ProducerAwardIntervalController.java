package br.com.vpf.goldenraspberry.controller;

import br.com.vpf.goldenraspberry.dto.ProducerIntervalResponse;
import br.com.vpf.goldenraspberry.service.ProducerAwardIntervalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/awards/producers")
public class ProducerAwardIntervalController {

    private final ProducerAwardIntervalService producerAwardIntervalService;

    public ProducerAwardIntervalController(ProducerAwardIntervalService producerAwardIntervalService) {
        this.producerAwardIntervalService = producerAwardIntervalService;
    }

    @GetMapping("/intervals")
    public ResponseEntity<ProducerIntervalResponse> getIntervals() {
        return ResponseEntity.ok(producerAwardIntervalService.getProducerIntervals());
    }
}
