package task.twittercodingtask.module;

/**
 * @Author Qing Guo
 * @Date 2018/7/18
 * @Description Observable Beans of the trend module
 */
public class TrendBean {
    public String name;
    public String url;
    public String query;
    public String volume;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
