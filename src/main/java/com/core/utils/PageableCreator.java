package com.core.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableCreator {
    public static Pageable createPageable(int page, int size, String sortCriteria, String direction) {
        Sort.Direction sortDirection = (direction == null || direction.isEmpty()) ? Sort.Direction.DESC : Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortCriteria);

        return PageRequest.of(page, size, sort);
    }
}
