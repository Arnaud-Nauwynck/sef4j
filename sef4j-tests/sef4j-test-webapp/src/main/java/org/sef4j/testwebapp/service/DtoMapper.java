package org.sef4j.testwebapp.service;

import java.util.List;

import javax.annotation.PostConstruct;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.sef4j.testwebapp.domain.ProductEntity;
import org.sef4j.testwebapp.dto.ProductDTO;
import org.springframework.stereotype.Component;


@Component
public class DtoMapper {

    private MapperFacade mapper;
    
    @PostConstruct
    public void init() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
            .build();
        this.mapper = mapperFactory.getMapperFacade();
    }
    
    public List<ProductDTO> map(List<ProductEntity> src) {
        return mapper.mapAsList(src, ProductDTO.class);
    }
    
}
