/*
 * Copyright 2020 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spinnaker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.kork.plugins.sdk.SdkFactory;
import com.netflix.spinnaker.kork.plugins.sdk.httpclient.HttpClientSdkFactory;
import com.netflix.spinnaker.kork.plugins.sdk.httpclient.internal.CompositeOkHttpClientFactory;
import com.netflix.spinnaker.kork.plugins.sdk.httpclient.internal.DefaultOkHttp3ClientFactory;
import com.netflix.spinnaker.kork.plugins.sdk.httpclient.internal.OkHttp3ClientFactory;
import com.netflix.spinnaker.okhttp.OkHttp3MetricsInterceptor;
import com.netflix.spinnaker.okhttp.OkHttpClientConfigurationProperties;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(OkHttpClientConfigurationProperties.class)
@Import(OkHttpClientComponents.class)
public class HttpClientSdkConfiguration {

  @Bean
  public static SdkFactory httpClientSdkFactory(
      List<OkHttp3ClientFactory> okHttpClientFactories,
      Environment environment,
      OkHttpClientConfigurationProperties okHttpClientConfigurationProperties,
      OkHttp3MetricsInterceptor okHttp3MetricsInterceptor) {

    List<OkHttp3ClientFactory> factories = new ArrayList<>(okHttpClientFactories);
    factories.add(new DefaultOkHttp3ClientFactory(okHttp3MetricsInterceptor));

    OkHttp3ClientConfiguration config =
        new OkHttp3ClientConfiguration(
            okHttpClientConfigurationProperties, okHttp3MetricsInterceptor);

    ObjectMapper objectMapper = new ObjectMapper();

    return new HttpClientSdkFactory(
        new CompositeOkHttpClientFactory(okHttpClientFactories), environment, objectMapper, config);
  }
}
