package org.sam.istoeproject;

import java.time.LocalDate;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String cpf,
        String phone,
        LocalDate nascimento
) {}
