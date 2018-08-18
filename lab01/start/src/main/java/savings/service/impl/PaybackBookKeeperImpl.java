package savings.service.impl;

import org.joda.money.Money;
import savings.model.*;
import savings.repository.AccountRepository;
import savings.repository.MerchantRepository;
import savings.repository.PaybackRepository;
import savings.service.PaybackBookKeeper;

public class PaybackBookKeeperImpl implements PaybackBookKeeper {

    AccountRepository accountRepository;

    MerchantRepository merchantRepository;

    PaybackRepository paybackRepository;

    public PaybackBookKeeperImpl(AccountRepository accountRepository, MerchantRepository merchantRepository, PaybackRepository paybackRepository) {
        this.accountRepository = accountRepository;
        this.merchantRepository = merchantRepository;
        this.paybackRepository = paybackRepository;
    }

    @Override
    public PaybackConfirmation registerPaybackFor(Purchase purchase) {
        Account account = accountRepository.findByCreditCard(purchase.getCreditCardNumber());
        Merchant merchant = merchantRepository.findByNumber(purchase.getMerchantNumber());
        Money moneyTotal = merchant.calculatePaybackFor(account, purchase);
        AccountIncome accountIncome = account.addPayback(moneyTotal);
        accountRepository.update(account);
        return paybackRepository.save(accountIncome, purchase);

    }
}
