package ru.yandex.practicum;

public class WordNotFoundException extends Exception {
    private final String word;

    public WordNotFoundException(String word) {
        super("Слово '" + word + "' не найдено в нашем словаре");
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
