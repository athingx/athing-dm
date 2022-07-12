package io.github.athingx.athing.dm.thing.impl.define;

import io.github.athingx.athing.dm.api.ThingDmData;
import io.github.athingx.athing.dm.api.annotation.ThDmParam;
import io.github.athingx.athing.dm.api.annotation.ThDmService;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmParamMeta;
import io.github.athingx.athing.dm.common.meta.ThDmServiceMeta;
import io.github.athingx.athing.dm.thing.define.DefineService;
import io.github.athingx.athing.dm.thing.define.ServiceInvoker;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

class DefineServiceImpl extends DefineMemberImpl<DefineService> implements DefineService {

    private boolean required;

    private final Map<String,Class<?>> parameters = new HashMap<>();

    public DefineServiceImpl(ThDmCompMeta meta) {
        super(meta);
    }

    public DefineService isRequired(boolean required) {
        this.required = required;
        return this;
    }

    public DefineService parameters(Supplier<Map<String, Class<?>>> supplier) {
        Optional.ofNullable(supplier.get())
                .ifPresent(parameters::putAll);
        return this;
    }

    @Override
    DefineService getThis() {
        return this;
    }

    @Override
    public <T extends ThingDmData> void sync(Class<T> returnType, ServiceInvoker<T> invoker) {
        invoke(returnType, true, invoker);
    }

    @Override
    public <T extends ThingDmData> void async(Class<T> returnType, ServiceInvoker<CompletableFuture<T>> invoker) {
        invoke(returnType, false, invoker);
    }

    private void invoke(Class<?> returnType, boolean sync, ServiceInvoker<?> invoker) {
        final ThDmService anThDmService = new ThDmService() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ThDmService.class;
            }

            @Override
            public String id() {
                return memberId;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String desc() {
                return desc;
            }

            @Override
            public boolean isRequired() {
                return required;
            }

            @Override
            public boolean isSync() {
                return sync;
            }
        };

        final ThDmServiceMeta sMeta = new ThDmServiceMeta(meta.getId(), anThDmService, null, null) {

            @Override
            public ThDmParamMeta[] getThDmParamMetaArray() {

                final Set<Map.Entry<String, Class<?>>> entries = parameters.entrySet();
                final ThDmParamMeta[] metas = new ThDmParamMeta[entries.size()];

                int index = 0;
                for (final Map.Entry<String, Class<?>> entry : entries) {

                    final ThDmParam anThDmParam = new ThDmParam() {

                        @Override
                        public Class<? extends Annotation> annotationType() {
                            return ThDmParam.class;
                        }

                        @Override
                        public String value() {
                            return entry.getKey();
                        }

                    };

                    metas[index] = new ThDmParamMeta(anThDmParam, entry.getValue(), index);
                    index++;

                }

                return metas;
            }

            @Override
            public Class<?> getReturnType() {
                return returnType;
            }

            @Override
            public Class<?> getActualReturnType() {
                return returnType;
            }

            @Override
            public Object service(Object instance, GetArgument getArgument) throws InvocationTargetException {
                try {
                    return invoker.invoke(new HashMap<>(){{
                        parameters.forEach((name, type)-> put(name, getArgument.get(name, type)));
                    }});
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
            }

        };

        if(meta.getIdentityThDmServiceMetaMap().putIfAbsent(sMeta.getIdentifier(), sMeta) != null) {
            throw new IllegalArgumentException("duplicate defining service: %s".formatted(sMeta.getIdentifier()));
        }
    }


}
