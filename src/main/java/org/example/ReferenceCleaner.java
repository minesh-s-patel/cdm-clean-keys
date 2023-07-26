package org.example;

import cdm.base.staticdata.party.Party;
import cdm.event.common.BusinessEvent;
import com.rosetta.model.lib.RosettaModelObject;

public class ReferenceCleaner<T extends RosettaModelObject> {

    private final Class<T> topLevelType;
    private final Class<? extends RosettaModelObject>[] includeGlobalKeys;

    @SafeVarargs
    public ReferenceCleaner(Class<T> topLevelType, Class<? extends RosettaModelObject>... includeGlobalKeys) {
        this.topLevelType = topLevelType;
        this.includeGlobalKeys = includeGlobalKeys;
    }

    public T removeUnusedReferences(T object) {
        GlobalKeyCleanupProcessStep globalKeyCleanupProcessStep = new GlobalKeyCleanupProcessStep(includeGlobalKeys);
        GlobalKeyCleanupProcessStep.KeyPostProcessReport referenceRemoverPostProcessorReport = globalKeyCleanupProcessStep.runProcessStep(topLevelType, object.toBuilder());
        RosettaModelObject resultObject = referenceRemoverPostProcessorReport.getResultObject();
        return topLevelType.cast(resultObject.toBuilder().prune().build());
    }
}
