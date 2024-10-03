package org.jamailun;

import com.intuit.karate.junit5.Karate;
import io.quarkus.test.junit.QuarkusTest;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.util.*;
import java.util.regex.Pattern;

@QuarkusTest
public class FooUnitTests {

    public static String[] allFeaturesRelativeTo(Class<?> clazz) {
        String packageName = clazz.getPackageName();
        Reflections reflections = new Reflections(packageName, new ResourcesScanner());
        Set<String> resourceList = reflections.getResources(Pattern.compile(".*\\.feature"));
        String[] result = resourceList.stream()
            .map(r -> "classpath:" + r)
            .toArray(String[]::new);
        System.out.println("Classpaths = " + Arrays.toString(result));
        return result;
    }

    @Karate.Test
    Karate testAllUnitTests() {
        return Karate.run(allFeaturesRelativeTo(getClass()))
            .tags("@foo");
//        return Karate.run()
//                .tags("@foo")
//                .relativeTo(getClass());
    }

}
