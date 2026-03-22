package com.vaibhav.user_service.serviceImpl;

import com.vaibhav.user_service.dto.AddressRequestDto;
import com.vaibhav.user_service.dto.AddressResponseDto;
import com.vaibhav.user_service.dto.AddressUpdateRequestDto;
import com.vaibhav.user_service.dto.AddressUpdateResponseDto;
import com.vaibhav.user_service.entity.Address;
import com.vaibhav.user_service.entity.User;
import com.vaibhav.user_service.exception.ResourceNotFoundException;
import com.vaibhav.user_service.exception.UnauthorizedException;
import com.vaibhav.user_service.repository.AddressRepository;
import com.vaibhav.user_service.repository.UserRepository;
import com.vaibhav.user_service.security.customservice.CustomUserDetails;
import com.vaibhav.user_service.service.AddressService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressServiceImplementation implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public AddressServiceImplementation(UserRepository userRepository,
                                        AddressRepository addressRepository){
        this.addressRepository = addressRepository;
        this.userRepository=userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAllAddresses() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Address> addresses = addressRepository.findByUserAndIsDeletedFalse(user);

        return addresses.stream()
                .map(address -> AddressResponseDto.builder()
                        .id(address.getId())
                        .addressLine1(address.getAddressLine1())
                        .city(address.getCity())
                        .isDefault(address.getIsDefault())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public AddressResponseDto saveAddressData(AddressRequestDto addressRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          String username =  authentication.getName();
          User user = userRepository.findByUsername(username).orElseThrow(() ->
                  new UsernameNotFoundException("User Not Found"));

        boolean isFirstAddress = addressRepository.countByUser(user) == 0;
          Address setAddress = Address.builder()
                  .user(user)
                  .addressLine1(addressRequestDto.getAddressLine1())
                  .addressLine2(addressRequestDto.getAddressLine2())
                  .city(addressRequestDto.getCity())
                  .state(addressRequestDto.getState())
                  .country(addressRequestDto.getCountry())
                  .pincode(addressRequestDto.getPincode())
                  .isDefault(isFirstAddress)
                  .build();

            Address savedData = addressRepository.save(setAddress);

            return AddressResponseDto.builder()
                    .id(savedData.getId())
                    .addressLine1(savedData.getAddressLine1())
                    .city(savedData.getCity())
                    .isDefault(savedData.getIsDefault())
                    .build();

    }

    @Override
    @Transactional
    public AddressUpdateResponseDto updateAddress(Long addressId, AddressUpdateRequestDto dto) {
        // fetch address
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found with id: " + addressId));

        // update only non-null fields (partial update)

        if (dto.getAddressLine1() != null)
            address.setAddressLine1(dto.getAddressLine1());

        if (dto.getAddressLine2() != null)
            address.setAddressLine2(dto.getAddressLine2());

        if (dto.getCity() != null)
            address.setCity(dto.getCity());

        if (dto.getState() != null)
            address.setState(dto.getState());

        if (dto.getCountry() != null)
            address.setCountry(dto.getCountry());

        if (dto.getPincode() != null)
            address.setPincode(dto.getPincode());

      //  special logic for default address
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            // make all other addresses false
            addressRepository.clearDefaultForUser(address.getUser().getId());
            address.setIsDefault(true);
        }

        return AddressUpdateResponseDto.builder()
                .id(address.getId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .pincode(address.getPincode())
                .isDefault(address.getIsDefault())
                .build();
    }

    @Override
    @Transactional
    public void softDeleteAddress(Long addressId) {
        Long userDetailsId = null;
        Address address = addressRepository.findByIdAndIsDeletedFalse(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
             userDetailsId = userDetails.getId();
        }

        if (!address.getUser().getId().equals(userDetailsId)) {
            throw new UnauthorizedException(
                    "You are not authorized to delete this address");
        }

        address.setDeleted(true);
        address.setDeletedAt(LocalDateTime.now());
        addressRepository.save(address);
    }
}
