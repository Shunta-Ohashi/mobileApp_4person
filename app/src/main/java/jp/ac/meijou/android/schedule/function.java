package jp.ac.meijou.android.schedule;

import java.util.ArrayList;
import java.util.List;

public class function {

    /** Habit用 指定した予定を duration 分だけ埋める */
    /**  例　time[15][1] = 15時30-59分  duration=1は30分を表す,task はString*/
    public void occupy(habit[][] time, int h, int m, int duration, habit task) {
        int nh = h, nm = m;
        for (int i = 0; i < duration; i++) {
            time[nh][nm] = task;
            nm++;
            if (nm == 2) { nm = 0; nh++; }
            if (nh >= 24) break;
        }
    }

    /** ToDo用 指定した予定を duration 分だけ埋める */
    public static void occupy(ToDo[][] time, int h, int m, int duration, ToDo task) {
        int nh = h, nm = m;
        for (int i = 0; i < duration; i++) {
            time[nh][nm] = task;
            nm++;
            if (nm == 2) { nm = 0; nh++; }
            if (nh >= 24) break;
        }
    }

    /** duration 分の空き時間がある開始時刻をすべて返す（time[h][m]の数字で） */
    static List<int[]> findFreeSlots(ToDo[][] time, int duration) {
        List<int[]> result = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 2; m++) {
                if (canFit(time, h, m, duration)) {
                    result.add(new int[]{h, m});
                }
            }
        }
        return result;
    }

    /** (h,m)から duration 分空いているか判定 */
    static boolean canFit(ToDo[][] time, int h, int m, int duration) {
        int nh = h, nm = m;
        for (int i = 0; i < duration; i++) {
            if (nh >= 24) return false;
            if (time[nh][nm] != null) return false;
            nm++;
            if (nm == 2) { nm = 0; nh++; }
        }
        return true;
    }


    /** ToDo を入れられる候補の開始時刻を探す */
    static List<int[]> findSlotsForTask(ToDo[][] time, ToDo task) {
        return findFreeSlots(time, task.getDuration());
    }

    /**  動作例
     public static void main(String[] args) {
     // ToDo用の時間割
     ToDo[][] todoTime = new ToDo[24][2];
     // Habit用の時間割
     habit[][] habitTime = new habit[24][2];

     // Habitを入れる（生活習慣）
     occupy(habitTime, 7, 0, 2, new habit("朝ごはん", 2));  // 7:00〜8:00
     occupy(habitTime, 23, 0, 2, new habit("就寝", 2));     // 23:00〜翌1:00


     // 新しいToDo
     ToDo newTask = new ToDo("勉強", 2);

     // 空き時間を探す
     List<TimeSlot> slots = findSlotsForTask(todoTime, newTask);

     // 出力

     System.out.println("候補:");
     for (int[] slot : slots) {
     System.out.println("time[" + slot[0] + "][" + slot[1] + "]");
     }

     出力結果

     候補:
     time[0][0]
     time[0][1]
     */

    /** 時間を表す数値の組 */
    private static class TimeSlot {
        int hour; // 時
        int half; // 0=00分, 1=30分

        TimeSlot(int hour, int half) {
            this.hour = hour;
            this.half = half;
        }

    }

    /** やるべき課題 ToDo */
    public static class ToDo {

        private String title;
        private int duration; // 30分単位

        public ToDo(String title, int duration) {
            this.title = title;
            this.duration = duration;
        }

        public String getTitle() {
            return title;
        }

        public int getDuration() {
            return duration;
        }
    }

    /** 生活習慣 Habit */
    private class habit {
        private String title;
        private int duration; // 30分単位

        public habit(String title, int duration) {
            this.title = title;
            this.duration = duration;
        }

        public String getTitle() {
            return title;
        }

        public int getDuration() {
            return duration;
        }
    }
}