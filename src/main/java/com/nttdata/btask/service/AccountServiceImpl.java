package com.nttdata.btask.service;

import com.nttdata.btask.interfaces.AccountService;
import com.nttdata.domain.contract.AccountRepository;
import com.nttdata.domain.models.AccountDto;
import com.nttdata.domain.models.TransferDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class AccountServiceImpl implements AccountService {
  private final AccountRepository accountRepository;

  public AccountServiceImpl(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public Multi<AccountDto> list() {
    return accountRepository.list();
  }

  @Override
  public Multi<AccountDto> findByNroAccount(AccountDto accountDto) {
    return accountRepository.findByNroAccount(accountDto);
  }

  @Override
  public Uni<AccountDto> addAccount(AccountDto accountDto) {
    return accountRepository.addAccount(accountDto);
  }

  @Override
  public Uni<AccountDto> updateAccount(AccountDto accountDto) {
    return accountRepository.updateAccount(accountDto);
  }

  @Override
  public Uni<AccountDto> deleteAccount(AccountDto accountDto) {
    return accountRepository.deleteAccount(accountDto);
  }

  @Override
  public Uni<AccountDto> updateAccountToDeposit(AccountDto accountDto) {
    return accountRepository.updateAccountToDeposit(accountDto);
  }

  @Override
  public Uni<AccountDto> updateAccountToWithdrawal(AccountDto accountDto) {
    return accountRepository.updateAccountToWithdrawal(accountDto);
  }

  @Override
  public Uni<TransferDto> updateTransfer(TransferDto transferDto) {
    return accountRepository.updateTransfer(transferDto);
  }

  @Override
  public Uni<List<AccountDto>> getAccountByCustomer(AccountDto accountDto) {
    return accountRepository.getAccountByCustomer(accountDto);
  }
}
