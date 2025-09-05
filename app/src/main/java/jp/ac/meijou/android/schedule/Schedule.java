package jp.ac.meijou.android.schedule;

public class Schedule {
    private String time;
    private String schedule;

    public Schedule(String time, String schedule) {
        this.time = time;
        this.schedule = schedule;
    }

    public String getTime() {
        return time;
    }

    public String getSchedule() {
        return schedule;
    }
}

