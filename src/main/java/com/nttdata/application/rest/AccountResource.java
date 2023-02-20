package com.nttdata.application.rest;

import com.nttdata.btask.interfaces.AccountService;
import com.nttdata.domain.models.AccountDto;
import com.nttdata.domain.models.TransferDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
  private final AccountService accountService;

  public AccountResource(AccountService accountService) {
    this.accountService = accountService;
  }

  @GET
  public Multi<AccountDto> findAll() {
    return accountService.list();
  }

  @POST
  public Uni<AccountDto> add(AccountDto accountDto) {
    return accountService.addAccount(accountDto);
  }

  @PUT
  public Uni<AccountDto> updateCustomer(AccountDto accountDto) {
    return accountService.updateAccount(accountDto);
  }

  @POST
  @Path("/search")
  public Multi<AccountDto> findByNroDocument(AccountDto accountDto) {
    return accountService.findByNroAccount(accountDto);
  }

  @DELETE
  public Uni<AccountDto> deleteCustomer(AccountDto accountDto) {
    return accountService.deleteAccount(accountDto);
  }


  @POST
  @Path("/deposit")
  public Uni<AccountDto> updateAccountAmount(AccountDto accountDto) {
    return accountService.updateAccountToDeposit(accountDto);
  }

  @POST
  @Path("/withdrawal")
  public Uni<AccountDto> updateAccountAmountWithdrawal(AccountDto accountDto) {
    return accountService.updateAccountToWithdrawal(accountDto);
  }

  @POST
  @Path("/transfer")
  public Uni<TransferDto> transferBetweenAccountOwn(TransferDto transferDto) {
    return accountService.updateTransfer(transferDto);
  }

  @POST
  @Path("/byCustomer")
  public Uni<List<AccountDto>> getAccountByCustomer(AccountDto accountDto) {
    return accountService.getAccountByCustomer(accountDto);
  }

}
