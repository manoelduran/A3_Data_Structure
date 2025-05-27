package com.example.cinemaapi.dto;

import com.example.cinemaapi.model.CustomerType;
import lombok.Data;

@Data
public class CreateCustomerDTO {
    private String name;
    private CustomerType type;
}
