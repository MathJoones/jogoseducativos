package com.unifil.jogoseducativos.service;

import com.unifil.jogoseducativos.dto.EstadoJogoDTO;
import com.unifil.jogoseducativos.model.Aluno;
import com.unifil.jogoseducativos.model.PartidaBlackjack;
import com.unifil.jogoseducativos.model.PartidaBlackjack.Status;
import com.unifil.jogoseducativos.model.PartidaBlackjack.Resultado;
import com.unifil.jogoseducativos.repository.AlunoRepository;
import com.unifil.jogoseducativos.repository.PartidaBlackjackRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlackjackService {

    private final PartidaBlackjackRepository partidaRepository;
    private final AlunoRepository alunoRepository;

    public BlackjackService(PartidaBlackjackRepository partidaRepository, AlunoRepository alunoRepository) {
        this.partidaRepository = partidaRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public EstadoJogoDTO iniciarPartida(Long alunoId) {
        Aluno aluno = buscarAluno(alunoId);
        PartidaBlackjack partida = new PartidaBlackjack(aluno);
        partida = partidaRepository.save(partida);
        return toDTO(partida, false);
    }

    @Transactional
    public EstadoJogoDTO apostar(Long partidaId, Integer valorAposta) {
        PartidaBlackjack partida = buscarPartida(partidaId);
        Aluno aluno = partida.getAluno();

        if (partida.getStatus() != Status.AGUARDANDO_APOSTA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Partida não está aguardando aposta");
        }
        if (valorAposta <= 0 || valorAposta > aluno.getSaldo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de aposta inválido");
        }

        // Criar e embaralhar baralho
        List<String> baralho = MotorBlackjack.criarBaralhoEmbaralhado();

        // Distribuir cartas iniciais: jogador, dealer, jogador, dealer
        List<String> maoJogador = new java.util.ArrayList<>();
        List<String> maoDealer = new java.util.ArrayList<>();
        maoJogador.add(MotorBlackjack.comprarCarta(baralho));
        maoDealer.add(MotorBlackjack.comprarCarta(baralho));
        maoJogador.add(MotorBlackjack.comprarCarta(baralho));
        maoDealer.add(MotorBlackjack.comprarCarta(baralho));

        // Descontar aposta do saldo
        aluno.setSaldo(aluno.getSaldo() - valorAposta);
        alunoRepository.save(aluno);

        partida.setAposta(valorAposta);
        partida.setMaoJogador(MotorBlackjack.serializarMao(maoJogador));
        partida.setMaoDealer(MotorBlackjack.serializarMao(maoDealer));
        partida.setBaralhoRestante(MotorBlackjack.serializarBaralho(baralho));
        partida.setStatus(Status.EM_JOGO);

        // Verificar Blackjack natural
        if (MotorBlackjack.isBlackjackNatural(maoJogador)) {
            return finalizarPartida(partida, maoJogador, maoDealer, baralho, "Blackjack! Vitória imediata!");
        }

        partida = partidaRepository.save(partida);
        return toDTO(partida, false);
    }

    @Transactional
    public EstadoJogoDTO pedir(Long partidaId) {
        PartidaBlackjack partida = buscarPartidaEmJogo(partidaId);
        List<String> maoJogador = MotorBlackjack.deserializarMao(partida.getMaoJogador());
        List<String> maoDealer = MotorBlackjack.deserializarMao(partida.getMaoDealer());
        List<String> baralho = MotorBlackjack.deserializarBaralho(partida.getBaralhoRestante());

        maoJogador.add(MotorBlackjack.comprarCarta(baralho));

        if (MotorBlackjack.isBust(maoJogador)) {
            partida.setMaoJogador(MotorBlackjack.serializarMao(maoJogador));
            partida.setBaralhoRestante(MotorBlackjack.serializarBaralho(baralho));
            return finalizarPartida(partida, maoJogador, maoDealer, baralho, "Passou de 21! Derrota.");
        }

        partida.setMaoJogador(MotorBlackjack.serializarMao(maoJogador));
        partida.setBaralhoRestante(MotorBlackjack.serializarBaralho(baralho));
        partida = partidaRepository.save(partida);
        return toDTO(partida, false);
    }

    @Transactional
    public EstadoJogoDTO parar(Long partidaId) {
        PartidaBlackjack partida = buscarPartidaEmJogo(partidaId);
        List<String> maoJogador = MotorBlackjack.deserializarMao(partida.getMaoJogador());
        List<String> maoDealer = MotorBlackjack.deserializarMao(partida.getMaoDealer());
        List<String> baralho = MotorBlackjack.deserializarBaralho(partida.getBaralhoRestante());

        // Dealer joga
        MotorBlackjack.jogarDealer(maoDealer, baralho);

        return finalizarPartida(partida, maoJogador, maoDealer, baralho, null);
    }

    @Transactional
    public EstadoJogoDTO dobrar(Long partidaId) {
        PartidaBlackjack partida = buscarPartidaEmJogo(partidaId);
        Aluno aluno = partida.getAluno();
        List<String> maoJogador = MotorBlackjack.deserializarMao(partida.getMaoJogador());
        List<String> maoDealer = MotorBlackjack.deserializarMao(partida.getMaoDealer());
        List<String> baralho = MotorBlackjack.deserializarBaralho(partida.getBaralhoRestante());

        int apostaExtra = partida.getAposta();
        if (aluno.getSaldo() < apostaExtra) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para dobrar");
        }

        // Dobrar aposta — descontar valor adicional
        aluno.setSaldo(aluno.getSaldo() - apostaExtra);
        alunoRepository.save(aluno);
        partida.setAposta(apostaExtra * 2);

        // Recebe exatamente 1 carta
        maoJogador.add(MotorBlackjack.comprarCarta(baralho));

        // Dealer joga
        MotorBlackjack.jogarDealer(maoDealer, baralho);

        return finalizarPartida(partida, maoJogador, maoDealer, baralho, null);
    }

    public EstadoJogoDTO getEstado(Long partidaId) {
        PartidaBlackjack partida = buscarPartida(partidaId);
        boolean revelar = partida.getStatus() == Status.FINALIZADA;
        return toDTO(partida, revelar);
    }

    // --- Privados ---

    private EstadoJogoDTO finalizarPartida(
            PartidaBlackjack partida,
            List<String> maoJogador,
            List<String> maoDealer,
            List<String> baralho,
            String mensagemOverride) {

        String resultado = MotorBlackjack.calcularResultado(maoJogador, maoDealer);
        Aluno aluno = partida.getAluno();
        int aposta = partida.getAposta();
        int ganho = 0;

        switch (resultado) {
            case "VITORIA" -> ganho = aposta * 2;
            case "EMPATE" -> ganho = aposta; // devolve aposta
            case "DERROTA" -> ganho = 0;
        }

        aluno.setSaldo(aluno.getSaldo() + ganho);
        alunoRepository.save(aluno);

        partida.setMaoJogador(MotorBlackjack.serializarMao(maoJogador));
        partida.setMaoDealer(MotorBlackjack.serializarMao(maoDealer));
        partida.setBaralhoRestante(MotorBlackjack.serializarBaralho(baralho));
        partida.setStatus(Status.FINALIZADA);
        partida.setResultado(Resultado.valueOf(resultado));
        partida.setSaldoFim(aluno.getSaldo());
        partida.setDataFim(LocalDateTime.now());
        partida = partidaRepository.save(partida);

        String mensagem = mensagemOverride != null ? mensagemOverride : switch (resultado) {
            case "VITORIA" -> "Você venceu! +" + aposta + " fichas";
            case "DERROTA" -> "Dealer venceu. -" + aposta + " fichas";
            case "EMPATE" -> "Empate! Aposta devolvida";
            default -> "";
        };

        List<String> dealerPublico = maoDealer;
        int valorDealer = MotorBlackjack.calcularValorMao(maoDealer);

        return new com.unifil.jogoseducativos.dto.EstadoJogoDTO(
            partida.getId(),
            partida.getStatus().name(),
            maoJogador,
            dealerPublico,
            MotorBlackjack.calcularValorMao(maoJogador),
            valorDealer,
            partida.getAposta(),
            aluno.getSaldo(),
            resultado,
            mensagem
        );
    }

    private EstadoJogoDTO toDTO(PartidaBlackjack partida, boolean revelarDealer) {
        List<String> maoJogador = MotorBlackjack.deserializarMao(partida.getMaoJogador());
        List<String> maoDealer = MotorBlackjack.deserializarMao(partida.getMaoDealer());
        List<String> dealerPublico = MotorBlackjack.maoPublicaDealer(maoDealer, revelarDealer);

        int valorJogador = maoJogador.isEmpty() ? 0 : MotorBlackjack.calcularValorMao(maoJogador);
        // Calcula valor apenas das cartas visíveis (exclui "?")
        List<String> cartasVisiveis = dealerPublico.stream()
            .filter(c -> !c.equals("?"))
            .toList();
        int valorDealer = cartasVisiveis.isEmpty() ? 0 : MotorBlackjack.calcularValorMao(cartasVisiveis);

        String resultado = partida.getResultado() != null ? partida.getResultado().name() : null;

        return new com.unifil.jogoseducativos.dto.EstadoJogoDTO(
            partida.getId(),
            partida.getStatus().name(),
            maoJogador,
            dealerPublico,
            valorJogador,
            valorDealer,
            partida.getAposta(),
            partida.getAluno().getSaldo(),
            resultado,
            null
        );
    }

    private PartidaBlackjack buscarPartida(Long id) {
        return partidaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada"));
    }

    private PartidaBlackjack buscarPartidaEmJogo(Long id) {
        PartidaBlackjack partida = buscarPartida(id);
        if (partida.getStatus() != Status.EM_JOGO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Partida não está em andamento");
        }
        return partida;
    }

    private Aluno buscarAluno(Long id) {
        return alunoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
    }
}
