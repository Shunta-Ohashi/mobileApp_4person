package jp.ac.meijou.android.schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private List<Schedule> scheduleList = new ArrayList<>();
    private static final String PREFS_NAME = "schedule_prefs";
    private static final String KEY_SCHEDULE_LIST = "schedule_list_json";

    public ScheduleRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        // 初期化処理
    }

    @Override
    public void onDataSetChanged() {
        // データの再読み込み
        scheduleList.clear();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String jsonStr = prefs.getString(KEY_SCHEDULE_LIST, null);

        if (jsonStr != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String time = obj.getString("time");
                    String schedule = obj.getString("schedule");
                    // "予定はありません" 以外のものを表示したい場合はここでフィルタリング
                    if (!"予定はありません".equals(schedule)) {
                        scheduleList.add(new Schedule(time, schedule));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        scheduleList.clear();
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position < 0 || position >= scheduleList.size()) return null;

        Schedule item = scheduleList.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item);

        views.setTextViewText(R.id.widgetItemTime, item.getTime());
        views.setTextViewText(R.id.widgetItemContent, item.getSchedule());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
