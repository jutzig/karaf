/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.itests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import java.net.URI;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JmsTest extends KarafTestSupport {

    @Before
    public void installJmsFeatureAndActiveMQBroker() throws Exception {
        installAndAssertFeature("jms");
        featureService.addRepository(new URI("mvn:org.apache.activemq/activemq-karaf/5.10.0/xml/features"));
        installAndAssertFeature("activemq-broker-noweb");
    }


    @Test
    public void createActiveMQConnectionFactoryTest() throws Exception {
        System.out.println(executeCommand("jms:create -t ActiveMQ -u karaf -p karaf --url tcp://localhost:61616 test"));
        // give time to fileinstall to load the blueprint file
        Thread.sleep(5000);
        String info = executeCommand("jms:info test");
        System.out.println(info);
        assertContains("ActiveMQ", info);
        assertContains("5.10.0", info);
        System.out.println(executeCommand("jms:send test queue message"));
        String consumed = executeCommand("jms:consume test queue");
        System.out.println(consumed);
        assertContains("1 message", consumed);
    }

}