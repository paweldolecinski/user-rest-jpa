package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exceptions.EntityErrorResponse;
import com.example.demo.exceptions.RoleNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.roleService.RoleService;
import com.example.demo.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {

    private UserService userService;
    private RoleService roleService;

    private Set<Role> userRoles = new HashSet<>();

    @Autowired
    public UserController(UserService userService, RoleService roleService){
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public List<User> findAll(){
        return userService.findAll();
    }

    @PostMapping("/register")
    public User addUser(@RequestBody User user){
        Role role = roleService.findById(3L);
        userRoles.add(role);
        user.setRoles(userRoles);
        userRoles = null;
        user.setId(0L);
        userService.save(user);
        return user;
    }

    @PostMapping("/admin-add-user")
    public User adminAddUser(@RequestBody User user){
        user.setId(0L);
        userService.save(user);
        return user;
    }

    @PutMapping("/admin-add-user-role/{id}")
    public String adminAddUserRole(@RequestBody Role role, @PathVariable Long id){
        User user = userService.findById(id);
        Role result = roleService.findByRole(role.getRole());
        if(result == null)
            throw new RoleNotFoundException("There is no role named: " + role.getRole());
        if(user == null)
            throw new UserNotFoundException("Cant find user with id: " + id);
        for(Role r : user.getRoles())
            if (r.getRole().equals(role.getRole()))
                throw new RuntimeException("Role " + role.getRole() + " already added to user with id: " + id);
        user.addRole(result);
        userService.save(user);
        return "Modified user with id: " + id;
    }

    @GetMapping("/users/{id}")
    public User findById(@PathVariable Long id){
        User user = userService.findById(id);
        if(user == null)
            throw new UserNotFoundException("Cant find user with id: " + id);
        return user;
    }

    @PostMapping("/users")
    public User updateUser(@RequestBody User user){
        userService.save(user);
        return user;
    }

    @DeleteMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id){
        User user = userService.findById(id);
        if(user == null)
            throw new UserNotFoundException("Cant find User with id: " + id);
        userService.deleteUserById(id);
        return "Deleted User with id: " + id;
    }

    @GetMapping("/logged")
    public String logged(){
        return "You are logged in";
    }
}
