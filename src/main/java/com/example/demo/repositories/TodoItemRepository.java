package com.example.demo.repositories;

import com.example.demo.models.TodoItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemRepository extends MongoRepository<TodoItem, String> {}
