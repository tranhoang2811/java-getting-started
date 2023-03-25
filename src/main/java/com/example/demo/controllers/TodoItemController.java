package com.example.demo.controllers;

import com.example.demo.models.TodoItem;
import com.example.demo.pageablefilters.TodoItemPageableFilter;
import com.example.demo.services.TodoItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/todos")
@RequiredArgsConstructor
public class TodoItemController {
  private final TodoItemService todoItemService;

  @GetMapping
  public List<TodoItem> getTodoItemList() {
    return todoItemService.getMany();
  }

  @GetMapping(path = "{id}")
  public Optional<TodoItem> getTodoItem(@PathVariable("id") String id) {
    return todoItemService.getOne(id);
  }

  @GetMapping("/paginate")
  public List<TodoItem> getPaginate(@ModelAttribute TodoItemPageableFilter filter) {
    return todoItemService.paginate(filter);
  }

  @PostMapping
  public TodoItem createTodoItem(@RequestBody TodoItem todoItem) {
    return todoItemService.createOne(todoItem);
  }

  @DeleteMapping(path = "{id}")
  public void deleteTodoItem(@PathVariable("id") String id) {
    todoItemService.deleteOne(id);
  }

  @PutMapping(path = "{id}")
  public TodoItem updateTodoItem(@RequestBody TodoItem todoItem, @PathVariable("id") String id) {
    return todoItemService.updateOne(todoItem, id);
  }
}
