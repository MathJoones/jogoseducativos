package com.unifil.jogoseducativos.dto;

import java.util.List;

public record EstadoJogoDTO(
    Long partidaId,
    String status,
    List<String> maoJogador,
    List<String> maoDealer,
    int valorJogador,
    int valorDealer,
    Integer aposta,
    Integer saldoAtual,
    String resultado,
    String mensagem
) {}
