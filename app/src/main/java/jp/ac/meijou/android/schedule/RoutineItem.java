package jp.ac.meijou.android.schedule;

public class RoutineItem {
    private String name;
    private String startTime;
    private String duration;

    public RoutineItem(String name, String startTime, String duration) {
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
    }

    // Getter メソッドを追加
    public String getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDuration() {
        return duration;
    }
}
