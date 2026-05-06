package blinov_first.util;

import java.util.List;

public class Page<T> {

    private final List<T> items;
    private final int currentPage;
    private final int pageSize;
    private final int totalItems;
    private final int totalPages;

    public Page(List<T> items, int currentPage, int pageSize, int totalItems) {
        this.items       = items;
        this.currentPage = currentPage;
        this.pageSize    = pageSize;
        this.totalItems  = totalItems;
        this.totalPages  = (int) Math.ceil((double) totalItems / pageSize);
    }

    public List<T> getItems()        { return items; }
    public int     getCurrentPage()  { return currentPage; }
    public int     getPageSize()     { return pageSize; }
    public int     getTotalItems()   { return totalItems; }
    public int     getTotalPages()   { return totalPages; }

    public boolean isPreviousAvailable() { return currentPage > 1; }
    public boolean isNextAvailable()     { return currentPage < totalPages; }
    public int     getPreviousPage()     { return currentPage - 1; }
    public int     getNextPage()         { return currentPage + 1; }
}