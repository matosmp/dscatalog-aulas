package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest // Porque está carregando o contexto da aplicação
@Transactional
// Para garantir que tenha o rollBack no banco. Essa anotação garante que após cada teste o banco de dados
// volte ao estado inicial.
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
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);
        // .count() irá retornar a quantidade total de registros no banco de dados
        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldDeleteThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0Size10() {
        //Prepando os argumentos da página inicial e quantidade de elementos por página
        PageRequest pageRequest = PageRequest.of(0, 10);

        //Buscar os dados paginados no banco de dados
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        //Assertions
        Assertions.assertFalse(result.isEmpty()); //Testar se a pagina não está vazia

        //Testar para verificar se está retornando a página inicial 0 (ZERO), getNumber() retorna o número da páginas.
        Assertions.assertEquals(0, result.getNumber());

        //Testar se a página está carregando realmente 10 elementos(objetos). getSize retorna a quantidade de elementos na página
        Assertions.assertEquals(10, result.getSize());

        //Testar se o total de produtos buscados é igual ao total countTotalProducts
        // getTotalElements() retorna a quantidade registros no banco de dados
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());

    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
        //Prepando os argumentos para retorna uma página que não existe.
        PageRequest pageRequest = PageRequest.of(50, 10);

        //Buscar os dados paginados no banco de dados
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        // Assertion
        //Deve retorna vazio porque não existe a página 50
        Assertions.assertTrue(result.isEmpty());

    }

    //Teste para verificar a ordenação da página
    @Test
    public void findAllPagedShouldReturnSortedPagedWhenSortByName() {
        //Prepando os argumentos com a página 0, com 10 objetos e ordenando por nome.
        PageRequest pageRequest = PageRequest.of(0, 10,Sort.by("name"));

        //Buscar os dados paginados no banco de dados
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        // Assertions
        //Testando se o nome do primeiro elemento no banco de dados é igual a Macbook Pro
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }
}
