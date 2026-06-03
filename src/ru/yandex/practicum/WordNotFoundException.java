package ru.yandex.practicum;

public class WordNotFoundException extends Exception {
    private final String word;

    public WordNotFoundException(String word) {
        super(String.format("Слово '%s' не найдено в нашем словаре", word));
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
