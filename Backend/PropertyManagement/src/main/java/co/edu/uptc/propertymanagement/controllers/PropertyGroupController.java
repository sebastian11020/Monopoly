package co.edu.uptc.propertymanagement.controllers;

import co.edu.uptc.propertymanagement.DTOs.PropertyGroupDTO;
import co.edu.uptc.propertymanagement.services.PropertyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/Property-group")
public class PropertyGroupController {

    @Autowired
    private PropertyGroupService propertyGroupService;

    @PostMapping("/Create")
    public ResponseEntity<HashMap<String,Object>> createPropertyGroup(PropertyGroupDTO propertyGroup) {
        return ResponseEntity.ok(propertyGroupService.createPropertyGroup(propertyGroup));
    }

}
