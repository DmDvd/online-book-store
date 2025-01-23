package com.example.library.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    Specification<T> getSpecification(String[] params);
}
