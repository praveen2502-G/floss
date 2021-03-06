package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.common.collect.ImmutableMap;

import com.example.model.Configuration;
import com.example.model.Registration;
import com.example.model.Tariff;
import com.example.repository.ConfigurationRepository;
import com.example.repository.RegistrationRepository;
import com.example.repository.TariffCrudRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


@RestController
public class RegistrationController {

    private ConfigurationRepository configurationRepository;

    private RegistrationRepository registrationRepository;

    private TariffCrudRepository tariffCrudRepository;

    @Autowired
    public RegistrationController(ConfigurationRepository configurationRepository
                                    , RegistrationRepository registrationRepository
                                    , TariffCrudRepository tariffCrudRepository) {
        this.configurationRepository = configurationRepository;
        this.registrationRepository = registrationRepository;
        this.tariffCrudRepository = tariffCrudRepository;
    }

    @GetMapping("/slots-info")
    public Map<String, Integer> getSlotsInfo() {
        Optional<Configuration> conf = configurationRepository.findTopByOrderByIdDesc();
        List<Registration> registrations = registrationRepository.findAllByDepartureIsNull();
        return conf.<Map<String, Integer>>map(configuration ->
                ImmutableMap.of("capacity", configuration.getValue(), "occupied", registrations.size())).orElseGet(() -> ImmutableMap.of("error", 1));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerCar(@RequestBody Registration pRegistration) {
        if(registrationRepository.countByDepartureIsNull()
            .equals(configurationRepository.findTopByOrderByIdDesc().orElse(new Configuration()).getValue())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(pRegistration.getRegistrationPlate())
                .isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(pRegistration.getRegistrationPlate().length() > 12) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        registrationRepository.save(createRegistration(pRegistration.getRegistrationPlate()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<ImmutableMap<String, String>> carFeeLookup(@RequestBody Registration pRegistration) {
        Optional<Registration> registrationOptional = registrationRepository
                .findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(pRegistration.getRegistrationPlate());
        if(registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            Optional<Tariff> tariff = tariffCrudRepository.findByid(registration.getTariffId());
            if(tariff.isPresent()) {
                LocalDateTime now = LocalDateTime.now();
                BigDecimal fee = calculatePrice(registration, tariff.get(), now);
                return new ResponseEntity<>(ImmutableMap.of("fee", fee.toString()
                        , "registrationPlate", registration.getRegistrationPlate()
                        , "arrival", registration.getArrival().toString()
                        , "departure", now.toString()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/unregister")
    public ResponseEntity<Void> unregisterCar(@RequestBody Registration pRegistration) {
        Optional<Registration> registrationOptional = registrationRepository
                .findTopByRegistrationPlateOrderByArrivalDesc(pRegistration.getRegistrationPlate());
        if(registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            registration.setDeparture(pRegistration.getDeparture());
            registration.setLocation("Chennai");
            registrationRepository.save(registration);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private Registration createRegistration(String registrationPlate) {
        Registration registration = new Registration();
        registration.setRegistrationPlate(registrationPlate);
        registration.setArrival(LocalDateTime.now());
        registration.setDeparture((LocalDateTime)null);
        registration.setTariffId(tariffCrudRepository.findTopByOrderByIdDesc().getId());
        return registration;
    }

    private BigDecimal calculatePrice(Registration registration, Tariff tariff, LocalDateTime now) {
        long hours = registration.getArrival().until(now, ChronoUnit.HOURS);
        long minutes = registration.getArrival().until(now, ChronoUnit.MINUTES) % 60;
        long seconds = registration.getArrival().until(now, ChronoUnit.SECONDS) % 60;
        BigDecimal hoursBigDecimal = new BigDecimal(hours);
        BigDecimal minutesBigDecimal = new BigDecimal(minutes);
        BigDecimal secondsBigDecimal = new BigDecimal(seconds);
        BigDecimal fee;
        if(hours < tariff.getBasicPeriod()) {
            fee = tariff.getBasicBid().multiply(hoursBigDecimal);
            fee = fee.add(tariff.getBasicBid().multiply(minutesBigDecimal).divide(new BigDecimal(60), RoundingMode.FLOOR));
            fee = fee.add(tariff.getBasicBid().multiply(secondsBigDecimal).divide(new BigDecimal(3600), RoundingMode.FLOOR));
        }
        else {
            BigDecimal extendedBigPeriod = hoursBigDecimal.subtract(new BigDecimal(tariff.getBasicPeriod()));
            fee = tariff.getBasicBid().multiply(new BigDecimal(tariff.getBasicPeriod()));
            fee = fee.add(tariff.getExtendedBid().multiply(extendedBigPeriod));
            fee = fee.add(tariff.getExtendedBid().multiply(minutesBigDecimal).divide(new BigDecimal(60), RoundingMode.FLOOR));
            fee = fee.add(tariff.getExtendedBid().multiply(secondsBigDecimal).divide(new BigDecimal(3600), RoundingMode.FLOOR));
        }

        return fee.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}