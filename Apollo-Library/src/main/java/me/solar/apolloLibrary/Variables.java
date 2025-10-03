package me.solar.apolloLibrary;

import lombok.Setter;
import org.bukkit.entity.Player;

public class Variables {

    private Variables() {
        throw new IllegalStateException("Utility class");
    }

    @Setter
    public static final String VERSION = "1.0.0";
    @Setter
    public static final String NAME = "ApolloLibrary";
    @Setter
    public static final String AUTHOR = "Solar";
    @Setter
    public static final String URL = "https://github.com/Solar-github/ApolloLibrary";


    public static String replacepPlayer(String string, Player player) {
        return string.replace("{player}", player.getName());
    }

    public static String replace(String string, Player player) {
        return string.replace("{player}", player.getName())
                .replace("{version}", VERSION)
                .replace("{name}", NAME)
                .replace("{author}", AUTHOR)
                .replace("{url}", URL);
    }

}
