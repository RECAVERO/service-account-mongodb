package com.nttdata.infraestructure.entity;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
  private int idTypeAccount;
  private String numberAccount;
  private double amount;
  private String registrationDate;
  private int idTypeCustomer;
  private Long nroDocument;
  private String created_datetime;
  private String updated_datetime;
  private String active;
}
