package com.dolphinskart.configuration;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.dolphinskart.prospectuser.model.ProspectCustomer;

@Configuration
@EnableBatchProcessing
public class CustomerEmailjobConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@SuppressWarnings("serial")
	@Bean
	public MongoItemReader<ProspectCustomer> reader() {
		MongoItemReader<ProspectCustomer> reader = new MongoItemReader<ProspectCustomer>();
		reader.setTemplate(mongoTemplate);
		reader.setQuery("{}");
		reader.setCollection("prospect_customers");
		reader.setTargetType(ProspectCustomer.class);
		reader.setSort(new HashedMap<String, Sort.Direction>() {
			{
				put("name", Sort.Direction.ASC);
			}
		});
		return reader;
	}
	
	@Bean
	public ProspectCustomerWriter writer(){
		return new ProspectCustomerWriter();
	}
	
	@Bean
	public ItemProcessor<ProspectCustomer, ProspectCustomer> processor(){
		return new ProspectCustomerProcessor();
	}
	

	@Bean
	public Step step() {
		return stepBuilderFactory.get("step").<ProspectCustomer, ProspectCustomer>chunk(1000).reader(reader())
				.writer(writer()).processor(processor())
				.build();
	}
	
	@Bean("job one")
	public Job jobExe() {
		return jobBuilderFactory.get("Customer Notification job execution").flow(step()).end().build();
	}
	
//	
//	@Bean
//	public TaskExecutor taskExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(64);
//		executor.setMaxPoolSize(64);
//		executor.setQueueCapacity(64);
//		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//		executor.setThreadNamePrefix("");
//		return executor;
//	}
}
