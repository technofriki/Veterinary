package com.mokah.veterinary.features.adresses.services;

import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import com.mokah.veterinary.features.adresses.dto.AddressResponse;

import java.util.List;

public interface AddressServiceInterface {
    AddressResponse create (AddressRequest request);
    List<AddressResponse> findAll();
    AddressResponse findById (Long id);
    AddressResponse update (Long id, AddressRequest request);
    void delete(Long id);
    AddressResponse findByStreet (String street);
}
