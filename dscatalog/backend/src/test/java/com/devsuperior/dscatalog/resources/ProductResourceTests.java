package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.List;

@WebMvcTest(ProductResource.class) //Nome da classe que será testada
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;

    //PageImpl é uma implementação concreta da interface Page.
    private PageImpl<ProductDTO> page;

    private Long existingId;
    private Long noExistingId;
    private Long dependentId;

    //Simular o comportamento do service
    @BeforeEach
    public void setUp() throws Exception {

        existingId = 1L;
        noExistingId = 2L;
        dependentId=3L;

        productDTO = Factory.createProductDTO(); // Criar um produto
        page = new PageImpl<>(List.of(productDTO)); //Com PageImpl é possível instanciar um objeto.
        when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(noExistingId)).thenThrow(ResourceNotFoundException.class);

        when(service.update(eq(existingId), any())).thenReturn(productDTO);
        when(service.update(eq(noExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(service).delete(noExistingId);
        doThrow(DataBaseException.class).when(service).delete(dependentId);


    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        // perform faz uma requisição
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk()); //Testando status HTTP 200
        result.andExpect(jsonPath("$.id").exists());//Testando se o retorno do JSON tem Oo id
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());

    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/products/{id}", noExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());

    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        //Convertendo o JSON em um objeto usando objectMapper
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody) // Conteúdo que será enviado na requisição
                        .contentType(MediaType.APPLICATION_JSON) // Tipo que será enviado na requisição
                        .accept(MediaType.APPLICATION_JSON));

        //Assertions
        result.andExpect(status().isOk());

        //Testando se o objeto que voltou no campo de resposta tem as propriedades abaixo
        result.andExpect(jsonPath("$.id").exists());//Testando se o retorno do JSON tem Oo id
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldReturnNotFoundExceptionWhenIdDoesNotExist() throws Exception {

        //Convertendo o JSON em um objeto usando objectMapper
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", noExistingId)
                        .content(jsonBody) // Conteúdo que será enviado na requisição
                        .contentType(MediaType.APPLICATION_JSON) // Tipo que será enviado na requisição
                        .accept(MediaType.APPLICATION_JSON));

        //Assertions
        result.andExpect(status().isNotFound());
    }

    
}
