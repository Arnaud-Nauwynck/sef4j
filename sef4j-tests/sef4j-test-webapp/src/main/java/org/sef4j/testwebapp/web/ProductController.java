package org.sef4j.testwebapp.web;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	private List<ProductDTO> products = new ArrayList<ProductDTO>(Arrays.asList(new ProductDTO[] {
			new ProductDTO(1, "product1", "cool product 1"),
			new ProductDTO(2, "product2", "cool product 2")
	}));
	
	@PostConstruct
	public void init() {
		LOG.info("init ProductController");
	}
	
	@RequestMapping(value="all", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProductDTO> findAll() {
		return products;
	}
	
}
