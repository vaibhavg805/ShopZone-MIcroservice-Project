package com.vaibhav.user_service.controller;

import com.vaibhav.user_service.dto.AddressRequestDto;
import com.vaibhav.user_service.dto.AddressResponseDto;
import com.vaibhav.user_service.dto.AddressUpdateRequestDto;
import com.vaibhav.user_service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/api")
@RequestMapping("/user/address")
public class AddressController {

    private AddressService addressService;

    public AddressController(AddressService addressService){
        this.addressService=addressService;
    }

    @PostMapping("/users/address")
    public ResponseEntity<?> saveAddressDetails(@Valid @RequestBody AddressRequestDto requestDto){
        AddressResponseDto responseDto = addressService.saveAddressData(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/users/address")
    public ResponseEntity<List<AddressResponseDto>> getAll() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @PutMapping("/users/address/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> updateAddressDetails(@PathVariable Long id, @RequestBody AddressUpdateRequestDto
                                                  updateRequestDto){
        return ResponseEntity.ok(addressService.updateAddress(id, updateRequestDto));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.softDeleteAddress(id);
        return ResponseEntity.noContent().build();
    }


}
