package savings.repository;

import java.util.List;

import savings.model.AccountIncome;
import savings.model.PaybackConfirmation;
import savings.model.Purchase;

// TODO #1: implement this repository to support basic CRUD operations as well as those defined below
public interface PaybackRepository {

    List<PaybackConfirmation> findByAccountNumber(String accountNumber);

    PaybackConfirmation save(AccountIncome income, Purchase purchase);

}
