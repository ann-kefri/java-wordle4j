package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private static final int MAX_ATTEMPTS = 6;
    private static final int WORD_LENGTH = 5;

    private String answer;

    private int steps;

    private WordleDictionary dictionary;

    private final PrintWriter logger;

    private boolean gameOver;

    private boolean wordGuessed;

    private final List<String> guesses;

    private final List<String> patterns;

    private final Set<Character> correctLetters;
    private final Set<Character> misplacedLetters;
    private final Set<Character> wrongLetters;

    private final Map<Integer, Character> correctPositions;
    private final Set<Character> lettersInWord;

    private final List<Map<Integer, Character>> misplacedHistory;

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this. dictionary = dictionary;
        this.logger = logger;

        this.answer = dictionary.getRandomWord();

        this.steps = 0;

        this.gameOver = false;
        this.wordGuessed = false;

        this.guesses = new ArrayList<>();
        this.patterns = new ArrayList<>();

        this.correctLetters = new HashSet<>();
        this.misplacedLetters = new HashSet<>();
        this.wrongLetters = new HashSet<>();
        this.correctPositions = new HashMap<>();
        this.lettersInWord = new HashSet<>();
        this.misplacedHistory = new ArrayList<>();

        log("Игра создана");
    }

    public String makeGuess(String word) throws WordNotFoundException,
                                                InvalidWordLengthException,
                                                GameOverException {
        if (gameOver) {
            throw new GameOverException("Игра закончилась");
        }

        String normalizedWord = WordleDictionary.normalizeWord(word);

        if (normalizedWord.length() != WORD_LENGTH) {
            throw new InvalidWordLengthException(word);
        }

        if (!dictionary.contains(normalizedWord)) {
            throw new WordNotFoundException(word);
        }

        String pattern = WordleDictionary.compareWords(normalizedWord,answer);

        updateLetterSets(normalizedWord, pattern);

        guesses.add(normalizedWord);
        patterns.add(pattern);
        steps++;

        if (pattern.equals("+++++")) {
            wordGuessed = true;
            gameOver = true;
            log("Победа.");
        } else if (steps >= MAX_ATTEMPTS) {
            gameOver = true;
            log("Проигрыш.");
        }

        logAttempt(steps, normalizedWord, pattern);

        return pattern;
    }

    private void updateLetterSets(String word, String pattern) {
        Map<Integer, Character> currentMisplaced = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            char letter = word.charAt(i);
            char result = pattern.charAt(i);

            switch (result) {
                case '+':
                    correctLetters.add(letter);
                    correctPositions.put(i, letter);
                    lettersInWord.add(letter);
                    break;
                case  '^':
                    misplacedLetters.add(letter);
                    lettersInWord.add(letter);
                    currentMisplaced.put(i, letter);
                    break;
                case '-':
                    if (!correctLetters.contains(letter) && !misplacedLetters.contains(letter)) {
                        wrongLetters.add(letter);
                    }
                    break;
            }
        }

        if(!currentMisplaced.isEmpty()) {
            misplacedHistory.add(currentMisplaced);
        }
    }

    public String getHint() {
        if (gameOver) {
            log("Игра закончена, невозможно выдать подсказку");
            return null;
        }

        if (guesses.isEmpty()) {
            log("Первая подсказка");
            return dictionary.getRandomWord();
        }

        List<String> possibleWords = dictionary.getWords();
        List<String> filtered = new ArrayList<>();

        for (String candidate : possibleWords) {
            if (isWordPossible(candidate)) {
                filtered.add(candidate);
            }
        }

        filtered.removeAll(guesses);

        if (filtered.isEmpty()) {
            log("нет доступных слов");
            return null;
        }

        Random random = new Random();
        String hint = filtered.get(random.nextInt(filtered.size()));
        log("Умная подсказка дана");
        return hint;
    }

    private boolean isWordPossible(String candidate) {
        String normalizedCandidate = WordleDictionary.normalizeWord(candidate);

        for (Map.Entry<Integer, Character> entry : correctPositions.entrySet()) {
            int position = entry.getKey();
            char requiredLetter = entry.getValue();
            if (normalizedCandidate.charAt(position) != requiredLetter) {
                return false;
            }
        }

        for (char letter : wrongLetters) {
            if (normalizedCandidate.indexOf(letter) != -1) {
                return false;
            }
        }

        for (char letter : lettersInWord) {
            if (normalizedCandidate.indexOf(letter) == -1) {
                return false;
            }
        }

        for (Map<Integer, Character> history : misplacedHistory) {
            for (Map.Entry<Integer, Character> entry : history.entrySet()) {
                int position = entry.getKey();
                char letter = entry.getValue();
                if (normalizedCandidate.charAt(position) == letter) {
                    return false;
                }
            }
        }

        return true;
    }

    public String getAnswer() {
        return answer;
    }

    public int getSteps() {
        return steps;
    }

    public int getRemainingSteps() {
        return MAX_ATTEMPTS - steps;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWordGuessed() {
        return wordGuessed;
    }

    public WordleDictionary getDictionary() {
        return dictionary;
    }

    public List<String> getGuesses() {
        return guesses;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public Set<Character> getCorrectLetters() {
        return correctLetters;
    }

    public Set<Character> getMisplacedLetters() {
        return misplacedLetters;
    }

    public Set<Character> getWrongLetters() {
        return wrongLetters;
    }

    public Map<Integer, Character> getCorrectPositions() {
        return correctPositions;
    }

    public Set<Character> getLettersInWord() {
        return lettersInWord;
    }

    private void log(String message) {
        if (logger != null) {
            logger.println("WordleGame " + message);
            logger.flush();
        }
    }

    private void logAttempt(int step, String word, String pattern) {
        StringBuilder sb = new StringBuilder();
        sb.append("Попытка");
        sb.append(step);
        sb.append(", ");
        sb.append(word);
        sb.append(" = ");
        sb.append(pattern);
        log(sb.toString());
    }

}
