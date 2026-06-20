package com.unifil.jogoseducativos.controller;

import com.unifil.jogoseducativos.dto.PontuacaoDTO;
import com.unifil.jogoseducativos.model.Pontuacao;
import com.unifil.jogoseducativos.repository.PontuacaoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pontuacoes")
public class PontuacaoController {

    private final PontuacaoRepository pontuacaoRepository;

    public PontuacaoController(PontuacaoRepository pontuacaoRepository) {
        this.pontuacaoRepository = pontuacaoRepository;
    }

    @GetMapping("/ranking")
    public List<PontuacaoDTO> ranking() {
        return pontuacaoRepository.findAll().stream()
            .sorted((a, b) -> b.getPontos() - a.getPontos())
            .limit(10)
            .map(p -> new PontuacaoDTO(
                p.getId(),
                p.getAluno().getNome(),
                p.getPontos(),
                p.getDataRegistro()
            ))
            .toList();
    }

    @GetMapping("/aluno/{alunoId}")
    public List<PontuacaoDTO> porAluno(@PathVariable Long alunoId) {
        return pontuacaoRepository.findByAlunoIdOrderByPontosDesc(alunoId).stream()
            .map(p -> new PontuacaoDTO(
                p.getId(),
                p.getAluno().getNome(),
                p.getPontos(),
                p.getDataRegistro()
            ))
            .toList();
    }
}
