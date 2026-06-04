package ru.yandex.practicum;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

class WordleTest {

    private WordleDictionary dictionary;
    private WordleGame game;
    private PrintWriter testLogger;

    @BeforeEach
    void setUp() {
        List<String> testWords = Arrays.asList("герой", "книга", "гроза", "столб", "ручка");
        testLogger = new PrintWriter(System.out, true);
        dictionary = new WordleDictionary(testWords, testLogger);
        game = new WordleGame(dictionary, testLogger);
    }

    @Test
    void testDictionaryContainsAndNormalized() {
        assertTrue(dictionary.contains("герой"));
        assertTrue(dictionary.contains("КНИГА"));
        assertFalse(dictionary.contains("йцуке"));
        assertFalse(dictionary.contains(null));
    }

    @Test
    void testDictionaryCompareWords() {
        assertEquals("+++++", WordleDictionary.compareWords("герой", "герой"));
        assertEquals("+-^^-", WordleDictionary.compareWords("герой", "гроза"));
        assertEquals("-----", WordleDictionary.compareWords("йцуке", "ячсми"));
    }

    @Test
    void testGameSetUp() {
        assertNotNull(game.getAnswer());
        assertEquals(0, game.getSteps());
        assertEquals(6, game.getRemainingSteps());
        assertFalse(game.isGameOver());
        assertFalse(game.isWordGuessed());
    }

    @Test
    void testDoTwoSteps() throws Exception {
        game.makeGuess("столб");
        assertEquals(1, game.getSteps());
        assertEquals(1, game.getGuesses().size());
        assertEquals("столб", game.getGuesses().get(0));

        if (!game.isGameOver()) {
            game.makeGuess("ручка");
            assertEquals(2, game.getSteps());
            assertEquals(2, game.getGuesses().size());
            assertEquals("ручка", game.getGuesses().get(1));
        } else {
            // Если игра закончилась, проверяем, что это победа
            assertEquals(1, game.getSteps());
            assertEquals(1, game.getGuesses().size());
            assertEquals("столб", game.getGuesses().get(0));
        }
    }

    @Test
    void testGameOverAfterMaxAttempts() throws Exception {
        for (int i = 0; i < 6; i++) {
            if (!game.isGameOver()) {
                game.makeGuess("столб");
            }
        }
        assertTrue(game.isGameOver());
        assertFalse(game.isWordGuessed());
    }

    @Test
    void testGetHint() {
        String hint = game.getHint();
        assertTrue(dictionary.contains(hint));
    }

    @Test
    void testIsGameOver() throws Exception {
        assertFalse(game.isGameOver());
        String answer = game.getAnswer();
        game.makeGuess(answer);
        assertTrue(game.isGameOver());
    }

    @Test
    void testIsWordGuessed() throws Exception {
        assertFalse(game.isWordGuessed());
        String answer = game.getAnswer();
        game.makeGuess(answer);
        assertTrue(game.isWordGuessed());
    }

}
