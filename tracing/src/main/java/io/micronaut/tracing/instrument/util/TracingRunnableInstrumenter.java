/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.tracing.instrument.util;

import io.micronaut.context.annotation.Requires;
import io.micronaut.scheduling.instrument.ReactiveInstrumenter;
import io.micronaut.scheduling.instrument.RunnableInstrumenter;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.function.Function;

/**
 * A function that instruments an existing Runnable with {@link TracingRunnable}.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
@Requires(beans = Tracer.class)
public class TracingRunnableInstrumenter implements Function<Runnable, Runnable>, RunnableInstrumenter, ReactiveInstrumenter {

    private final Tracer tracer;

    /**
     * Create a function that instrument an existing Runnable.
     *
     * @param tracer For span creation and propagation across arbitrary transports
     */
    public TracingRunnableInstrumenter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Runnable apply(Runnable runnable) {
        return new TracingRunnable(runnable, tracer);
    }

    @Override
    public Runnable instrument(Runnable command) {
        return apply(command);
    }

    @Override
    public Optional<RunnableInstrumenter> newInstrumentation() {
        Scope active = tracer.scopeManager().active();
        Span activeSpan;
        if (active != null) {
            activeSpan = active.span();
        } else {
            activeSpan = tracer.activeSpan();
        }
        if (activeSpan != null) {
            return Optional.of(new RunnableInstrumenter() {
                @Override
                public Runnable instrument(Runnable command) {
                    return () -> {
                        Scope scope;
                        scope = tracer.scopeManager().activate(activeSpan, false);
                        try {
                            command.run();
                        } finally {
                            if (scope != null) {
                                scope.close();
                            }
                        }
                    };
                }
            });
        }
        return Optional.empty();
    }
}
