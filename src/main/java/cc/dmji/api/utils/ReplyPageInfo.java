package cc.dmji.api.utils;


/**
 * Created by echisan on 2018/5/26
 */
public class ReplyPageInfo {

    // 默认长度
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    // 默认子评论长度
    public static final Integer DEFAULT_SON_PAGE_SIZE = 10;

    // 总评论
    private Long totalSize;

    // 父级评论
    private Long parentTotalSize;

    // 第几页
    private Integer pageNumber;

    // 每页长度
    private Integer pageSize;


    public ReplyPageInfo() {
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Long getParentTotalSize() {
        return parentTotalSize;
    }

    public void setParentTotalSize(Long parentTotalSize) {
        this.parentTotalSize = parentTotalSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
