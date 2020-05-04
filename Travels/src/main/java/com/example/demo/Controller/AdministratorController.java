package com.example.demo.Controller;

import com.example.demo.Entity.Administrator;
import com.example.demo.Service.AdministratorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;

    @PostMapping(value = "admin")
    public Administrator createAdmin(@RequestBody Administrator administrator) throws JsonProcessingException {
        return administratorService.createAdmin(administrator);
    }

    @GetMapping(value = "admin")
    public List<Administrator> getAdminAll() throws JsonProcessingException{
        return administratorService.getAdminAll();
    }

    @GetMapping(value = "admin/query")
    public Administrator getUser(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "id", required = false) Long id) throws JsonProcessingException{
        return administratorService.getAdmin(username,id);
    }

    @PostMapping(value = "admin/deleteAdmin")
    public void deleteUser(@RequestBody Administrator administrator) throws JsonProcessingException{
        administratorService.deleteAdmin(administrator);
    }
}
