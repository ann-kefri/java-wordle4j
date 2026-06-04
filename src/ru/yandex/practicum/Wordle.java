package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        try {
            PrintWriter logger = createLogger();
            writeLogHeader(logger);

            WordleDictionaryLoader loader = new WordleDictionaryLoader(logger);
            WordleDictionary dictionary = loader.loadDictionary(DICTIONARY_FILE);

            WordleGame game = new WordleGame(dictionary, logger);
            playGame(game, logger);

            if (game.isGameOver()) {
                if (!game.isWordGuessed()) {
                    System.out.println("Игра окончена. Вы не угадали слово");
                }
            }

            writeLogFooter(logger, game);
        } catch (DictionaryEmptyException e) {
            System.out.println("Файл не найден");
        } catch (Exception e) {
            System.out.println("Непредвиденная ошибка");
        }
    }

    private static PrintWriter createLogger() throws IOException {
        FileOutputStream fos = new FileOutputStream(LOG_FILE, true);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        return new PrintWriter(osw);
    }

    private static void writeLogHeader(PrintWriter logger) {
        logger.println("Новый запуск игры.");
    }

    private static void writeLogFooter(PrintWriter logger, WordleGame game) {
        logger.println("Игра завершена.");
        logger.println("Загаданное слово: " + game.getAnswer());
        logger.println("Сделано шагов: " + game.getSteps());
        logger.println("Результат: " + (game.isWordGuessed() ? "ПОБЕДА" : "ПОРАЖЕНИЕ"));
    }

    private static void playGame(WordleGame game, PrintWriter logger) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("ДОБРО ПОЖАЛОВАТЬ В ИГРУ WORDLE!");

        while (!game.isGameOver()) {
            printGameStatus(game);

            System.out.println("\nВведите слово или нажмите Enter для получения подсказки");
            String input = scanner.nextLine().trim();

            logger.println("Ввели: " + input);

            if (input.isEmpty()) {
                handleHintRequest(game, logger);
                continue;
            }

            try {
                String result = game.makeGuess(input);
                System.out.println(input);
                System.out.println(result);
                logger.println("Слово - паттерн: " + input + " - " + result);

                if (game.isWordGuessed()) {
                    System.out.println("Поздравляем! Вы угадали слово!");
                    break;
                }
            } catch (WordNotFoundException e) {
                System.out.println("Слово '" + e.getWord() + "' не найдено в словаре.");
                logger.println("Слово '" + e.getWord() + "' не найдено в словаре.");
            } catch (InvalidWordLengthException e) {
                System.out.println("Слово должно состоять из 5 букв");
                logger.println("Слово должно состоять из 5 букв");
            } catch (GameOverException e) {
                System.out.println(e.getMessage());
                logger.println(e.getMessage());
                break;
            }
        }
        scanner.close();
    }

    private static void printGameStatus(WordleGame game) {
        System.out.println("Осталось попыток: " + game.getRemainingSteps());

        if (game.getSteps() > 0) {
            System.out.println("Ваши предыдущие попытки: ");
            List<String> guesses = game.getGuesses();
            List<String> patterns = game.getPatterns();
            for (int i = 0; i < game.getSteps(); i++) {
                System.out.println((i + 1) + ". " + guesses.get(i) + " = " + patterns.get(i));
            }
        }
    }

    private static void handleHintRequest(WordleGame game, PrintWriter logger) {
        String hint = game.getHint();
        if (hint == null) {
            System.out.println("Нет подсказок.");
            logger.println("Нет подсказок");
        } else {
            System.out.println("Подсказка: " + hint);
            logger.println("Подсказка: " + hint);
        }
    }

}
