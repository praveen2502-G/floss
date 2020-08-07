package com.example.repository;


import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.example.model.Tariff;

import java.util.Optional;

public interface TariffCrudRepository extends Repository<Tariff, Long> {

    Optional<Tariff> findByid(Long id);

    Tariff findTopByOrderByIdDesc();

    @RestResource
    Tariff save(Tariff tariff);
}
