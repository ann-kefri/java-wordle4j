package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this. dictionary = dictionary;
        this.logger = logger;

        this.answer = dictionary.getRandomWord();

        this.steps = 0;

        this.gameOver = false;
        this.wordGuessed = false;

        this.guesses = new ArrayList<>();
        this.patterns = new ArrayList<>();

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

        log("Попытка " + steps + ", " + normalizedWord + " = " + pattern);

        return pattern;
    }

    public String getHint() {
        if (gameOver) {
            log("Игра закончена, невозможно выдать подсказку.");
            return null;
        }

        if (guesses.isEmpty()) {
            log("Первая подсказка");
            return dictionary.getRandomWord();
        }

        List<String> possibleWords = dictionary.filterWordsByAllPatterns(guesses, patterns);
        possibleWords.removeAll(guesses);

        if (possibleWords.isEmpty()) {
            log("Нет доступных слов");
            return null;
        }

        Random random = new Random();
        String hint = possibleWords.get(random.nextInt(possibleWords.size()));

        log("Подсказка дана.");

        return hint;
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

    public void printState() {
        System.out.println("Состояние игры:");
        System.out.println("Шагов использовано: " + steps + " из " + MAX_ATTEMPTS);
        System.out.println("Игра окончена: " + gameOver);
        System.out.println("Победа: " + wordGuessed);
    }

    private void log(String message) {
        if (logger != null) {
            logger.println("WordleGame " + message);
            logger.flush();
        }
    }

}
