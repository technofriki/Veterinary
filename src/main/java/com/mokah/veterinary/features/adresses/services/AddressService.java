package com.mokah.veterinary.features.adresses.services;


import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import com.mokah.veterinary.features.adresses.dto.AddressResponse;
import com.mokah.veterinary.features.adresses.entity.AddressEntity;
import com.mokah.veterinary.features.adresses.mapper.AddressMapper;
import com.mokah.veterinary.features.adresses.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService implements AddressServiceInterface {

private final AddressRepository addressRepository;
private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    @Override
public List<AddressResponse> findAll(){
        return addressRepository.findAll().stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
}

@Override
public AddressResponse findById (Long id){
        AddressEntity entity = addressRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Address not found with id: "+id));
        return addressMapper.toResponse(entity);
}

@Override
public AddressResponse findByStreet (String street){
        AddressEntity entity = addressRepository.findAll().stream()
                .filter(s-> s.getStreet().equalsIgnoreCase(street))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Address not found with street name: "+street));
        return addressMapper.toResponse(entity);
}

@Override
public AddressResponse create (AddressRequest request){
        AddressEntity entity = addressMapper.toEntity(request);
        AddressEntity saved = addressRepository.save(entity);

        return addressMapper.toResponse(saved);
}

@Override
public void delete (Long id){
        AddressEntity entity = addressRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Address not found with id: "+id));
        addressRepository.delete(entity);
}

@Override
public AddressResponse update (Long id, AddressRequest request){
        AddressEntity existingEntity = addressRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Address not found with id: "+id));

        existingEntity.setStreet(request.getStreet());
        existingEntity.setStreetNumber(request.getStreetNumber());
        existingEntity.setCity(request.getCity());
        existingEntity.setProvince(request.getProvince());
        existingEntity.setCountry(request.getCountry());

        AddressEntity updated =  addressRepository.save(existingEntity);
        return addressMapper.toResponse(updated);
}
}
