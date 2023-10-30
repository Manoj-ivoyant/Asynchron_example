package com.example.demoasync.service;

import com.example.demoasync.entity.Pet;
import com.example.demoasync.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PetService {
    private static final Logger logger= LoggerFactory.getLogger(PetService.class);

    @Autowired
    private PetRepository petRepository;



    @Async
    public CompletableFuture<List<Pet>> savePets(MultipartFile multipartFile) throws Exception {
        long start=System.currentTimeMillis();
        List<Pet> pets=parseCSVFile(multipartFile);
        logger.info("saving list of pets of size {}",pets.size(),""+Thread.currentThread().getName());
        pets=petRepository.saveAll(pets);
        long end=System.currentTimeMillis();
        logger.info("Total time {}",(end-start));
        return CompletableFuture.completedFuture(pets);

    }

    @Async
    public CompletableFuture<List<Pet>> findAllPets(){
        logger.info("get list of pets by "+Thread.currentThread().getName());
        List<Pet> pets=petRepository.findAll();
        return CompletableFuture.completedFuture(pets);
    }

    private List<Pet> parseCSVFile(final MultipartFile file) throws Exception {
        final List<Pet> pets = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final Pet pet= new Pet();
                    pet.setName(data[0]);
                    pet.setBreed(data[1]);
                    pet.setMail(data[2]);
                    pets.add(pet);
                }
                return pets;
            }
        } catch (final IOException e) {
            logger.error("Failed to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }

}
