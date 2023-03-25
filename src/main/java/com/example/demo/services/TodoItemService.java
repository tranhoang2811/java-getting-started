package com.example.demo.services;

import com.example.demo.models.TodoItem;
import com.example.demo.pageablefilters.TodoItemPageableFilter;
import com.example.demo.repositories.TodoItemRepository;
import com.mongodb.MongoWriteException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoItemService {
  private final TodoItemRepository todoItemRepository;
  private final MongoTemplate mongoTemplate;

  public List<TodoItem> getMany() {
    return todoItemRepository.findAll();
  }

  public Optional<TodoItem> getOne(String id) {
    try {
      return todoItemRepository.findById(id);
    } catch (EmptyResultDataAccessException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found", exception);
    }
  }

  public TodoItem createOne(TodoItem todoItem) {
    try {
      return todoItemRepository.insert(todoItem);
    } catch (Exception exception) {
      throw exception;
    }
  }

  public void deleteOne(String id) {
    try {
      todoItemRepository.deleteById(id);
    } catch (EmptyResultDataAccessException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found", exception);
    }
  }

  public TodoItem updateOne(TodoItem todoItem, String id) {
    try {
      return todoItemRepository.save(todoItem);
    } catch (MongoWriteException exception) {
      if (exception.getError().getCode() == 11000) {
        throw new DuplicateKeyException("Duplicate key error", exception);
      }
      throw exception;
    }
  }

  public List<TodoItem> paginate(TodoItemPageableFilter filter) {
    try {
      Query query = filter.toQuery();
      Pageable pageable = filter.toPageable();
      Sort sort = filter.toSort();
      return mongoTemplate.find(query.with(pageable).with(sort), TodoItem.class);
    } catch (Exception exception) {
      throw exception;
    }
  }
}
