//package com.dolphinskart.configuration;
//
//import org.apache.commons.collections4.map.HashedMap;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.data.MongoItemReader;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import com.dolphinskart.prospectuser.model.ProspectSeller;
//
//@Configuration
//@EnableBatchProcessing
//public class SellerEmailJobConfig {
//
//	@Autowired
//	private JobBuilderFactory jobBuilderFactory;
//	
//	@Autowired
//	private StepBuilderFactory stepBuilderFactory;
//	
//	@Autowired
//	private MongoTemplate mongoTemplate;
//	
//	@SuppressWarnings("serial")
//	@Bean
//	public MongoItemReader<ProspectSeller> reader() {
//		MongoItemReader<ProspectSeller> reader = new MongoItemReader<ProspectSeller>();
//		reader.setTemplate(mongoTemplate);
//		reader.setQuery("{}");
//		reader.setCollection("prospect_sellers");
//		reader.setTargetType(ProspectSeller.class);
//		reader.setSort(new HashedMap<String, Sort.Direction>() {
//			{
//				put("name", Sort.Direction.ASC);
//			}
//		});
//		return reader;
//	}
//	
//	@Bean
//	public ProspectSellerWriter writer(){
//		return new ProspectSellerWriter();
//	}
//	
//	@Bean
//	public ItemProcessor<ProspectSeller, ProspectSeller> processor(){
//		return new ProspectSellerProcessor();
//	}
//	
//
//	@Bean
//	public Step step() {
//		return stepBuilderFactory.get("step 1").<ProspectSeller, ProspectSeller>chunk(1000).reader(reader())
//				.writer(writer()).processor(processor())
//				.build();
//	}
//
//	
//	@Bean("job two")
//	public Job jobExe() {
//		return jobBuilderFactory.get("Seller Notification job execution").flow(step()).end().build();
//	}
//}
