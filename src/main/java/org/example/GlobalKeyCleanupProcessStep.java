package org.example;

import com.regnosys.rosetta.common.util.SimpleBuilderProcessor;
import com.rosetta.lib.postprocess.PostProcessorReport;
import com.rosetta.model.lib.GlobalKey;
import com.rosetta.model.lib.RosettaModelObject;
import com.rosetta.model.lib.RosettaModelObjectBuilder;
import com.rosetta.model.lib.meta.FieldWithMeta;
import com.rosetta.model.lib.path.RosettaPath;
import com.rosetta.model.lib.process.AttributeMeta;
import com.rosetta.model.lib.process.BuilderProcessor;
import com.rosetta.model.lib.process.PostProcessStep;

import java.util.Arrays;
import java.util.List;

public class GlobalKeyCleanupProcessStep implements PostProcessStep {


    private final Class<? extends RosettaModelObject>[] includeGlobalKeys;

    public GlobalKeyCleanupProcessStep(Class<? extends RosettaModelObject> ...includeGlobalKeys) {
        this.includeGlobalKeys = includeGlobalKeys;
    }

    @Override
    public Integer getPriority() {
        return 1;
    }

    @Override
    public String getName() {
        return "GlobalKey Cleanup Post Processor";
    }

    @Override
    public <T extends RosettaModelObject> GlobalKeyCleanupProcessStep.KeyPostProcessReport runProcessStep(Class<? extends T> topClass, T instance) {
        RosettaModelObjectBuilder builder = instance.toBuilder();
        KeyRemoverProcessor keyRemoverProcessor = new KeyRemoverProcessor(builder, includeGlobalKeys);
        builder.process(RosettaPath.valueOf(topClass.getSimpleName()), keyRemoverProcessor);
        return keyRemoverProcessor.report;
    }

    static class KeyRemoverProcessor extends SimpleBuilderProcessor {
        private final GlobalKeyCleanupProcessStep.KeyPostProcessReport report;
        private final List<Class<? extends RosettaModelObject>> includeGlobalKeys;

        public KeyRemoverProcessor(RosettaModelObjectBuilder builder, Class<? extends RosettaModelObject> ...includeGlobalKeys) {
            this.report = new KeyPostProcessReport(builder);
            this.includeGlobalKeys = List.of(includeGlobalKeys);
        }

        @Override
        public <R extends RosettaModelObject> boolean processRosetta(RosettaPath path,
                                                                     Class<R> rosettaType,
                                                                     RosettaModelObjectBuilder builder,
                                                                     RosettaModelObjectBuilder parent,
                                                                     AttributeMeta... metas) {
            if (builder == null || !builder.hasData())
                return false;
            if (isGlobalKey(builder, metas) && !includeGlobalKeys.contains(builder.getType())) {
                GlobalKey.GlobalKeyBuilder keyBuilder = (GlobalKey.GlobalKeyBuilder) builder;
                keyBuilder.getOrCreateMeta().setGlobalKey(null);
            }
            return true;
        }

        @Override
        public GlobalKeyCleanupProcessStep.KeyPostProcessReport report() {
            return report;
        }

        private boolean isGlobalKey(RosettaModelObjectBuilder builder, AttributeMeta... metas) {
            return builder instanceof GlobalKey&& !(builder instanceof FieldWithMeta && !Arrays.asList(metas).contains(AttributeMeta.GLOBAL_KEY_FIELD));
        }
    }

    public static class KeyPostProcessReport implements PostProcessorReport, BuilderProcessor.Report {

        private final RosettaModelObjectBuilder result;

        public KeyPostProcessReport(RosettaModelObjectBuilder result) {
            this.result = result;
        }

        @Override
        public RosettaModelObjectBuilder getResultObject() {
            return result;
        }
    }
}