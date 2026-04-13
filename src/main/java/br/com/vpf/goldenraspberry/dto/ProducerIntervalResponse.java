package br.com.vpf.goldenraspberry.dto;

import java.util.List;

public record ProducerIntervalResponse(
        List<ProducerIntervalItemResponse> min,
        List<ProducerIntervalItemResponse> max
) {
}
