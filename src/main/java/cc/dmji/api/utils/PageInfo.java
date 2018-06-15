package cc.dmji.api.utils;

/**
 * 分页信息
 * Created by echisan on 2018/6/8
 */
public class PageInfo {

    private Long totalSize;

    private Integer pageSize;

    private Integer PageNumber;

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return PageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        PageNumber = pageNumber;
    }
}
