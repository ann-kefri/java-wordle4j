package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words;
    private final Set<String> wordSet;
    private final PrintWriter logger;

    public WordleDictionary(List<String> words, PrintWriter logger) {
        this.words = new ArrayList<>(words);
        this.wordSet = new HashSet<>(words);
        this.logger = logger;

        logger.println("Словарь создан");
    }

    public boolean contains(String word) {
        if (word == null) {
            return false;
        }

        String normalized = normalizeWord(word);
        return wordSet.contains(normalized);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            logger.println("WordleDictionary - getRandomWord ОШИБКА: словарь пуст");
            return null;
        }

        Random random = new Random();
        String randomWord = words.get(random.nextInt(words.size()));
        logger.println("WordleDictionary - getRandomWord Выбрано случайное слово");

        return randomWord;
    }

    public static String normalizeWord(String word) {
        if (word == null) {
            return null;
        }
        String normalized = word.toLowerCase();
        normalized = normalized.replace("ё", "е");
        return normalized;
    }

    public static String compareWords(String guess, String target) {
        guess = normalizeWord(guess);
        target = normalizeWord(target);

        char[] result = new char[guess.length()];
        boolean[] targetUsed = new boolean[target.length()];

        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                result[i] = '+';
                targetUsed[i] = true;
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            if (result[i] == '+') {
                continue;
            }

            char guessChar = guess.charAt(i);
            boolean found = false;

            for (int j = 0; j < target.length(); j++) {
                if (!targetUsed[j] && guessChar == target.charAt(j)) {
                    found = true;
                    targetUsed[j] = true;
                    break;
                }
            }

            result[i] = found ? '^' : '-';
        }

        return new String(result);
    }

    public List<String> filterWordsByPattern(String guess, String pattern) {
        String normalizedGuess = normalizeWord(guess);

        List<String> filtered = new ArrayList<>();

        for (String candidate : words) {
            if (matchesPattern(normalizedGuess, pattern, candidate)) {
                filtered.add(candidate);
            }
        }

        logger.println("WordleDictionary - filterWordsByPattern Фильтрация по паттерну '" +
                pattern + "' для слова '" + guess);

        return filtered;
    }

    public boolean matchesPattern(String guess, String pattern, String candidate) {
        String normalizedCandidate = normalizeWord(candidate);
        boolean[] candidateUsed = new boolean[normalizedCandidate.length()];

        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == '+') {
                if (normalizedCandidate.charAt(i) != guess.charAt(i)) {
                    return false;
                }
                candidateUsed[i] = true;
            }
        }
        for (int i = 0; i < pattern.length(); i++) {
            char patternChar = pattern.charAt(i);
            char guessChar = guess.charAt(i);

            if (patternChar == '+') {
                continue;
            }

            if (patternChar == '^') {
                if (normalizedCandidate.charAt(i) == guessChar) {
                    return false;
                }

                boolean found = false;
                for (int j = 0; j < pattern.length(); j++) {
                    if (!candidateUsed[j] && normalizedCandidate.charAt(j) == guessChar) {
                        found = true;
                        candidateUsed[j] = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            if (patternChar == '-') {
                for (int j = 0; j < normalizedCandidate.length(); j++) {
                    if (normalizedCandidate.charAt(j) == guessChar) {
                        boolean isMarkedAsCaret = false;
                        for (int k = 0; k < pattern.length(); k++) {
                            if (pattern.charAt(k) == '^' && guess.charAt(k) == guessChar) {
                                isMarkedAsCaret = true;
                                break;
                            }
                        }
                        if (!isMarkedAsCaret) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<String> filterWordsByAllPatterns(List<String> guesses, List<String> patterns) {
        if (guesses.isEmpty()) {
            return new ArrayList<>(words);
        }

        List<String> possibleWords = filterWordsByPattern(guesses.get(0), patterns.get(0));

        for (int i = 1; i < guesses.size(); i++) {
            List<String> currentWords = filterWordsByPattern(guesses.get(i), patterns.get(i));

            possibleWords = intersectLists(possibleWords, currentWords);

            if (possibleWords.isEmpty()) {
                break;
            }
        }

        logger.println("WordleDictionary - filterWordsByAllPatterns");

        return possibleWords;

    }

    private List<String> intersectLists(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();

        for (String word : list1) {
            if (list2.contains(word)) {
                result.add(word);
            }
        }

        return result;
    }

}
