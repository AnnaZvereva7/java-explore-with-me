package ru.practicum.explore.ewm.common;

import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@EqualsAndHashCode
public class OffsetBasedPageRequest implements Pageable {
    private long offset;
    private int size;
    private final Sort sort;

    public OffsetBasedPageRequest(long offset, int size, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less then zero!");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Limit must not be less then one!");
        }
        this.size = size;
        this.offset = offset;
        this.sort = sort;
    }

    public OffsetBasedPageRequest(long offset, int size) {
        this(offset, size, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return (int) offset / size;
    }

    @Override
    public int getPageSize() {
        return size;
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
        return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previos() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0, getPageSize(), getSort());
    }

    public Pageable previos() {
        return hasPrevious() ? new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetBasedPageRequest(pageNumber * size, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > size;
    }

}
