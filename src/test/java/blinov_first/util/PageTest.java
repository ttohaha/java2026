package blinov_first.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageTest {

    @Test
    void totalPages_shouldBeCalculatedCorrectly() {
        Page<String> page = new Page<>(List.of("a", "b", "c", "d", "e"), 1, 5, 23);
        assertEquals(5, page.getTotalPages());
    }

    @Test
    void totalPages_shouldRoundUp() {
        Page<String> page = new Page<>(List.of(), 1, 5, 21);
        assertEquals(5, page.getTotalPages());
    }

    @Test
    void totalPages_shouldBeOne_whenItemsFitOnOnePage() {
        Page<String> page = new Page<>(List.of("a", "b"), 1, 5, 2);
        assertEquals(1, page.getTotalPages());
    }

    @Test
    void isPreviousAvailable_shouldBeFalse_onFirstPage() {
        Page<String> page = new Page<>(List.of("a"), 1, 5, 10);
        assertFalse(page.isPreviousAvailable());
    }

    @Test
    void isPreviousAvailable_shouldBeTrue_onSecondPage() {
        Page<String> page = new Page<>(List.of("a"), 2, 5, 10);
        assertTrue(page.isPreviousAvailable());
    }

    @Test
    void isNextAvailable_shouldBeFalse_onLastPage() {
        Page<String> page = new Page<>(List.of("a"), 2, 5, 10);
        assertFalse(page.isNextAvailable());
    }

    @Test
    void isNextAvailable_shouldBeTrue_whenMorePagesExist() {
        Page<String> page = new Page<>(List.of("a"), 1, 5, 10);
        assertTrue(page.isNextAvailable());
    }

    @Test
    void getPreviousPage_shouldReturnCurrentMinusOne() {
        Page<String> page = new Page<>(List.of(), 3, 5, 20);
        assertEquals(2, page.getPreviousPage());
    }

    @Test
    void getNextPage_shouldReturnCurrentPlusOne() {
        Page<String> page = new Page<>(List.of(), 3, 5, 20);
        assertEquals(4, page.getNextPage());
    }

    @Test
    void getItems_shouldReturnCorrectList() {
        List<String> items = List.of("x", "y", "z");
        Page<String> page = new Page<>(items, 1, 5, 3);
        assertEquals(items, page.getItems());
    }

    @Test
    void totalPages_shouldBeZero_whenNoItems() {
        Page<String> page = new Page<>(List.of(), 1, 5, 0);
        assertEquals(0, page.getTotalPages());
    }
}
