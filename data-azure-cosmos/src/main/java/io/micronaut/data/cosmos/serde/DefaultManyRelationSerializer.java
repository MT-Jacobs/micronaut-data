/*
 * Copyright 2017-2022 original authors
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
package io.micronaut.data.cosmos.serde;

import io.micronaut.core.type.Argument;
import io.micronaut.data.document.serde.ManyRelationSerializer;
import io.micronaut.serde.Encoder;
import io.micronaut.serde.Serializer;
import jakarta.inject.Singleton;

import java.io.IOException;

/**
 * Default {@link ManyRelationSerializer} implementation.
 *
 * @author radovanradic
 * @since 3.9.0
 */
@Singleton
final class DefaultManyRelationSerializer implements ManyRelationSerializer {
    @Override
    @SuppressWarnings("java:S3740") // Disabled SonarLint rule for: Raw types should not be used
    public void serialize(Encoder encoder, EncoderContext context, Argument<?> type, Object value) throws IOException {
        if (value == null) {
            encoder.encodeNull();
        } else {
            Serializer serializer = context.findSerializer(type);
            serializer.createSpecific(context, type).serialize(encoder, context, type, value);
        }
    }
}
