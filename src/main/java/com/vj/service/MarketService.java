package com.vj.service;

import com.vj.model.entity.Market;

import java.time.LocalDate;

public interface MarketService {

    LocalDate getTradeDate(Market market);
}
