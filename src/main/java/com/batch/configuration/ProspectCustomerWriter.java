package com.dolphinskart.configuration;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.dolphinskart.prospectuser.model.ProspectCustomer;

@Component
public class ProspectCustomerWriter implements ItemWriter<ProspectCustomer>{

	@Override
	public void write(List<? extends ProspectCustomer> items) throws Exception {	
	}
}
