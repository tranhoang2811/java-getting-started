package com.example.demo.pageablefilters;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.beans.PropertyDescriptor;
import java.util.*;

public class TodoItemPageableFilter {
  private String title;
  private boolean completed;
  private int pageNumber;
  private int pageSize;
  private String sort;

  public TodoItemPageableFilter(String title, boolean completed, int pageNumber, int pageSize) {
    this.title = title;
    this.completed = completed;
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public Query toQuery() {
    Criteria criteria = new Criteria();
    BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(this);
    Set<String> excludedProperties =
        new HashSet<>(Arrays.asList("class", "sort", "id", "pageSize", "pageNumber"));

    for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
      String propertyName = descriptor.getName();
      Object propertyValue = wrapper.getPropertyValue(propertyName);

      if (propertyValue != null && !excludedProperties.contains(propertyName)) {
        criteria.and(propertyName).is(propertyValue);
      }
    }
    return new Query(criteria);
  }

  public Pageable toPageable() {
    return PageRequest.of(pageNumber, pageSize);
  }

  public Sort toSort() {
    Sort.Direction direction = Sort.Direction.ASC;
    if (getSort().startsWith("-")) {
      direction = Sort.Direction.DESC;
      setSort(getSort().substring(1));
    }
    return Sort.by(direction, getSort());
  }
}
