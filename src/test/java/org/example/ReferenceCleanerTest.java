package org.example;

import cdm.base.staticdata.party.Party;
import cdm.event.common.BusinessEvent;
import cdm.event.common.Trade;
import cdm.event.common.TradeState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceCleanerTest {

    private BusinessEvent testEvent;
    private ReferenceCleaner<BusinessEvent> referenceCleaner;

    @BeforeEach
    void setUp() throws IOException {
        URI uri = URI.create("https://raw.githubusercontent.com/finos/common-domain-model/4.0.0/rosetta-source/src/main/resources/result-json-files/fpml-5-10/products/repo/repo-ex01-repo-fixed-rate.json");
        ObjectMapper objectMapper = RosettaObjectMapper.getNewMinimalRosettaObjectMapper();
        testEvent = BusinessEvent.builder().addAfter(objectMapper.readValue(uri.toURL(), TradeState.class)).build();
        referenceCleaner = new ReferenceCleaner<>(BusinessEvent.class, Trade.class);
    }

    @Test
    void allReferencesAreRemoved() {
        BusinessEvent businessEvent = referenceCleaner.removeUnusedReferences(testEvent);
        assertReferences(testEvent, businessEvent);
    }

    private void assertReferences(BusinessEvent expected, BusinessEvent actual) {
        Trade actualTrade = actual.getAfter().get(0).getTrade();
        Trade expectedTrade = expected.getAfter().get(0).getTrade();

        assertEquals(expectedTrade.getMeta().getGlobalKey(), actualTrade.getMeta().getGlobalKey());

        Party actualParty = actualTrade.getParty().get(0);
        Party expectedParty = expectedTrade.getParty().get(0);

        assertNull(actualParty.getMeta().getGlobalKey());
        assertNotNull(expectedParty.getMeta().getGlobalKey());
    }
}