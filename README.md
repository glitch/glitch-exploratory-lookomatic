# Fun with FSTs, Bloomfilters, Oh My!
- Playground for learning how to use Lucene's FST components as a pseudo Map / Id-Lookup service.
- _Coming Soon_ that same type of service using Bloom filters (Guava lib) backed by compressed, Sorted (K,V) File (probably Accumulo or HBase)

## Brief developer notes
### Build
- `mvn formatter:format` will run the code formatter
- `mvn clean package` will build jar & jar-with-dependencies
- `mvn clean package spring-boot:repackage` will build the above plus a -springboot.jar pointing to alt main class

### Example CmdLine run of the app
```
java -Xmx6G -Dlogback.configurationFile=logback.xml -jar target/lookup-o-matic-0.0.1-SNAPSHOT-jar-with-dependencies.jar --total 100000 --batch 50000 --validatePercent 50 --query 1000000
```
#### Chronicle-Map additional args
You may need to add these args to the java command line args for Chronicle-Map
```
--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED
--add-exports=java.base/sun.nio.ch=ALL-UNNAMED
--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
```

### Example Spring Boot run
```
mvn clean package spring-boot:repackage
java -Dlogging.config=logback.xml -Dspring.profiles.active=local -jar target/lookup-o-matic-0.0.1-SNAPSHOT-springboot.jar
```
Then point your browser at `http://localhost:8080/swagger-ui/index.html`

### Example VSCode launch.json
```
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug",
      "request": "launch",
      "mainClass": "dev.glitch.exploratory.spring.SpringBootApp",
      "projectName": "lookup-o-matic",
      "args": "",
      "vmArgs": [
        "-Dspring.profiles.active=local,swagger",
        "-Dlogback.configurationFile=logback.xml",
        "--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED",
        "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.io=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED"
      ]
    }
  ]
}
```
#### For Chronicle & VSCode
Add this to .vscode/settings.json to run unit tests from testing widget
```
      "java.test.config": {
        "vmArgs": [
            "-Dlogback.configurationFile=logback.xml",
            "--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED",
            "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
            "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED"
        ]
      }
```