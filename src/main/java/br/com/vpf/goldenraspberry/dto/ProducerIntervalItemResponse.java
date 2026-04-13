package br.com.vpf.goldenraspberry.dto;

public record ProducerIntervalItemResponse(
        String producer,
        int interval,
        int previousWin,
        int followingWin
) {
}
