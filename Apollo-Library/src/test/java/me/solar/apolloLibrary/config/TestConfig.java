package me.solar.apolloLibrary.config;

@ConfigFormat(FormatType.YAML)
public class TestConfig {

    @ConfigKey(value = "number", comment = "A number")
    public static int number = 42;

    @ConfigKey(value = "name", comment = "A string name")
    public static String name = "Hello World!";

    @ConfigKey(value = "inner", comment = "An inner class")
    public static class Inner {

        @ConfigKey(value = "flag", comment = "An inner flag")
        public static boolean flag = false;

    }

}
