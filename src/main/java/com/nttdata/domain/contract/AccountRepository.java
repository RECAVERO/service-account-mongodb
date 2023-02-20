package com.nttdata.domain.contract;

import com.nttdata.domain.models.AccountDto;
import com.nttdata.domain.models.TransferDto;
import com.nttdata.infraestructure.entity.Account;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface AccountRepository {
  Multi<AccountDto> list();

  Multi<AccountDto> findByNroAccount(AccountDto accountDto);

  Uni<AccountDto> addAccount(AccountDto accountDto);

  Uni<AccountDto> updateAccount(AccountDto accountDto);

  Uni<AccountDto> deleteAccount(AccountDto accountDto);

  Uni<AccountDto> updateAccountToDeposit(AccountDto accountDto);

  Uni<AccountDto> updateAccountToWithdrawal(AccountDto accountDto);

  Uni<TransferDto> updateTransfer(TransferDto transferDto);

  Uni<List<AccountDto>> getAccountByCustomer(AccountDto accountDto);
}
