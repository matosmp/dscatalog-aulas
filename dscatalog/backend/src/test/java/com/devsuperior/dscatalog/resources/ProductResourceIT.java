package com.devsuperior.dscatalog.resources;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional //Para garantir que a cada teste, será feito um rollback no banco de dados
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;

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


}
