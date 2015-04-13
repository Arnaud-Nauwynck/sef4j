package org.sef4j.testwebapp.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.sef4j.testwebapp.dto.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app/rest/products")
public class ProductController {

	private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);
	
	private List<ProductDTO> products = new ArrayList<ProductDTO>();
	
	@PostConstruct
	public void init() {
		LOG.info("init ProductController");
		for (int i = 0; i < 20000; i++) {
		    String name = null;
		    switch(i % 3) {
		        case 0: name = "book"; break; 
                case 1: name = "pc"; break; 
                case 2: name = "telephone"; break; 
		    }
		    products.add(new ProductDTO(i, name + " " + i, "cool " + name + " " + i));
		}
	}
	
	@RequestMapping(value="all", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProductDTO> findAll() {
	    // emulate changes ... add one more Product on each refresh
	    int i = products.size();
	    products.add(new ProductDTO(i, "product" + i, "cool product " + i));
	    
		return products;
	}
	
}
