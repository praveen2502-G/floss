package com.example.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import com.example.model.Tariff;
import com.example.repository.TariffCrudRepository;

import org.springframework.web.bind.annotation.*;


@RestController
public class TariffController {

  private TariffCrudRepository tariffCrudRepository;
  

  @Autowired
  public TariffController(TariffCrudRepository tariffCrudRepository) {
      this.tariffCrudRepository = tariffCrudRepository;
  }

  @PostMapping("/tariff-data")
  public ResponseEntity<Void> postNewTariff(@RequestBody Tariff tariff) {
      tariffCrudRepository.save(tariff);
      return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/tariff-data")
  public ResponseEntity<Tariff> getLatestTariff() {
      Tariff tariff = tariffCrudRepository.findTopByOrderByIdDesc();
      return new ResponseEntity<>(tariff, HttpStatus.OK);
  }
  
  
}
