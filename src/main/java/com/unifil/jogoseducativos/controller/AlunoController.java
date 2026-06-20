package com.unifil.jogoseducativos.controller;

import com.unifil.jogoseducativos.dto.AlunoRequestDTO;
import com.unifil.jogoseducativos.dto.AlunoResponseDTO;
import com.unifil.jogoseducativos.dto.LoginDTO;
import com.unifil.jogoseducativos.service.AlunoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public AlunoResponseDTO registrar(@RequestBody AlunoRequestDTO dto) {
        return alunoService.registrar(dto);
    }

    @PostMapping("/login")
    public AlunoResponseDTO login(@RequestBody LoginDTO dto) {
        return alunoService.login(dto);
    }

    @GetMapping("/{id}")
    public AlunoResponseDTO buscar(@PathVariable Long id) {
        return alunoService.buscarPorId(id);
    }
}
