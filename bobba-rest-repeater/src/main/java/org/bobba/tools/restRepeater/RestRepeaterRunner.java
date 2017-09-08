package org.bobba.tools.restRepeater;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class RestRepeaterRunner {

    public static void main(String[] args) throws IOException {
        final ClassPathXmlApplicationContext applicationContext = createApplicationContext();
        runRestRepeater(applicationContext);
        applicationContext.close();
    }

    private static void runRestRepeater(ClassPathXmlApplicationContext applicationContext) throws IOException {
        final RestRepeaterCommandLine restRepeaterCommandLine =
                applicationContext.getBean(RestRepeaterCommandLine.class);
        restRepeaterCommandLine.run();
    }

    private static ClassPathXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/rest-repeater-application.spring.xml");
    }

}
