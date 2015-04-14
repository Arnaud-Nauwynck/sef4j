package org.sef4j.testwebapp.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.sef4j.testwebapp.dto.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryProductService {
    
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryProductService.class);

    private List<ProductDTO> inMemoryProductsDTO = new ArrayList<ProductDTO>();

    
    @PostConstruct
    public void initInMemoryProducts() {
        LOG.info("init InMemoryProductService");
        for (int i = 0; i < 20000; i++) {
            String name = null;
            switch(i % 3) {
                case 0: name = "book"; break; 
                case 1: name = "pc"; break; 
                case 2: name = "telephone"; break; 
            }
            inMemoryProductsDTO.add(new ProductDTO(i, name + " " + i, "cool " + name + " " + i));
        }
    }

    public List<ProductDTO> findAll() {
        return inMemoryProductsDTO;
    }
    
    
}
