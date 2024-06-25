package org.jamailun;

import com.intuit.karate.junit5.Karate;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FooUnitTests {

    @Karate.Test
    Karate testAllUnitTests() {
        return Karate.run()
                .debugMode(true)
                .tags("@foo")
                .relativeTo(getClass());
    }

    @Test
    void a() {
        System.out.println("ok");
    }

}
