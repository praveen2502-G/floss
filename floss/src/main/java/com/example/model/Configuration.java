package com.example.model;
import javax.persistence.*;

@Entity
@Table(name = "CONFIGURATIONS")
public class Configuration {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "VALUE")
    private Integer value;

    public Long getId() {
        return id;
    }
    
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}