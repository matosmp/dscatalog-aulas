package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional //Para garantir que a cada teste, será feito um rollback no banco de dados
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;
    private ProductDTO productDTO = Factory.createProductDTO(); // Criar um produto

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L; // 25 é número de registro que está no banco de dados
    }

    @Test
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));
        //Assertions

        //Deve retornar status HTTP 200 quando realizar a requisição
        result.andExpect(status().isOk());

        result.andExpect((ResultMatcher) jsonPath("$.totalElements").value(countTotalProducts));/* jsonPath para acessar
         * o objeto da resposta com a expressão &.+nome da propriedade. */

        //Testando se existe o content no corpo da resposta
        result.andExpect(jsonPath("$.content").exists());

        //Testando se página está retornando ordenado por nome corretamente
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));


    }


    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        //Convertendo o JSON em um objeto usando objectMapper
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        // Copiar o nome e description anterior antes de realizar o update para depois verificar se foi alterado corretamente
        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody) // Conteúdo que será enviado na requisição
                        .contentType(MediaType.APPLICATION_JSON) // Tipo que será enviado na requisição
                        .accept(MediaType.APPLICATION_JSON));
        //Assertions
        result.andExpect(status().isOk());

        //Testando se o objeto que voltou no campo de resposta tem as propriedades abaixo
        result.andExpect(jsonPath("$.id").exists());//Testando se o retorno do JSON tem Oo id
        result.andExpect(jsonPath("$.name").value(expectedName));
        result.andExpect(jsonPath("$.description").value(expectedDescription));

    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        //Convertendo o JSON em um objeto usando objectMapper
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody) // Conteúdo que será enviado na requisição
                        .contentType(MediaType.APPLICATION_JSON) // Tipo que será enviado na requisição
                        .accept(MediaType.APPLICATION_JSON));

        //Assertion para testar se irá ocorrer uma exceção
        result.andExpect(status().isNotFound());


    }

}
