package com.vj.mock;

import com.vj.model.entity.Market;
import com.vj.service.MarketService;

import java.time.LocalDate;

public class MarketServiceImpl implements MarketService {
    @Override
    public LocalDate getTradeDate(Market market) {
        return LocalDate.now();
    }
}
