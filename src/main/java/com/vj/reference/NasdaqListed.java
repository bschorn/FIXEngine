package com.vj.reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NASDAQ's listed securities
 */
public class NasdaqListed {

    private static final Logger log = LoggerFactory.getLogger(NasdaqListed.class);

    private enum Fields {
        Symbol, Security_Name, Market_Category, Test_Issue, Financial_Status, Round_Lot_Size, ETF, NextShares
    }

    private static final int FIELD_CT = Fields.values().length;

    public static class Item {

        private final String[] data;

        public static Item create(String input) throws Exception {
            String[] data = input.split("[|]");
            if (data.length > 0 && (data[0].equalsIgnoreCase("symbol") || data[0].equalsIgnoreCase("file"))) {
                return null;
            }
            if (data.length != FIELD_CT) {
                throw new Exception(data.length + " fields != " + FIELD_CT);
            }
            return new Item(data);
        }

        private Item(String[] data) {
            this.data = data;
        }

        public String symbol() {
            return data[Fields.Symbol.ordinal()];
        }

        public String securityName() {
            return data[Fields.Security_Name.ordinal()];
        }

        public int roundLotSize() {
            return Integer.valueOf(data[Fields.Round_Lot_Size.ordinal()]);
        }
    }

    private Map<String, Item> map = new HashMap<>();

    public NasdaqListed() throws IOException {
        load(NasdaqListed.class.getResourceAsStream("/nasdaqlisted.txt"));
    }

    public List<String> getSymbols() {
        return map.keySet().stream().sorted().collect(Collectors.toList());
    }

    private void load(InputStream inputStream) throws IOException {
        try {
            Map<String, Item> map = new HashMap<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            while (line != null) {
                try {
                    Item item = Item.create(line);
                    if (item == null) {
                        continue;
                    }
                    if (map.containsKey(item.symbol())) {
                        throw new Exception(item.symbol() + " is a duplicate entry.");
                    }
                    map.put(item.symbol(), item);
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                } finally {
                    line = reader.readLine();
                }
            }
            this.map = map;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
