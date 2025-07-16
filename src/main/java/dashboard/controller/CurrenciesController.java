package dashboard.controller;

import dashboard.dto.fe.ConvertRateFeDto;
import dashboard.dto.fe.SupportedCurrenciesFeDto;
import dashboard.dto.fe.UpdateSavedRatesFeDto;
import dashboard.service.CurrenciesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exchange")
@RequiredArgsConstructor
public class CurrenciesController {
    private final CurrenciesService currenciesService;

    @GetMapping("/get-currencies-list")
    public List<SupportedCurrenciesFeDto> getCurrenciesList() {
        return currenciesService.getCurrenciesList();
    }

    @GetMapping("/get-exchange-rate")
    public ConvertRateFeDto getExchangeRate(@RequestBody ConvertRateFeDto request) {
        return currenciesService.getTheRate(request.from(), request.to());
    }

    @GetMapping("/get-saved-rates")
    public UpdateSavedRatesFeDto getSavedRates() {
        return currenciesService.getSavedRates();
    }

    @DeleteMapping("/delete-rate/{id}")
    public void deleteRate(@PathVariable("id") Long id) {
        currenciesService.deleteSavedRate(id);
    }
}
