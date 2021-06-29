package foo.foo;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sjms2.Sjms2Component;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {


      var component = new Sjms2Component();
      //var component = new SjmsComponent();
      //var component = new JmsComponent();
      ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
      activeMQConnectionFactory.setUser("artemis");
      activeMQConnectionFactory.setPassword("artemis");

      component.setConnectionFactory(activeMQConnectionFactory);

      this.getContext().addComponent("jms-testing", component);

      from("timer://foo?fixedRate=true&period=5000")
          .onCompletion()
            .onFailureOnly()
            .log("Something has failed.")
          .end()
          .setBody(simple("Body from onCompletion and transacted=true"))
          .to("jms-testing:queue:myQueue?transacted=true");

      from("timer://foo?fixedRate=true&period=5000")
          .onCompletion()
          .onFailureOnly()
          .log("Something has failed.")
          .end()
          .setBody(simple("Body from onCompletion and transacted=false"))
          .to("jms-testing:queue:myQueue?transacted=false");

      from("jms-testing:queue:myQueue?transacted=true")
        .log("Message received: ${body}");
    }

}
