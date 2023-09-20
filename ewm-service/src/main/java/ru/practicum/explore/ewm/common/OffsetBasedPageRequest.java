package ru.practicum.explore.ewm.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetBasedPageRequest implements Pageable {
    private int limit;
    private long offset;
    private final Sort sort;

    public OffsetBasedPageRequest(int limit, long offset, Sort sort) {
        if(offset<0) {
            throw new IllegalArgumentException("Offset index must not be less then zero!");
        }
        if(limit<1){
            throw new IllegalArgumentException("Limit must not be less then one!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public OffsetBasedPageRequest(int limit, long offset) {
        this(limit, offset, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return  (int) offset/limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(getPageSize(),getOffset()+getPageSize(),  getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previos(): first();
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(getPageSize(),0,  getSort());
    }

    public Pageable previos() {
        return hasPrevious()? new OffsetBasedPageRequest(getPageSize(),getOffset()-getPageSize(),  getSort()):this;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetBasedPageRequest(getPageSize(),pageNumber*limit,  getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset>limit;
    }

}
