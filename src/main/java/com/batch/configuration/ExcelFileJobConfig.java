//package com.dolphinskart.configuration;
//
//import java.net.MalformedURLException;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.data.MongoItemWriter;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.LineMapper;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.batch.item.file.mapping.DefaultLineMapper;
//import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import com.dolphinskart.prospectcustomer.model.ProspectCustomer;
//
//@Configuration
//public class ExcelFileJobConfig {
//
//	@Autowired
//	private JobBuilderFactory jobBuilderFactory;
//
//	@Autowired
//	private StepBuilderFactory stepBuilderFactory;
//
//	@Autowired
//	private MongoTemplate template;
//
//
//	@Bean
//	public FlatFileItemReader<ProspectCustomer> importReader(
//			@Value("#{jobParameters[fullPathFileName]}") String pathToFile) throws MalformedURLException {
////		FlatFileItemReader<ProspectCustomer> reader = new FlatFileItemReader<>();
////		reader.setResource(new FileSystemResource(pathToFile));
////		reader.setLineMapper(lineMapper());
////		reader.setName("csv-reader");
//		FlatFileItemReader<ProspectCustomer> reader = new FlatFileItemReader<ProspectCustomer>();
//		reader.setResource(new FileSystemResource(pathToFile));
//		reader.setName("csv-reader");
//		reader.setLineMapper(new DefaultLineMapper<ProspectCustomer>() {{
//			setLineTokenizer(new DelimitedLineTokenizer() {{
//				setNames(new String[] {"id","name", "email", "mobile"});
//				setStrict(false);
//			}});
//			setFieldSetMapper(new BeanWrapperFieldSetMapper<ProspectCustomer>() {{
//				setTargetType(ProspectCustomer.class);
//			}});
//		}});
//		return reader;
//	}
//
//
////	public LineMapper<ProspectCustomer> lineMapper() {
////		DefaultLineMapper<ProspectCustomer> defaultLineMapper = new DefaultLineMapper<ProspectCustomer>();
////		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
////		tokenizer.setDelimiter(",");
////		tokenizer.setStrict(false);
//////		tokenizer.setNames("Name", "Email ID", "Phone Number" ); 
////		tokenizer.setNames("id", "Name", "Email ID", "Phone Number");
////		BeanWrapperFieldSetMapper<ProspectCustomer> fieldSetMapper = new BeanWrapperFieldSetMapper<ProspectCustomer>();
////		fieldSetMapper.setTargetType(ProspectCustomer.class);
////		defaultLineMapper.setLineTokenizer(tokenizer);
////		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
////		return defaultLineMapper;
////	}
//
//	@Bean
//	public ProspectCustomerProcessor processor() {
//		return new ProspectCustomerProcessor();
//	}
//
//	@Bean
//	public MongoItemWriter<ProspectCustomer> writer() {
//		MongoItemWriter<ProspectCustomer> writter = new MongoItemWriter<ProspectCustomer>();
//		writter.setTemplate(template);
//		writter.setCollection("prospect_customers");
//		return writter;
//	}
//
//	@Bean
//	public Step step1(@Qualifier("importReader") ItemReader<ProspectCustomer> importReader)
//			throws MalformedURLException {
//		return stepBuilderFactory.get("step1").<ProspectCustomer, ProspectCustomer>chunk(10).reader(importReader)
//				.processor(processor()).writer(writer()).build();
//	}
//	
//	@Bean
//	public Job importUserJob(JobCompletetionNotificationListener listener) throws MalformedURLException {
//		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener)
//				.flow(step1(importReader("#{jobParameters[fullPathFileName]}"))).end().preventRestart().build();
//	}
//}
