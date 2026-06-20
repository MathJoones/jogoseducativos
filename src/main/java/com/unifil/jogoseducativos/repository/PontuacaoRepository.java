package com.unifil.jogoseducativos.repository;

import com.unifil.jogoseducativos.model.Pontuacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PontuacaoRepository extends JpaRepository<Pontuacao, Long> {
    List<Pontuacao> findByAlunoIdOrderByPontosDesc(Long alunoId);
    List<Pontuacao> findTop10ByJogoIdOrderByPontosDesc(Long jogoId);
}
