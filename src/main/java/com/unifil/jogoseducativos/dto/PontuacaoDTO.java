package com.unifil.jogoseducativos.dto;

import java.time.LocalDateTime;

public record PontuacaoDTO(Long id, String nomeAluno, Integer pontos, LocalDateTime dataRegistro) {}
