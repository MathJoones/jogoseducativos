package com.unifil.jogoseducativos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partidas_blackjack")
public class PartidaBlackjack {

    public enum Status {
        AGUARDANDO_APOSTA, EM_JOGO, FINALIZADA
    }

    public enum Resultado {
        VITORIA, DERROTA, EMPATE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.AGUARDANDO_APOSTA;

    private Integer aposta;

    @Column(name = "mao_jogador", columnDefinition = "TEXT")
    private String maoJogador;

    @Column(name = "mao_dealer", columnDefinition = "TEXT")
    private String maoDealer;

    @Column(name = "baralho_restante", columnDefinition = "TEXT")
    private String baralhoRestante;

    @Column(name = "saldo_inicio")
    private Integer saldoInicio;

    @Column(name = "saldo_fim")
    private Integer saldoFim;

    @Enumerated(EnumType.STRING)
    private Resultado resultado;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio = LocalDateTime.now();

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    public PartidaBlackjack() {}

    public PartidaBlackjack(Aluno aluno) {
        this.aluno = aluno;
        this.saldoInicio = aluno.getSaldo();
        this.dataInicio = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Integer getAposta() { return aposta; }
    public void setAposta(Integer aposta) { this.aposta = aposta; }

    public String getMaoJogador() { return maoJogador; }
    public void setMaoJogador(String maoJogador) { this.maoJogador = maoJogador; }

    public String getMaoDealer() { return maoDealer; }
    public void setMaoDealer(String maoDealer) { this.maoDealer = maoDealer; }

    public String getBaralhoRestante() { return baralhoRestante; }
    public void setBaralhoRestante(String baralhoRestante) { this.baralhoRestante = baralhoRestante; }

    public Integer getSaldoInicio() { return saldoInicio; }
    public void setSaldoInicio(Integer saldoInicio) { this.saldoInicio = saldoInicio; }

    public Integer getSaldoFim() { return saldoFim; }
    public void setSaldoFim(Integer saldoFim) { this.saldoFim = saldoFim; }

    public Resultado getResultado() { return resultado; }
    public void setResultado(Resultado resultado) { this.resultado = resultado; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
}
