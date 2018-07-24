package task.twittercodingtask.viewmodel;

/**
 * @Author Qing Guo
 * @Date 2018/7/19
 * @Description interface for calling android life cycle
 */
public interface ModelLifeCycle {
    void onResume();

    void onPause();

    void onDestroy();
}
