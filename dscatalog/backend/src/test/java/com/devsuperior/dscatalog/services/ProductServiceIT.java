package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // Porque está carregando o contexto da aplicação
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L; // 25 é número de registro que está no banco de dados
    }


    @Test
    public void deleteShouldDeleteResourceWhenIdExists(){
        service.delete(existingId);

        // .count() irá retornar a quantidade total de registros no banco de dados
        Assertions.assertEquals(countTotalProducts -1 , repository.count());
    }

    @Test
    public void deleteShouldDeleteThrowResourceNotFoundExceptionWhenIdDoesNotExists(){

      Assertions.assertThrows(ResourceNotFoundException.class,()->{
          service.delete(nonExistingId);
      });

    }


}
