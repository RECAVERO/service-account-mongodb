package com.nttdata.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
  private String id;
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
