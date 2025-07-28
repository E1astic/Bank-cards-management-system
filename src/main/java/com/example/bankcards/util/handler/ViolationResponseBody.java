package com.example.bankcards.util.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ViolationResponseBody {

    private List<Violation> violations;
}
