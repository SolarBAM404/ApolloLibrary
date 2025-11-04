package me.solar.apolloLibrary.timers;

import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.Assertions;

public class TimerTests {

    @Test
    public void testTimer() {
        // Since TimerTask and CountdownTask are abstract and depend on Bukkit, we can't instantiate them directly.
        // Instead, we can create a simple subclass for testing purposes.

        class TestTimerTask extends me.solar.apolloLibrary.runnables.TimerTask {
            public TestTimerTask(long time) {
                super(time);
            }

            @Override
            public void runTimer() {
                // Simulate some work
                System.out.println("Timer tick");
            }
        }

        class TestCountdownTask extends me.solar.apolloLibrary.runnables.timerTasks.CountdownTask {
            public TestCountdownTask(long time) {
                super(time);
            }

            @Override
            public void action() {
                // Simulate some work
                System.out.println("Countdown: " + getCountdownTime());
            }
        }

        // Create instances of the test tasks
        TestTimerTask timerTask = new TestTimerTask(5);
        TestCountdownTask countdownTask = new TestCountdownTask(5);

        BukkitTask mockBukkitTask = Mockito.mock(BukkitTask.class);

        // Simulate the passage of time by calling accept multiple times
        for (int i = 0; i < 6; i++) {
            timerTask.runTimer(); // Passing null as BukkitTask since we are not using it in this test
            countdownTask.runTimer(); // Same here
        }

        Assertions.assertEquals(5, timerTask.getTime(), "TimerTask time should be 5");
        Assertions.assertEquals(5, countdownTask.getTime(), "CountdownTask time should be 5");
        Assertions.assertEquals(0, countdownTask.getCountdownTime(), "CountdownTask countdown time should be 0 after completion");
    }

}
