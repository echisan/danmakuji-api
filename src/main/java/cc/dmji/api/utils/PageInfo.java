package cc.dmji.api.utils;

/**
 * 分页信息
 * Created by echisan on 2018/6/8
 */
public class PageInfo {

    private long totalSize;

    private int pageSize;

    private int pageNumber;

    public PageInfo(){}

    public PageInfo(int pageNumber, int pageSize, long totalSize){
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
