package lock.brainsynder.api;

public class TimeInfo {
    private long start = 0;
    private int seconds = 0;

    public void setStart(long start) {
        this.start = start;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public long getStart() {
        return start;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean hasTimeRemaining() {
        long seconds = start / 1000L;
        long secondsLeft = (seconds + this.seconds) - (System.currentTimeMillis() / 1000L);
        return (secondsLeft > 0L);
    }

    public int getRemainingTime() {
        long seconds = start / 1000L;
        return (int) ((seconds + this.seconds) - (System.currentTimeMillis() / 1000L));
    }
}