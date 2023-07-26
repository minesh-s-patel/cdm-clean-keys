# cdm-clean-keys
Utility to clean global keys in CDM JSON

Quick utility that will remove keys from a CDM JSON Object.



```java

    // Load some data with global references
    ObjectMapper objectMapper = RosettaObjectMapper.getNewMinimalRosettaObjectMapper();
    BusinessEvent eventWithGlobalKeys = objectMapper.readValue(uri.toURL(), BusinessEvent.class);
    
    // Example for removing all global keys for a `BusinessEvent`
    ReferenceCleaner<BusinessEvent> referenceCleaner1 = new ReferenceCleaner<>(BusinessEvent.class);
    BusinessEvent eventWithNoGlobalKeys = referenceCleaner.removeUnusedReferences(eventWithGlobalKeys);

    // Example for removing all global keys for a `BusinessEvent` but keep the key for Trade
    ReferenceCleaner<BusinessEvent> referenceCleaner2 = new ReferenceCleaner<>(BusinessEvent.class, Trade.class);
    BusinessEvent eventWithOnlyTradeGlobalKeys = referenceCleaner2.removeUnusedReferences(eventWithGlobalKeys);

    // Example for removing all global keys for a `BusinessEvent` but keep the key for TradeState and Party
    ReferenceCleaner<BusinessEvent> referenceCleaner3 = new ReferenceCleaner<>(BusinessEvent.class, TradeState.class, Party.class);
    BusinessEvent eventWithOnlyTradeStateAndPartyGlobalKeys = referenceCleaner2.removeUnusedReferences(eventWithGlobalKeys);
```