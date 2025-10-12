package jp.ac.meijou.android.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    /**
     * 空き時間候補からランダムに1つ選び、
     * その開始時刻 (h1, m1) を返す
     * 候補がない場合は null を返す
     */
    static int[] decideStartSlotRandom(ToDo[][] time, ToDo task) {
        int duration = task.getDuration();
        List<int[]> slots = findFreeSlots(time, duration);

        if (slots.isEmpty()) {
            System.out.println("空き時間がありません。");
            return null;
        }

        // ランダムに1つの候補を選ぶ
        Random rand = new Random();
        int[] start = slots.get(rand.nextInt(slots.size()));
        int h1 = start[0];
        int m1 = start[1];

        // ★ここで occupy() を呼び出して登録する
        occupy(time, h1, m1, duration, task);

        System.out.println("決定した開始時刻: " + h1 + "時" + (m1 == 0 ? "00分" : "30分"));
        System.out.println("「" + task.getTitle() + "」を登録しました。");

        // 開始時刻を返す
        return new int[]{h1, m1};
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
     package jp.ac.meijou.android.schedule;

     public class MainTest {
     public static void main(String[] args) {

     // ToDo用の時間割
     function.ToDo[][] todoTime = new function.ToDo[24][2];
     // Habit用の時間割
     function.habit[][] habitTime = new function.habit[24][2];

     // Habitを入れる（生活習慣）
     function.occupy(habitTime, 7, 0, 2, new function.habit("朝ごはん", 2));  // 7:00〜8:00
     function.occupy(habitTime, 23, 0, 2, new function.habit("就寝", 2));     // 23:00〜翌1:00

     // 新しいToDo
     function.ToDo newTask = new function.ToDo("勉強", 2);

     // 空いている1つの時間帯を決定
     int[] slot = function.decideSlot(todoTime, habitTime, newTask);

     if (slot != null) {
     System.out.println("決定した時間帯:");
     System.out.println("開始: time[" + slot[0] + "][" + slot[1] + "]");}
     else {
     System.out.println("空き時間がありません。");
     }
     }
     }


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
    public static class habit implements Serializable {
        public String title;
        public int duration; // 30分単位

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