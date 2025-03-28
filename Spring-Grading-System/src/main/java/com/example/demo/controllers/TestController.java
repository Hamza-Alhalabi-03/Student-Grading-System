package com.example.demo.controllers;

import com.example.demo.data.GradingSystemDAO;
import com.example.demo.data.ToDoDao;
import com.example.demo.models.ToDo;
import java.util.List;
import java.util.Map;

import com.example.demo.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todo")
public class TestController {
    private final GradingSystemDAO dao;

    public TestController(GradingSystemDAO dao) {
        this.dao = dao;
    }

    @GetMapping
    public Map<String, String> all() {
        return dao.getUsers();
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public User create(@RequestBody ToDo todo) {
//        return dao.addUser(String username, String password);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ToDo> findById(@PathVariable int id) {
//        ToDo result = dao.findById(id);
//        if (result == null) {
//            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
//        }
//        return ResponseEntity.ok(result);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity update(@PathVariable int id, @RequestBody ToDo todo) {
//        ResponseEntity response = new ResponseEntity(HttpStatus.NOT_FOUND);
//        if (id != todo.getId()) {
//            response = new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
//        } else if (dao.update(todo)) {
//            response = new ResponseEntity(HttpStatus.NO_CONTENT);
//        }
//        return response;
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity delete(@PathVariable int id) {
//        if (dao.deleteById(id)) {
//            return new ResponseEntity(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity(HttpStatus.NOT_FOUND);
//    }

}
