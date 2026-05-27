package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.entity.PetEntity;
import com.mokah.veterinary.features.pets.mapper.PetMapper;
import com.mokah.veterinary.features.pets.repository.PetRepository;
import com.mokah.veterinary.features.users.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService implements PetServiceInterface{

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final UserMapper userMapper;

    public PetService(PetRepository petRepository, PetMapper petMapper, UserMapper userMapper) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
        this.userMapper = userMapper;
    }


    @Override
    public List<PetResponse> findAll(){
    return petRepository.findAll().stream()
            .map(petMapper::toResponse)
            .collect(Collectors.toList());
    }
    @Override
    public PetResponse findById(Long id){
        PetEntity entity = petRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));
        return petMapper.toResponse(entity);
    }

    @Override
    public PetResponse findByName(String name){
        PetEntity entity = petRepository.findAll().stream()
                .filter(n-> n.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("User not found with name: "+name));
        return petMapper.toResponse(entity);
    }

    @Override
    public PetResponse create (PetRequest request){
        PetEntity entity = petMapper.toEntity(request);
        PetEntity created = petRepository.save(entity);
        return petMapper.toResponse(created);
    }

    @Override
    public PetResponse update(Long id, PetRequest request){
        PetEntity existingEntity = petRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));

        PetEntity mapperPet = petMapper.toEntity(request);
        existingEntity.setName(mapperPet.getName());
        existingEntity.setBirthDate(mapperPet.getBirthDate());
        existingEntity.setAnimalType(mapperPet.getAnimalType());
        existingEntity.setBreed(mapperPet.getBreed());

        PetEntity updated = petRepository.save(existingEntity);
        return petMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id){
        PetEntity entity = petRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));
        petRepository.delete(entity);

    }
}
