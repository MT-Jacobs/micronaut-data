/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.data.runtime.intercept.reactive;

import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.intercept.RepositoryMethodKey;
import io.micronaut.data.intercept.reactive.FindPageReactiveInterceptor;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.runtime.PreparedQuery;
import io.micronaut.data.operations.RepositoryOperations;
import io.micronaut.transaction.support.TransactionSynchronizationManager;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * Default implementation of {@link FindPageReactiveInterceptor}.
 *
 * @author graemerocher
 * @since 1.0.0
 */
public class DefaultFindPageReactiveInterceptor extends AbstractPublisherInterceptor
    implements FindPageReactiveInterceptor<Object, Object> {
    /**
     * Default constructor.
     *
     * @param operations The operations
     */
    protected DefaultFindPageReactiveInterceptor(@NonNull RepositoryOperations operations) {
        super(operations);
    }

    @Override
    public Publisher<?> interceptPublisher(RepositoryMethodKey methodKey, MethodInvocationContext<Object, Object> context) {
        if (context.hasAnnotation(Query.class)) {
            PreparedQuery<?, ?> preparedQuery = prepareQuery(methodKey, context);
            PreparedQuery<?, Number> countQuery = prepareCountQuery(methodKey, context);

            TransactionSynchronizationManager.TransactionSynchronizationState state = TransactionSynchronizationManager.getState();

            return Flux.from(reactiveOperations.findOne(countQuery))
                .flatMap(total -> {
                    try (TransactionSynchronizationManager.TransactionSynchronizationStateOp ignore = TransactionSynchronizationManager.withState(state)) {
                        Flux<Object> resultList = Flux.from(reactiveOperations.findAll(preparedQuery));
                        return resultList.collectList().map(list ->
                            Page.of(list, preparedQuery.getPageable(), total.longValue())
                        );
                    }
                });
        }
        return reactiveOperations.findPage(getPagedQuery(context));
    }
}
