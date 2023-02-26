package dev.glitch.exploratory.spring.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.glitch.exploratory.lookup.Lookup;
import dev.glitch.exploratory.lookup.fst.MultiFst;
import dev.glitch.exploratory.model.RecordPair;
import dev.glitch.exploratory.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class LookupConfig {
    
    @Bean(value = "fst")
    public Lookup getLookup() throws IOException {
        List<RecordPair> validation = new ArrayList<>();
        Stream<RecordPair> pairStream = CommonUtil.getRecordsStream(10000L, validation, 10);
        MultiFst multi = new MultiFst(pairStream, 10000);
        log.info("{}", validation);
        return multi;
    }
}
