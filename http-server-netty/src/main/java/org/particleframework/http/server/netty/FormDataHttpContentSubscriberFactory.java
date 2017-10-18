/*
 * Copyright 2017 original authors
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
package org.particleframework.http.server.netty;

import io.netty.buffer.ByteBuf;
import org.particleframework.http.MediaType;
import org.particleframework.http.server.netty.configuration.NettyHttpServerConfiguration;
import org.particleframework.http.annotation.Consumes;
import org.reactivestreams.Subscriber;

import javax.inject.Singleton;

/**
 * Builds a {@link Subscriber} for {@link MediaType#APPLICATION_FORM_URLENCODED}
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA})
@Singleton
public class FormDataHttpContentSubscriberFactory implements HttpContentSubscriberFactory {

    private final NettyHttpServerConfiguration configuration;

    public FormDataHttpContentSubscriberFactory(NettyHttpServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public HttpContentSubscriber<ByteBuf> build(NettyHttpRequest request) {
        return new FormDataHttpContentSubscriber(request, configuration);
    }
}
