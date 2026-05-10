package com.mokah.veterinary.features.adresses.controller;

import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import com.mokah.veterinary.features.adresses.dto.AddressResponse;
import com.mokah.veterinary.features.adresses.services.AddressServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressServiceInterface addressService;
    public AddressController(AddressServiceInterface addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity <List<AddressResponse>> findAllAddresses(){
        List<AddressResponse> responseList = addressService.findAll();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id){
        AddressResponse response = addressService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/street/{street}")
    public ResponseEntity<AddressResponse> getAddressByStreet(@PathVariable String street){
        AddressResponse response = addressService.findByStreet(street);
    return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest){
        AddressResponse created = addressService.create(addressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long id, @RequestBody AddressRequest addressRequest){
        AddressResponse updated = addressService.update(id, addressRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AddressResponse> deleteAddress(@PathVariable Long id){
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
