package co.edu.uptc.propertymanagement.controllers;

import co.edu.uptc.propertymanagement.DTOs.GenericCardDTO;
import co.edu.uptc.propertymanagement.DTOs.PropertyGroupDTO;
import co.edu.uptc.propertymanagement.services.PropertyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/Property-group")
public class PropertyGroupController {

    @Autowired
    private PropertyGroupService propertyGroupService;

    @PostMapping("/Create")
    public ResponseEntity<HashMap<String,Object>> createPropertyGroup(@RequestBody PropertyGroupDTO propertyGroup) {
        return ResponseEntity.ok(propertyGroupService.createPropertyGroup(propertyGroup));
    }

}
