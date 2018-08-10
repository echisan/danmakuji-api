package cc.dmji.api.web.listener;

import org.springframework.context.ApplicationEvent;

import java.util.List;

public class SysMessageEvent extends ApplicationEvent {
    private List<Long> uids;
    private String title;
    private String content;

    public SysMessageEvent(Object source, List<Long> uids, String title, String content) {
        super(source);
        this.uids = uids;
        this.title = title;
        this.content = content;
    }

    public List<Long> getUids() {
        return uids;
    }

    public void setUids(List<Long> uids) {
        this.uids = uids;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
