package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    private Long exintingId;
    private Long countTotalProducts;

    //Inicializar a variável antes de comerçar os testes. Para facilita quando houver muitos testes
    @BeforeEach
    void setUp() throws Exception {
        exintingId = 1L;
        countTotalProducts=25L;
    }

    @Test
    public void saveShouldPersistAutoincrementWhenIdIsNull(){
        Product product = Factory.createProduct();

        product.setId(null);// Para garantir que id está null

        product = productRepository.save(product);

        Assertions.assertNotNull(product.getId()); //Verificando se não está retornando null
        Assertions.assertEquals(countTotalProducts+1,product.getId());



    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        //Arrange  --> Preparando os dados parâmetro
        //Long exintingId=1L;

        //ACT --> Realizando a deleção
        productRepository.deleteById(exintingId);

        //Assert   --> Verificando se realmente deletou, se o Optional retornar vazio significa que realmente deletou
        Optional<Product> result = productRepository.findById(exintingId);
        Assertions.assertFalse(result.isPresent()); // isPresent testa se realmente existe algum objeto dentro do optional


    }

}
