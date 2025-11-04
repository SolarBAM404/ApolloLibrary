package me.solar.apolloLibrary.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidTests {

    @Test
    public void testCheckNull(){
        String test = null;
        Valid.checkNull(test, "test");
        // Should not throw exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String test2 = "not null";
            Valid.checkNull(test2, "test2");
        });

    }

}
