package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    private final PrintWriter logger;

    public WordleDictionaryLoader(PrintWriter logger) {
        this.logger = logger;
        log("Создан загрузчик словаря");
    }

    public WordleDictionary loadDictionary(String filename) throws IOException {
        log("Начинаю загрузку слов из файла: " + filename);

        List<String> rawWords = readWordsFromFile(filename);
        log("Прочитано строк из файла: " + rawWords.size());

        List<String> validWords = processWords(rawWords);
        log("После обработки (5 букв): " + validWords.size());

        if (validWords.isEmpty()) {
            log("Словарь пуст! В файле нет слов из 5 букв.");
            throw new RuntimeException("Словарь пуст! В файле нет слов из 5 букв.");
        }

        WordleDictionary dictionary = new WordleDictionary(validWords, logger);
        log("Словарь создан");

        return dictionary;
    }

    private List<String> readWordsFromFile(String fileName) throws IOException {
        List<String> words = new ArrayList<>();
        log("WordleDictionaryLoader-readWordsFromFile");

        try (BufferedReader  reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileName),
                        StandardCharsets.UTF_8))){
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                words.add(line);
            }
            log("Обработано строк: " + lineNumber + ", добавлено слов: " + words.size());
        } catch (FileNotFoundException e) {
            log("Файл не найден");
            throw new IOException("Файл не найден");
        }

        return words;
    }

    private List<String> processWords(List<String> rawWords) {
        List<String> validWords = new ArrayList<>();
        log("processWords start");

        for (String word : rawWords) {
            String normalized = WordleDictionary.normalizeWord(word);
            if (normalized.length() == 5) {
                validWords.add(normalized);
            }
        }
        log("Подходящих слов: " + validWords.size());

        List<String> uniqueWords = removeDuplicates(validWords);

        return uniqueWords;
    }

    private List<String> removeDuplicates(List<String> words) {
        Set<String> uniqueWords = new LinkedHashSet<>(words);
        return new ArrayList<>(uniqueWords);
    }

    private void log(String message) {
        if (logger != null) {
            logger.println(message);
            logger.flush();
        }
    }

}
