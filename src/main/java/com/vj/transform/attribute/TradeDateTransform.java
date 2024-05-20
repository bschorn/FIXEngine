package com.vj.transform.attribute;

import quickfix.field.TradeDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TradeDateTransform implements AttributeTransform<quickfix.field.TradeDate,LocalDate> {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE;

    @Override
    public LocalDate inbound(TradeDate tradeDate) {
        return LocalDate.parse(tradeDate.getValue(), dateTimeFormatter);
    }

    @Override
    public TradeDate outbound(LocalDate localDate) {
        return new TradeDate(localDate.format(dateTimeFormatter));
    }
}
