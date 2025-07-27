package com.example.bankcards.util.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ViolationResponseBody {

    private List<Violation> violations;
}
