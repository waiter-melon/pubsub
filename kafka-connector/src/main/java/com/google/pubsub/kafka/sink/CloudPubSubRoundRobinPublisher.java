// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////
package com.google.pubsub.kafka.sink;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.pubsub.v1.PublishRequest;
import com.google.pubsub.v1.PublishResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link CloudPubSubPublisher} that distributes publishes in round-robin fashion over a set of
 * {@link CloudPubSubGRPCPublisher}s.
 */
public class CloudPubSubRoundRobinPublisher implements CloudPubSubPublisher {

  private List<CloudPubSubPublisher> publishers;
  private int currentPublisherIndex = 0;

  CloudPubSubRoundRobinPublisher(int publisherCount) {
    publishers = new ArrayList<>(publisherCount);
    for (int i = 0; i < publisherCount; ++i) {
      publishers.add(new CloudPubSubGRPCPublisher());
    }
  }

  @Override
  public ListenableFuture<PublishResponse> publish(PublishRequest request) {
    currentPublisherIndex = (currentPublisherIndex + 1) % publishers.size();
    return publishers.get(currentPublisherIndex).publish(request);
  }
}
