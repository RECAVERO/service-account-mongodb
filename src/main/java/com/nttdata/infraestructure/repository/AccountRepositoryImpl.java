package com.nttdata.infraestructure.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.domain.contract.AccountRepository;
import com.nttdata.domain.models.AccountDto;
import com.nttdata.domain.models.TransferDto;
import com.nttdata.infraestructure.entity.Account;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.enterprise.context.ApplicationScoped;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@ApplicationScoped
public class AccountRepositoryImpl implements AccountRepository {

  private final ReactiveMongoClient reactiveMongoClient;

  public AccountRepositoryImpl(ReactiveMongoClient reactiveMongoClient) {
    this.reactiveMongoClient = reactiveMongoClient;
  }

  @Override
  public Multi<AccountDto> list() {
    return getCollection().find().map(doc->{
      AccountDto accountDto = new AccountDto();
      accountDto.setId(doc.getString("id"));
      accountDto.setIdTypeAccount(doc.getInteger("idTypeAccount"));
      accountDto.setNumberAccount(doc.getString("numberAccount"));
      accountDto.setAmount(doc.getDouble("amount"));
      accountDto.setRegistrationDate(doc.getString("registrationDate"));
      accountDto.setIdTypeCustomer(doc.getInteger("idTypeCustomer"));
      accountDto.setNroDocument(doc.getLong("nroDocument"));
      accountDto.setCreated_datetime(doc.getString("created_datetime"));
      accountDto.setUpdated_datetime(doc.getString("updated_datetime"));
      accountDto.setActive(doc.getString("active"));
      return accountDto;
    }).filter(account->{
      return account.getActive().equals("S");
    });
  }


  @Override
  public Multi<AccountDto> findByNroAccount(AccountDto accountDto) {
    ReactiveMongoDatabase database = reactiveMongoClient.getDatabase("accounts");
    ReactiveMongoCollection<Document> collection = database.getCollection("account");


    return collection
        .find(new Document("numberAccount", accountDto.getNumberAccount())).map(doc->{
          AccountDto account = new AccountDto();
          account.setId(doc.getString("id"));
          account.setIdTypeAccount(doc.getInteger("idTypeAccount"));
          account.setNumberAccount(doc.getString("numberAccount"));
          account.setAmount(doc.getDouble("amount"));
          account.setRegistrationDate(doc.getString("registrationDate"));
          account.setIdTypeCustomer(doc.getInteger("idTypeCustomer"));
          account.setNroDocument(doc.getLong("nroDocument"));
          account.setCreated_datetime(doc.getString("created_datetime"));
          account.setUpdated_datetime(doc.getString("updated_datetime"));
          account.setActive(doc.getString("active"));
          return account;
        }).filter(s->s.getActive().equals("S"));
  }

  @Override
  public Uni<AccountDto> addAccount(AccountDto accountDto) {

    Document document = new Document()
        .append("idTypeAccount", accountDto.getIdTypeAccount())
        .append("numberAccount", accountDto.getNumberAccount())
        .append("amount", accountDto.getAmount())
        .append("registrationDate", this.getDateNow())
        .append("idTypeCustomer", accountDto.getIdTypeCustomer())
        .append("nroDocument", accountDto.getNroDocument())
        .append("created_datetime", this.getDateNow())
        .append("updated_datetime", this.getDateNow())
        .append("active", "S");


    return getCollection().insertOne(document).replaceWith(accountDto);
  }

  @Override
  public Uni<AccountDto> updateAccount(AccountDto accountDto) {
    Bson filter = eq("numberAccount", accountDto.getNumberAccount());
    Bson update = combine(
                          set("idTypeAccount", accountDto.getIdTypeAccount()),
                          set("amount", accountDto.getAmount()),
                          set("idTypeCustomer", accountDto.getIdTypeCustomer()),
                          set("updated_datetime", this.getDateNow())
                          );
    return getCollection().updateMany(filter,update).replaceWith(accountDto);
  }

  @Override
  public Uni<AccountDto> deleteAccount(AccountDto accountDto) {
    Bson filter = eq("numberAccount", accountDto.getNumberAccount());
    Bson update = set("active", "N");
    return getCollection().updateMany(filter,update).replaceWith(accountDto);
  }

  @Override
  public Uni<AccountDto> updateAccountToDeposit(AccountDto accountDto) {
    ReactiveMongoDatabase database = reactiveMongoClient.getDatabase("accounts");
    ReactiveMongoCollection<Document> collection = database.getCollection("account");

    Bson filter = eq("numberAccount", accountDto.getNumberAccount());
    Bson update = set("amount", accountDto.getAmount());

    return collection.find(filter).call(c->{
      AccountDto dd = new AccountDto();
      ReactiveMongoDatabase database1 = reactiveMongoClient.getDatabase("accounts");
      ReactiveMongoCollection<Document> collection1 = database1.getCollection("account");
      Bson updates = combine(
          set("amount", accountDto.getAmount() + c.getDouble("amount")),
          set("updated_datetime", this.getDateNow())
      );
      Bson filters = eq("numberAccount", accountDto.getNumberAccount());
      return collection1.updateOne(filters,updates);
    }).onItem().transform(doc -> {
      AccountDto account = new AccountDto();
      account.setId(doc.getString("id"));
      account.setIdTypeAccount(doc.getInteger("idTypeAccount"));
      account.setNumberAccount(doc.getString("numberAccount"));
      account.setAmount(doc.getDouble("amount") + accountDto.getAmount());
      account.setRegistrationDate(doc.getString("registrationDate"));
      account.setIdTypeCustomer(doc.getInteger("idTypeCustomer"));
      account.setNroDocument(doc.getLong("nroDocument"));
      account.setCreated_datetime(doc.getString("created_datetime"));
      account.setUpdated_datetime(this.getDateNow());
      account.setActive(doc.getString("active"));
      return account;
    }).toUni();
  }

  @Override
  public Uni<AccountDto> updateAccountToWithdrawal(AccountDto accountDto) {
    ReactiveMongoDatabase database = reactiveMongoClient.getDatabase("accounts");
    ReactiveMongoCollection<Document> collection = database.getCollection("account");

    Bson filter = eq("numberAccount", accountDto.getNumberAccount());
    Bson update = set("amount", accountDto.getAmount());

    return collection.find(filter).call(c->{
      AccountDto dd = new AccountDto();
      ReactiveMongoDatabase database1 = reactiveMongoClient.getDatabase("accounts");
      ReactiveMongoCollection<Document> collection1 = database1.getCollection("account");
      Bson updates = combine(
          set("amount", c.getDouble("amount") - accountDto.getAmount()),
          set("updated_datetime", this.getDateNow())
      );
      Bson filters = eq("numberAccount", accountDto.getNumberAccount());
      return collection1.updateOne(filters,updates);
    }).onItem().transform(doc -> {
      AccountDto account = new AccountDto();
      account.setId(doc.getString("id"));
      account.setIdTypeAccount(doc.getInteger("idTypeAccount"));
      account.setNumberAccount(doc.getString("numberAccount"));
      account.setAmount(doc.getDouble("amount") - accountDto.getAmount());
      account.setRegistrationDate(doc.getString("registrationDate"));
      account.setIdTypeCustomer(doc.getInteger("idTypeCustomer"));
      account.setNroDocument(doc.getLong("nroDocument"));
      account.setCreated_datetime(doc.getString("created_datetime"));
      account.setUpdated_datetime(this.getDateNow());
      account.setActive(doc.getString("active"));
      return account;
    }).toUni();
  }

  @Override
  public Uni<TransferDto> updateTransfer(TransferDto transferDto) {

    ReactiveMongoDatabase database = reactiveMongoClient.getDatabase("accounts");
    ReactiveMongoCollection<Document> collection = database.getCollection("account");

    Bson filter = eq("numberAccount", transferDto.getNumberAccountOrigin());


    return collection.find(filter).call(c->{
      AccountDto dd = new AccountDto();
      ReactiveMongoDatabase database1 = reactiveMongoClient.getDatabase("accounts");
      ReactiveMongoCollection<Document> collection1 = database1.getCollection("account");
      Bson updates = combine(
          set("amount", c.getDouble("amount") - transferDto.getAmount()),
          set("updated_datetime", this.getDateNow())
      );
      Bson filters = eq("numberAccount", transferDto.getNumberAccountOrigin());
      return collection1.updateOne(filters,updates);
    }).call(d->{
      AccountDto dd = new AccountDto();
      ReactiveMongoDatabase database2 = reactiveMongoClient.getDatabase("accounts");
      ReactiveMongoCollection<Document> collection2 = database2.getCollection("account");
      Bson updates = combine(
          set("amount", d.getDouble("amount") + transferDto.getAmount()),
          set("updated_datetime", this.getDateNow())
      );
      Bson filters = eq("numberAccount", transferDto.getNumberAccountDestination());
      return collection2.updateOne(filters,updates);
    }).onItem().transform(doc -> {
      TransferDto t1 = new TransferDto();
      t1.setNumberAccountOrigin(transferDto.getNumberAccountOrigin());
      t1.setNumberAccountDestination(transferDto.getNumberAccountDestination());
      t1.setAmount(transferDto.getAmount());
      return t1;
    }).toUni();
  }

  @Override
  public Uni<List<AccountDto>> getAccountByCustomer(AccountDto accountDto) {
    ReactiveMongoDatabase database = reactiveMongoClient.getDatabase("accounts");
    ReactiveMongoCollection<Document> collection = database.getCollection("account");
    List<AccountDto> list = new ArrayList<>();



    return collection
        .find(new Document("nroDocument", accountDto.getNroDocument())).map(doc->{
          AccountDto account = new AccountDto();
          account.setId(doc.getString("id"));
          account.setIdTypeAccount(doc.getInteger("idTypeAccount"));
          account.setNumberAccount(doc.getString("numberAccount"));
          account.setAmount(doc.getDouble("amount"));
          account.setRegistrationDate(doc.getString("registrationDate"));
          account.setIdTypeCustomer(doc.getInteger("idTypeCustomer"));
          account.setNroDocument(doc.getLong("nroDocument"));
          account.setCreated_datetime(doc.getString("created_datetime"));
          account.setUpdated_datetime(doc.getString("updated_datetime"));
          account.setActive(doc.getString("active"));
          return account;
        }).filter(s->s.getActive().equals("S")).collect().asList();

  }


  private static String getDateNow(){
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return formatter.format(date).toString();
  }

  public static Account mapToEntity(Object accountDto) {
    return new ObjectMapper().convertValue(accountDto, Account.class);
  }
  public static AccountDto mapToDto(Object accountDto) {
    return new ObjectMapper().convertValue(accountDto, AccountDto.class);
  }

  public static AccountDto mapToDomain(Account Account) {
    return new ObjectMapper().convertValue(Account, AccountDto.class);
  }

  private ReactiveMongoCollection<Document> getCollection() {
    return reactiveMongoClient.getDatabase("accounts").getCollection("account");
  }

}
