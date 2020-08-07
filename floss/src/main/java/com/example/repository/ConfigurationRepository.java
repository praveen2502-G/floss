package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.example.model.Configuration;


public interface ConfigurationRepository extends Repository<Configuration, Long> {

   List<Configuration> findByValue(Integer capacity);
    
   Configuration findTop10ByOrderByIdDesc();
   
   Optional<Configuration> findTopByOrderByIdDesc();
  
    @RestResource
    Configuration save(Configuration Configuration);
}