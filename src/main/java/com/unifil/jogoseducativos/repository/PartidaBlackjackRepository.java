package com.unifil.jogoseducativos.repository;

import com.unifil.jogoseducativos.model.PartidaBlackjack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaBlackjackRepository extends JpaRepository<PartidaBlackjack, Long> {
    List<PartidaBlackjack> findByAlunoIdOrderByDataInicioDesc(Long alunoId);
}
