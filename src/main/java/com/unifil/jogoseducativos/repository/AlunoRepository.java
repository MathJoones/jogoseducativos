package com.unifil.jogoseducativos.repository;

import com.unifil.jogoseducativos.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByEmail(String email);
    boolean existsByEmail(String email);
}
