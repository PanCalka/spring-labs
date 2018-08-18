package savings.web.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import savings.model.PaybackConfirmation;
import savings.model.Purchase;
import savings.service.PaybackBookKeeper;

@Controller
@RequestMapping(value = "/api/payback")
public class PaybackRestController {

    private final PaybackBookKeeper paybackBookKeeper;

    @Autowired
    public PaybackRestController(PaybackBookKeeper paybackBookKeeper) {
        this.paybackBookKeeper = paybackBookKeeper;
    }

    // TODO #6 make this method deserialize request body into 'purchase' parameter
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PaybackConfirmation create(@RequestBody Purchase purchase) {
        return paybackBookKeeper.registerPaybackFor(purchase);
    }
}
