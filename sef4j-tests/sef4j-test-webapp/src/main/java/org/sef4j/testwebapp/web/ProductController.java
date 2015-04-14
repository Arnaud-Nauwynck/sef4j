package org.sef4j.testwebapp.web;

import java.util.List;

import javax.inject.Inject;

import org.sef4j.testwebapp.dto.ProductDTO;
import org.sef4j.testwebapp.service.InMemoryProductService;
import org.sef4j.testwebapp.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="app/rest/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

	private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);
	
	@Inject
	private ProductService productService;

	@Inject
    private InMemoryProductService inMemoryProductService;
	
	@RequestMapping(value="all", method=RequestMethod.GET)
	public List<ProductDTO> findAll() {
	    LOG.info("findAll");
	    List<ProductDTO> res = productService.findAll();
	    return res;
	}

	@RequestMapping(value="all-in-memory", method=RequestMethod.GET)
	public List<ProductDTO> findInMemoryAll() {
        LOG.info("findInMemoryAll");
		List<ProductDTO> res = inMemoryProductService.findAll();
        return res;
	}
	
}
