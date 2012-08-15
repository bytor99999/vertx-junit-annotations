/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vertx.testing.junit;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.testing.junit.VertxConfigurableJUnit4Runner;
import org.vertx.testing.support.VertxTestBase;


/**
 * @author swilliams
 *
 */
@RunWith(VertxConfigurableJUnit4Runner.class)
public class SimpleEchoTest extends VertxTestBase {

  private static final String QUESTION = "Hello World";

  private static final String TEST_ADDRESS = "vertx.test.echo";

  @Before
  public void setup() {
    Assert.assertNotNull(super.getVertx());

    getVertx().eventBus().registerHandler(TEST_ADDRESS, new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> event) {
        if (QUESTION.equals(event.body)) {
          event.reply(event.body);
        }
        else {
          event.reply("huh?");
        }
      }});
    
    try {
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      //
    }
  }

  @Test
  public void test() {

    final long timeout = 10L;
    final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    getVertx().eventBus().send(TEST_ADDRESS, QUESTION, new QueueReplyHandler<String>(queue, timeout));

    try {
      String answer = queue.poll(timeout, TimeUnit.SECONDS);
      System.out.println("answer: " + answer);
      Assert.assertTrue(QUESTION.equals(answer));

    } catch (InterruptedException e) {
      //
    }
  }

}