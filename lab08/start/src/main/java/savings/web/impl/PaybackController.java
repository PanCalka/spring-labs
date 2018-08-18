package savings.web.impl;

import static common.json.MoneyModule.moneyPropertyEditor;
import static org.joda.time.DateTime.now;

import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import savings.model.PaybackConfirmation;
import savings.model.Purchase;
import savings.service.PaybackBookKeeper;

// TODO #1 mark this class as a controller component mapped to '/payback' url
@Controller
@RequestMapping(value = "/payback")
public class PaybackController {

    private final PaybackBookKeeper paybackBookKeeper;

    @Autowired
    public PaybackController(PaybackBookKeeper paybackBookKeeper) {
        this.paybackBookKeeper = paybackBookKeeper;
    }

    @InitBinder
    public void replaceMeWithWebDataBinderConfiguration(WebDataBinder binder) {
        binder.registerCustomEditor(Money.class, moneyPropertyEditor);
    }

    /**
     * Notice that by convention this method returns a 'purchaseForm' model attribute
     * and looks up a view by id 'payback/new' to render in response.
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public PurchaseForm purchase() {
        PurchaseForm purchaseForm = new PurchaseForm();
        purchaseForm.setCreditCardNumber("1234123412341234");
        purchaseForm.setMerchantNumber("1234567890");
        return purchaseForm;
    }

    /**
     * Analogous to the above, this method returns a 'paybackConfirmation' model attribute
     * and looks up a view by id 'payback/confirm' to render in response.
     */
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public PaybackConfirmation paybackConfirmation(@ModelAttribute PurchaseForm purchaseForm) {
        return paybackBookKeeper.registerPaybackFor(new Purchase(
                purchaseForm.transactionValue,
                purchaseForm.creditCardNumber,
                purchaseForm.merchantNumber,
                now()));
    }

    public static class PurchaseForm {

        public String creditCardNumber;

        public String merchantNumber;

        public Money transactionValue;

        public String getCreditCardNumber() {
            return creditCardNumber;
        }

        public void setCreditCardNumber(String creditCardNumber) {
            this.creditCardNumber = creditCardNumber;
        }

        public String getMerchantNumber() {
            return merchantNumber;
        }

        public void setMerchantNumber(String merchantNumber) {
            this.merchantNumber = merchantNumber;
        }

        public Money getTransactionValue() {
            return transactionValue;
        }

        public void setTransactionValue(Money transactionValue) {
            this.transactionValue = transactionValue;
        }
    }
}
