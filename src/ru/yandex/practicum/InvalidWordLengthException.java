package ru.yandex.practicum;

public class InvalidWordLengthException extends Exception {

    private final String word;

    public InvalidWordLengthException(String word) {

        super("Слово '" + word + "' должно состоять из 5 букв");

        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
