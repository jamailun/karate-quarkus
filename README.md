# Karate/quarkus

With dichotomy, I could find the release which broke the Karate framework : [3.8.5](https://github.com/quarkusio/quarkus/releases/tag/3.8.3).

The Quarkus PR [#39442](https://github.com/quarkusio/quarkus/pull/39442) excluded `org.graalvm.polyglot:polyglot` from the Graal-SDK.

But reimporting it does not seem to work.

## The issue

Because the Graal SDK was excluded, Karates causes this issue :
```log
java.lang.NoClassDefFoundError: org/graalvm/polyglot/Value
        at com.intuit.karate.core.Variable.<init>(Variable.java:72)
        at com.intuit.karate.core.Variable.<clinit>(Variable.java:66)
        at com.intuit.karate.core.Config.<init>(Config.java:94)
        at com.intuit.karate.core.ScenarioCall.getParentConfig(ScenarioCall.java:67)
        at com.intuit.karate.core.ScenarioRuntime.<init>(ScenarioRuntime.java:74)
        at com.intuit.karate.core.ScenarioIterator.tryAdvance(ScenarioIterator.java:162)
        ...
```

## Potential fixes

### 1) Adding Graal-SDK :

We add the `graal-sdk` dependency.
```xml
<dependency>
  <groupId>org.graalvm.sdk</groupId>
  <artifactId>graal-sdk</artifactId>
  <version>22.3.3</version>
</dependency>
```

New exception : `ServiceConfiguration org.graalvm.polyglot.impl.AbstractPolyglotImpl: com.oracle.truffle.polyglot.PolyglotImpl not a subtype`

```log
java.util.ServiceConfigurationError: org.graalvm.polyglot.impl.AbstractPolyglotImpl: com.oracle.truffle.polyglot.PolyglotImpl not a subtype
        at java.base/java.util.ServiceLoader.fail(ServiceLoader.java:593)
        at java.base/java.util.ServiceLoader$LazyClassPathLookupIterator.hasNextService(ServiceLoader.java:1244)
        at java.base/java.util.ServiceLoader$LazyClassPathLookupIterator.hasNext(ServiceLoader.java:1273)
        at java.base/java.util.ServiceLoader$2.hasNext(ServiceLoader.java:1309)
        at java.base/java.util.ServiceLoader$3.hasNext(ServiceLoader.java:1393)
        at org.graalvm.polyglot.Engine$1.searchServiceLoader(Engine.java:918)
        at org.graalvm.polyglot.Engine$1.run(Engine.java:900)
        at org.graalvm.polyglot.Engine$1.run(Engine.java:894)
        at java.base/java.security.AccessController.doPrivileged(AccessController.java:319)
        at org.graalvm.polyglot.Engine.initEngineImpl(Engine.java:894)
        at org.graalvm.polyglot.Engine$ImplHolder.<clinit>(Engine.java:139)
        at org.graalvm.polyglot.Engine.getImpl(Engine.java:363)
        at org.graalvm.polyglot.Engine$Builder.build(Engine.java:621)
        at com.intuit.karate.graal.JsEngine.createContext(JsEngine.java:73)
        at com.intuit.karate.graal.JsEngine$1.initialValue(JsEngine.java:65)
        at com.intuit.karate.graal.JsEngine$1.initialValue(JsEngine.java:62)
        at java.base/java.lang.ThreadLocal.setInitialValue(ThreadLocal.java:225)
        at java.base/java.lang.ThreadLocal.get(ThreadLocal.java:194)
        at java.base/java.lang.ThreadLocal.get(ThreadLocal.java:172)
        at com.intuit.karate.graal.JsEngine.global(JsEngine.java:93)
        at com.intuit.karate.core.Tags.evaluate(Tags.java:156)
        at com.intuit.karate.core.ScenarioRuntime.isSelectedForExecution(ScenarioRuntime.java:344)
        at com.intuit.karate.core.ScenarioRuntime.<init>(ScenarioRuntime.java:108)
        at com.intuit.karate.core.ScenarioIterator.tryAdvance(ScenarioIterator.java:162)
        ...
```

Which is weird, because the class `com.oracle.truffle.polyglot.PolyglotImpl` does inherit `org.graalvm.polyglot.impl.AbstractPolyglotImpl`

Link to [Truffle class](https://github.com/oracle/graal/blob/master/truffle/src/com.oracle.truffle.polyglot/src/com/oracle/truffle/polyglot/PolyglotImpl.java)
source code.

### Only adding the new polyglot module

**Instead** of the previous fix, we add:
```xml
<dependency>
    <groupId>org.graalvm.polyglot</groupId>
    <artifactId>polyglot</artifactId>
    <version>24.0.1</version>
</dependency>
```

Now it compiles ! But karate won't find anything, even though the tests are the sames.

```log
org.opentest4j.AssertionFailedError: no features or scenarios found: [classpath:org/jamailun]
        at com.intuit.karate.junit5.Karate.iterator(Karate.java:72)
        ...
```
