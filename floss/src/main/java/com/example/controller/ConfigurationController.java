package com.example.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.model.Configuration;
import com.example.repository.ConfigurationRepository;

@RestController
public class ConfigurationController {

 private ConfigurationRepository configurationRepository;
 
 @Autowired
  public ConfigurationController(ConfigurationRepository configurationRepository) {
	this.configurationRepository = configurationRepository;
  }

@PostMapping("/capacity-data")
  public ResponseEntity<Void> postNewTariff(@RequestBody Configuration configuration) {
      configurationRepository.save(configuration);
      return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/capacity-data")
  public ResponseEntity<Configuration> getLatestCapacity() {
	  Configuration configuration = configurationRepository.findTop10ByOrderByIdDesc();
      return new ResponseEntity<>(configuration , HttpStatus.OK);
  }
}
