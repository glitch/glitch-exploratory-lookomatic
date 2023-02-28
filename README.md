# Fun with FSTs, Bloomfilters, Oh My!

## Brief developer notes
- `mvn formatter:format` will run the code formatter
- `mvn clean package` will build jar & jar-with-dependencies
- `mvn clean package spring-boot:repackage` will build the above plus a -springboot.jar pointing to alt main class

### Example CmdLine run of the app
```
java -Xmx6G -Dlogback.configurationFile=logback.xml -jar target/lookup-o-matic-0.0.1-SNAPSHOT-jar-with-dependencies.jar --total 100000 --batch 50000 --validatePercent 50 --query 1000000
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
      "vmArgs": "-Dspring.profiles.active=local,swagger"
    }
  ]
}
```