package com.unifil.jogoseducativos.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MotorBlackjack {

    private static final String[] NAIPES = {"C", "D", "H", "S"};
    private static final String[] VALORES = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    public static List<String> criarBaralhoEmbaralhado() {
        List<String> baralho = new ArrayList<>();
        for (String naipe : NAIPES) {
            for (String valor : VALORES) {
                baralho.add(valor + naipe);
            }
        }
        Collections.shuffle(baralho);
        return baralho;
    }

    public static String serializarBaralho(List<String> baralho) {
        return String.join(",", baralho);
    }

    public static List<String> deserializarBaralho(String csv) {
        if (csv == null || csv.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(csv.split(",")));
    }

    public static String serializarMao(List<String> mao) {
        return String.join(",", mao);
    }

    public static List<String> deserializarMao(String csv) {
        if (csv == null || csv.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(csv.split(",")));
    }

    public static String comprarCarta(List<String> baralho) {
        return baralho.remove(0);
    }

    public static int calcularValorMao(List<String> mao) {
        int total = 0;
        int ases = 0;

        for (String carta : mao) {
            String valor = carta.replaceAll("[CDHS]", "");
            switch (valor) {
                case "A" -> { total += 11; ases++; }
                case "J", "Q", "K", "10" -> total += 10;
                default -> total += Integer.parseInt(valor);
            }
        }

        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }

        return total;
    }

    public static boolean isBust(List<String> mao) {
        return calcularValorMao(mao) > 21;
    }

    public static boolean isBlackjackNatural(List<String> mao) {
        return mao.size() == 2 && calcularValorMao(mao) == 21;
    }

    /** Dealer joga automaticamente: compra até valor >= 17 */
    public static void jogarDealer(List<String> maoDealer, List<String> baralho) {
        while (calcularValorMao(maoDealer) < 17) {
            maoDealer.add(comprarCarta(baralho));
        }
    }

    /** Calcula resultado: VITORIA, DERROTA, EMPATE */
    public static String calcularResultado(List<String> maoJogador, List<String> maoDealer) {
        int valorJogador = calcularValorMao(maoJogador);
        int valorDealer = calcularValorMao(maoDealer);

        if (valorJogador > 21) return "DERROTA";
        if (valorDealer > 21) return "VITORIA";
        if (valorJogador > valorDealer) return "VITORIA";
        if (valorJogador < valorDealer) return "DERROTA";
        return "EMPATE";
    }

    /** Mão do dealer para exibição: esconde segunda carta enquanto em jogo */
    public static List<String> maoPublicaDealer(List<String> maoDealer, boolean revelar) {
        if (revelar || maoDealer.size() <= 1) return maoDealer;
        List<String> publica = new ArrayList<>();
        publica.add(maoDealer.get(0));
        publica.add("?");
        return publica;
    }
}
