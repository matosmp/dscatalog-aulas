package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 5L;

        //Comportamento simulado do repository
        //Mockito.when(repository.existsById(existingId)).thenReturn(true);
        //Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        //Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExist() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });
    }




}
