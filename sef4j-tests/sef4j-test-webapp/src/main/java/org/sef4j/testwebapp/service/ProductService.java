package org.sef4j.testwebapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.sef4j.testwebapp.domain.ProductEntity;
import org.sef4j.testwebapp.dto.ProductDTO;
import org.sef4j.testwebapp.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class ProductService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    @Inject
    private ProductRepository productRepository;
    
    @Inject
    private DtoMapper dtoMapper;
    
    public List<ProductDTO> findAll() {
        LOG.debug("findAll");
        List<ProductEntity> tmpres = productRepository.findAll();
        return dtoMapper.map(tmpres);
    }
    
    @PostConstruct // needed only once ...
    public void createDefaults() {
        LOG.info("createDefaults");
        List<ProductEntity> alreadyFound = productRepository.findAllByShortDescrLike("cool %");
        Map<String,ProductEntity> alreadyFoundByDescr = new HashMap<String,ProductEntity>();
        alreadyFound.stream().forEach(x -> alreadyFoundByDescr.put(x.getShortDescr(), x));
        
        List<ProductEntity> toCreate = new ArrayList<ProductEntity>();
        for (int i = 0; i < 2000; i++) {
            String name = null;
            switch(i % 3) {
                case 0: name = "book"; break; 
                case 1: name = "pc"; break; 
                case 2: name = "telephone"; break; 
            }
            String shortDescr = "cool " + name + " " + i;
            ProductEntity p = alreadyFoundByDescr.get(shortDescr);
            if (p == null) {        
                p = new ProductEntity();
                p.setName(name + " " + i);
                p.setShortDescr(shortDescr);
                toCreate.add(p);
            }
        }
        if (! toCreate.isEmpty()) {
            LOG.info("createDefaults ProductEntity : " + toCreate.size());
            productRepository.save(toCreate);
            productRepository.flush();
            LOG.info("... done createDefaults ProductEntity : " + toCreate.size());
        }
    }
    
}
