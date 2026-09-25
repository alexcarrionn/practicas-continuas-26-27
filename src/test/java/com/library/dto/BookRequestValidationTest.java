package com.library.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Test
    void titleIsRequired() {
        BookRequest request = new BookRequest("", "Autor", "Novela", null, 2000, 100);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void isbnMustHave10Or13DigitsWhenProvided() {
        BookRequest request = new BookRequest("Libro", "Autor", "Novela", "12345", 2000, 100);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void pagesMustBePositive() {
        BookRequest request = new BookRequest("Libro", "Autor", "Novela", null, 2000, 0);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void publishedYearMustBeAtLeast1450() {
        BookRequest request = new BookRequest("Libro", "Autor", "Novela", null, 1449, 100);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void validRequestPassesValidation() {
        BookRequest request = new BookRequest("Libro", "Autor", "Novela", "1234567890", 2000, 100);

        assertTrue(validator.validate(request).isEmpty());
    }
}
