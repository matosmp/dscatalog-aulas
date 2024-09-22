package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



import java.util.List;

@WebMvcTest(ProductResource.class) //Nome da classe que será testada
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    private ProductDTO productDTO;

    //PageImpl é uma implementação concreta da interface Page.
    private PageImpl<ProductDTO> page;

    //Simular o comportamento do service
    @BeforeEach
    public void setUp() throws Exception {

        productDTO = Factory.createProductDTO(); // Criar um produto
        page = new PageImpl<>(List.of(productDTO)); //Com PageImpl é possível instanciar um objeto.

        Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        // perform faz uma requisição
        mockMvc.perform(get("/products")).andExpect(status().isOk());       //(get("/products"))   .andExpect(status().isOk());


    }

}
