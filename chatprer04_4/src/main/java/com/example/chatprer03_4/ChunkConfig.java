//package com.example.chatprer03_4;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.support.ListItemReader;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//
//@Configuration
//public class ChunkConfig {
//
//    @Autowired
//    JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    StepBuilderFactory stepBuilderFactory;
//
//
//    @Bean
//    public Job chunkJob() {
//        return jobBuilderFactory.get("chunkJob")
//                .start(chunkStep1())
//                .incrementer(new RunIdIncrementer())
//                .build();
//    }
//
//    @Bean
//    public Step chunkStep1() {
//        return stepBuilderFactory.get("chunkStep1")
//                .<String,String>chunk(100)
//                .reader(chunkReader())
//                .writer(chunkWriter())
//                .build();
//    }
//
//    @Bean
//    public ItemReader<String> chunkReader() {
//        ArrayList<String> strings = new ArrayList<>(1000);
//        for (int i = 0; i < 1000; i++) {
//            strings.add("Item " + i);
//        }
//        return new ListItemReader<>(strings);
//    }
//
//    @Bean
//    public ItemWriter<String> chunkWriter() {
//       return items ->{
//           System.out.println("itemSize: " + items.size());
//           System.out.println("startItem: "+ items.get(0));
//           System.out.println("endItem: "+ items.get(items.size() - 1));
//       };
//    }
//}
