package Desafio;

import java.util.Random;
import java.util.Scanner;

public class GuessingGame {

    static Scanner sc = new Scanner(System.in);
    static Random rnd = new Random();

    // posição 0 = Fácil, 1 = Médio, 2 = Difícil
    static String[] levelName = { "Fácil", "Médio", "Difícil" };
    static int[] maxNumber = { 50, 100, 200 };
    static int[] attempts = { 10, 7, 5 };
    static int[] baseScore = { 100, 200, 300 };

    // guarda as últimas 10 partidas
    static String[] histLevel = new String[10];
    static String[] histMode = new String[10];
    static int[] histScore = new int[10];
    static int histCount = 0; 

    static int[] classicRecord = { 0, 0, 0 };
    static int[] sequenceRecord = { 0, 0, 0 };

    public static void main(String[] args) {
        int option = 0;

        while (option != 4) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Iniciar Novo Jogo");
            System.out.println("2. Ver Regras");
            System.out.println("3. Ver Histórico de Pontuações");
            System.out.println("4. Sair");
            System.out.print("Opção: ");

            // hasNextInt() evita que o programa quebre se a pessoa digitar uma letra inves de numero
            if (sc.hasNextInt()) {
                option = sc.nextInt();
                sc.nextLine(); 
            } else {
                sc.nextLine(); 
                option = 0;
            }

            if (option == 1) {
                startNewGame();
            } else if (option == 2) {
                showRules();
            } else if (option == 3) {
                showHistory();
            } else if (option != 4) {
                System.out.println("Opção inválida.");
            }
        }

        System.out.println("Até a próxima!");
    }

    static void startNewGame() {
        System.out.println("\nEscolha o nível:");
        for (int i = 0; i < levelName.length; i++) {
            System.out.println((i + 1) + " - " + levelName[i] + " (1 a " + maxNumber[i] + ", " + attempts[i] + " tentativas)");
        }
        System.out.print("Opção: ");

        int level = 0;
        if (sc.hasNextInt()) {
            level = sc.nextInt() - 1;
            sc.nextLine();
        } else {
            sc.nextLine();
        }

        System.out.println("\n1 - Clássico (1 número)  2 - Sequência (3 números)");
        System.out.print("Opção: ");
        String mode = sc.nextLine();

        if (mode.equals("2")) {
            playSequence(level);
        } else {
            playClassic(level);
        }
    }

    static void playClassic(int level) {
        int max = maxNumber[level];
        int total = attempts[level];
        int secretNumber = rnd.nextInt(max) + 1;

        int used = 0;
        int hintCost = 0;
        int lastGuess = 0;
        boolean won = false;

        System.out.println("\nAdivinhe o número entre 1 e " + max + "!");

        while (used < total && !won) {
            System.out.print("Tentativa " + (used + 1) + "/" + total + " (ou \"dica\"): ");

            // se não for número, deve ser "dica"
            if (!sc.hasNextInt()) {
                String text = sc.nextLine();
                if (text.equalsIgnoreCase("dica")) {
                    hintCost += giveHint(secretNumber, max, lastGuess);
                } else {
                    System.out.println("Digite um número válido.");
                }
                continue;
            }

            int guess = sc.nextInt();
            sc.nextLine();
            used++;
            lastGuess = guess; 

            if (guess == secretNumber) {
                won = true;
            } else if (guess < secretNumber) {
                System.out.println("O número é maior!");
            } else {
                System.out.println("O número é menor!");
            }
        }

        int score = 0;
        if (won) {
            int remaining = total - used;
            // desconta 10 por erro e por dica usada, soma 50 por tentativa sobrando
            score = baseScore[level] - (used - 1) * 10 - hintCost + remaining * 50;
            if (score < 0) score = 0;
            System.out.println("Acertou em " + used + " tentativa(s)! Pontos: " + score);
        } else {
            System.out.println("Suas tentativas acabaram! O número era " + secretNumber);
        }

        saveHistory(levelName[level], "Clássico", score);

        if (score > classicRecord[level]) {
            classicRecord[level] = score;
            System.out.println("Novo recorde no modo Clássico - " + levelName[level] + "!");
        }
    }

    // 3 números secretos, adivinha um de cada vez
    static void playSequence(int level) {
        int max = maxNumber[level];
        int total = attempts[level];
        int[] numbers = { rnd.nextInt(max) + 1, rnd.nextInt(max) + 1, rnd.nextInt(max) + 1 };
        int totalScore = 0;

        System.out.println("\nDescubra os 3 números secretos, um de cada vez!");

        for (int i = 0; i < 3; i++) {
            int secretNumber = numbers[i];
            int used = 0;
            int hintCost = 0;
            int lastGuess = 0;
            boolean won = false;

            System.out.println("\n-- Número " + (i + 1) + " de 3 --");

            while (used < total && !won) {
                System.out.print("Tentativa " + (used + 1) + "/" + total + " (ou \"dica\"): ");

                if (!sc.hasNextInt()) {
                    String text = sc.nextLine();
                    if (text.equalsIgnoreCase("dica")) {
                        hintCost += giveHint(secretNumber, max, lastGuess);
                    } else {
                        System.out.println("Digite um número válido.");
                    }
                    continue;
                }

                int guess = sc.nextInt();
                sc.nextLine();
                used++;
                lastGuess = guess;

                if (guess == secretNumber) {
                    won = true;
                } else if (guess < secretNumber) {
                    System.out.println("O número é maior!");
                } else {
                    System.out.println("O número é menor!");
                }
            }

            if (won) {
                int remaining = total - used;
                int score = baseScore[level] / 3 - (used - 1) * 10 - hintCost + remaining * 50;
                if (score < 0) score = 0;
                totalScore += score;
                System.out.println("Acertou! +" + score + " pontos.");
            } else {
                System.out.println("Não foi dessa vez! Era " + secretNumber);
            }
        }

        System.out.println("\nPontuação total: " + totalScore);
        saveHistory(levelName[level], "Sequência", totalScore);

        if (totalScore > sequenceRecord[level]) {
            sequenceRecord[level] = totalScore;
            System.out.println("Novo recorde no modo Sequência - " + levelName[level] + "!");
        }
    }

    // retorna quanto a dica custou
    static int giveHint(int secretNumber, int max, int lastGuess) {
        System.out.println("1 - Par/ímpar (-10)  2 - Metade superior/inferior (-20)  3 - Quente/frio (-15)  0 - Cancelar");
        System.out.print("Opção: ");
        String option = sc.nextLine();

        if (option.equals("1")) {
            System.out.println("O número é " + (secretNumber % 2 == 0 ? "PAR" : "ÍMPAR"));
            return 10;
        } else if (option.equals("2")) {
            System.out.println("O número está na metade " + (secretNumber > max / 2 ? "SUPERIOR" : "INFERIOR"));
            return 20;
        } else if (option.equals("3")) {
            // só funciona se já tiver um palpite pra comparar
            if (lastGuess == 0) {
                System.out.println("Faça pelo menos uma tentativa antes de usar essa dica.");
                return 0;
            }
            int difference = Math.abs(lastGuess - secretNumber);
            if (difference <= max * 0.05) {
                System.out.println("Muito quente!");
            } else if (difference <= max * 0.15) {
                System.out.println("Quente.");
            } else if (difference <= max * 0.30) {
                System.out.println("Morno.");
            } else {
                System.out.println("Frio.");
            }
            return 15;
        }
        return 0;
    }

    static void saveHistory(String level, String mode, int score) {
        // quando o array esta cheio, desloca tudo uma posição pra esquerda
        if (histCount == 10) {
            for (int i = 0; i < 9; i++) {
                histLevel[i] = histLevel[i + 1];
                histMode[i] = histMode[i + 1];
                histScore[i] = histScore[i + 1];
            }
            histLevel[9] = level;
            histMode[9] = mode;
            histScore[9] = score;
        } else {
            histLevel[histCount] = level;
            histMode[histCount] = mode;
            histScore[histCount] = score;
            histCount++;
        }
    }

    static void showHistory() {
        System.out.println("\n===== HISTÓRICO =====");
        if (histCount == 0) {
            System.out.println("Nenhuma partida ainda.");
        } else {
            for (int i = histCount - 1; i >= 0; i--) {
                System.out.println(histMode[i] + " - " + histLevel[i] + ": " + histScore[i] + " pts");
            }
        }

        System.out.println("\n===== RECORDES =====");
        for (int i = 0; i < levelName.length; i++) {
            System.out.println("Clássico - " + levelName[i] + ": " + classicRecord[i] + " pts");
        }
        for (int i = 0; i < levelName.length; i++) {
            System.out.println("Sequência - " + levelName[i] + ": " + sequenceRecord[i] + " pts");
        }
    }

    static void showRules() {
        System.out.println("\n===== REGRAS =====");
        System.out.println("Modo Clássico: adivinhe 1 número secreto.");
        System.out.println("Modo Sequência: adivinhe 3 números, um de cada vez.");
        System.out.println("Cada erro desconta 10 pontos. Cada tentativa que sobrar soma 50 pontos.");
        System.out.println("Dicas: par/ímpar -10 | metade superior/inferior -20 | quente/frio -15.");
        System.out.println("O histórico guarda as últimas 10 partidas.");
    }
}