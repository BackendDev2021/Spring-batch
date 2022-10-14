package com.dolphinskart.configuration;

import javax.batch.api.listener.JobListener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.stereotype.Component;

@Component
public class JobCompletetionNotificationListener implements JobListener {

	@Override
	public void beforeJob() throws Exception {
		// TODO Auto-generated method stub
	}

	public void afterJob(org.springframework.batch.core.JobExecution jobExecution) throws Exception {
		 if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
	            System.out.println("BATCH JOB COMPLETED SUCCESSFULLY");
	        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
	            System.out.println("BATCH JOB FAILED");
	        }
	}

	@Override
	public void afterJob() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
