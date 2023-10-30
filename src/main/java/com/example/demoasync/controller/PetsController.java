package com.example.demoasync.controller;

import com.example.demoasync.entity.Pet;
import com.example.demoasync.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class PetsController {
    @Autowired
    private PetService petService;

    @PostMapping(value = "/pets",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},produces = "application/json")
    public ResponseEntity savePets(@RequestParam(value="files")MultipartFile[] files) throws Exception {
        for(MultipartFile file:files){
            petService.savePets(file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/pets",produces = "application/json")
    public CompletableFuture<ResponseEntity> findAllPets(){
       return petService.findAllPets().thenApply(ResponseEntity::ok);
    }

    @GetMapping(value = "/getPetsByThreads",produces = "application/json")
    public ResponseEntity getPets(){
        CompletableFuture<List<Pet>> future=petService.findAllPets();
        CompletableFuture<List<Pet>> future2=petService.findAllPets();
        CompletableFuture<List<Pet>> future3=petService.findAllPets();
        CompletableFuture.allOf(future,future2,future3).join();
        return ResponseEntity.status(HttpStatus.OK).build();

    }
}
