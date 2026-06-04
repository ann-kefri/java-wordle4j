package ru.yandex.practicum;

public class InvalidWordLengthException extends Exception {

    private final String word;

    public InvalidWordLengthException(String word) {

        super(String.format("Слово '%s' должно состоять из 5 букв", word));

        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
