package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks/* Para injetar somente o service, basta utilizar essa anotação @InjectMocks ao invés de @Autowired.
     Irá criar uma instância da classe e injeta os mocks que são criados com as anotações @Mock nessa instância. */
    private ProductService service; // A classe que estamos testando

    @Mock // Cria um mock para cada dependência
    private ProductRepository repository; // Uma dependência da classe(service) que estamos testando

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page; // Classe do Spring Data que permite retornar paginação, muito utilizado em teste
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));

        /* cast do Pageable porque há outras implementações do findAll, sobrecarga do método
        *  ArgumentMatchers.any() para receber qualquer objeto
        * .thenReturn(page) para retornar uma paginação
        **/
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);


        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        //Irá retornar um optional de product
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

        //Quando o id for inexistente irá retornar um Optional vazio
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());


        //Comportamento simulado do repository
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DataBaseException.class).when(repository).deleteById(dependentId);

    }

    @Test
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0,10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).findAll(pageable);

    }

    @Test
    public void deleteShouldDoNothingWhenIdExist() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDataBaseExceptionWhenDependentId(){

        Assertions.assertThrows(DataBaseException.class,()->{
           service.delete(dependentId);
        });
    }


}
