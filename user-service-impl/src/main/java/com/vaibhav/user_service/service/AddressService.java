package com.vaibhav.user_service.service;

import com.vaibhav.user_service.dto.AddressRequestDto;
import com.vaibhav.user_service.dto.AddressResponseDto;
import com.vaibhav.user_service.dto.AddressUpdateRequestDto;
import com.vaibhav.user_service.dto.AddressUpdateResponseDto;

import java.util.List;

public interface AddressService {
    public List<AddressResponseDto> getAllAddresses();
    public AddressResponseDto saveAddressData(AddressRequestDto addressRequestDto);
    public AddressUpdateResponseDto updateAddress(Long id, AddressUpdateRequestDto addressUpdateRequestDto);
    public void softDeleteAddress(Long id);
}
