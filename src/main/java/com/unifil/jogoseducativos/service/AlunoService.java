package com.unifil.jogoseducativos.service;

import com.unifil.jogoseducativos.dto.AlunoRequestDTO;
import com.unifil.jogoseducativos.dto.AlunoResponseDTO;
import com.unifil.jogoseducativos.dto.LoginDTO;
import com.unifil.jogoseducativos.model.Aluno;
import com.unifil.jogoseducativos.repository.AlunoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public AlunoResponseDTO registrar(AlunoRequestDTO dto) {
        if (alunoRepository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }
        Aluno aluno = new Aluno(dto.nome(), dto.email(), dto.senha(), null);
        aluno = alunoRepository.save(aluno);
        return toDTO(aluno);
    }

    public AlunoResponseDTO login(LoginDTO dto) {
        Aluno aluno = alunoRepository.findByEmail(dto.email())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email não encontrado"));
        if (!aluno.getSenha().equals(dto.senha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta");
        }
        return toDTO(aluno);
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        return toDTO(aluno);
    }

    private AlunoResponseDTO toDTO(Aluno aluno) {
        return new AlunoResponseDTO(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getSaldo());
    }
}
