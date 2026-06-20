package com.unifil.jogoseducativos.controller;

import com.unifil.jogoseducativos.dto.ApostaDTO;
import com.unifil.jogoseducativos.dto.EstadoJogoDTO;
import com.unifil.jogoseducativos.dto.IniciarPartidaDTO;
import com.unifil.jogoseducativos.service.BlackjackService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blackjack")
public class BlackjackController {

    private final BlackjackService blackjackService;

    public BlackjackController(BlackjackService blackjackService) {
        this.blackjackService = blackjackService;
    }

    @PostMapping("/iniciar")
    public EstadoJogoDTO iniciar(@RequestBody IniciarPartidaDTO dto) {
        return blackjackService.iniciarPartida(dto.alunoId());
    }

    @PostMapping("/{partidaId}/apostar")
    public EstadoJogoDTO apostar(@PathVariable Long partidaId, @RequestBody ApostaDTO dto) {
        return blackjackService.apostar(partidaId, dto.valor());
    }

    @PostMapping("/{partidaId}/pedir")
    public EstadoJogoDTO pedir(@PathVariable Long partidaId) {
        return blackjackService.pedir(partidaId);
    }

    @PostMapping("/{partidaId}/parar")
    public EstadoJogoDTO parar(@PathVariable Long partidaId) {
        return blackjackService.parar(partidaId);
    }

    @PostMapping("/{partidaId}/dobrar")
    public EstadoJogoDTO dobrar(@PathVariable Long partidaId) {
        return blackjackService.dobrar(partidaId);
    }

    @GetMapping("/{partidaId}/estado")
    public EstadoJogoDTO estado(@PathVariable Long partidaId) {
        return blackjackService.getEstado(partidaId);
    }
}
