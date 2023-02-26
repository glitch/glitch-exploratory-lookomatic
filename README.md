# Fun with FSTs, Bloomfilters, Oh My!

## Brief developer notes
- `mvn formatter:format` will run the code formatter
- `mvn clean package` will build jar & jar-with-dependencies

### Example run of the app
```
java -Xmx6G -Dlogback.configurationFile=logback.xml -jar target/lookup-o-matic-0.0.1-SNAPSHOT-jar-with-dependencies.jar --total 100000 --batch 50000 --validatePercent 50
```