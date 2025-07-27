package com.example.bankcards.util.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Violation {

    private String field;

    private String error;
}
