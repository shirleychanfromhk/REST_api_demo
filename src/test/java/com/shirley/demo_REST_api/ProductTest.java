package com.shirley.demo_REST_api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductTest {
	private HttpHeaders httpHeaders;
	
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeAll
    public void init() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
    }
   @BeforeAll
    public void clear() {
        productRepository.deleteAll();
    }

    
    @Test
    public void testCreateProduct() throws Exception {
    	HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        JSONObject request = new JSONObject();
        request.put("name", "Harry Potter2");
        request.put("price", 550);
        
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders
                        .post("/")
                        .headers(httpHeaders)
                        .content(request.toString());
        
        mockMvc.perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").hasJsonPath())
        .andExpect(jsonPath("$.name").value(request.getString("name")))
        .andExpect(jsonPath("$.price").value(request.getInt("price")))
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
    
    @Test
    public void testGetProduct() throws Exception {

    	Product product = createProduct("Economics", 450);
        productRepository.insert(product);

        mockMvc.perform(get("/" + product.getId())
                .headers(httpHeaders))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice()));

    }
    
    @Test
    public void testReplaceProduct() throws Exception {
        Product product = createProduct("Economics", 450);
        productRepository.insert(product);

        JSONObject request = new JSONObject()
                .put("name", "Macroeconomics")
                .put("price", 550);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders
                        .put("/" + product.getId())
                        .headers(httpHeaders)
                        .content(request.toString());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(request.getString("name")))
                .andExpect(jsonPath("$.price").value(request.getInt("price")));
    }
    
    @Test
    public void testDeleteProduct() throws Exception{
        Product product = createProduct("Economics", 450);
        productRepository.insert(product);

        mockMvc.perform(delete("/" + product.getId())
                .headers(httpHeaders))
                .andExpect(status().isNoContent());

        Exception exception = assertThrows(NotFoundException.class,  () -> productService.getProduct(product.getId()));
        
    }
    
    @Test
    public void testSearchProductsSortByPriceAsc() throws Exception {
        Product p1 = createProduct("Operation Management", 350);
        Product p2 = createProduct("Marketing Management", 200);
        Product p3 = createProduct("Human Resource Management", 420);
        Product p4 = createProduct("Finance Management", 400);
        Product p5 = createProduct("Enterprise Resource Planning", 440);
        productRepository.insert(Arrays.asList(p1, p2, p3, p4, p5));
        
        MvcResult result = mockMvc.perform(get("/")
                .headers(httpHeaders)
                .param("keyword", "Manage")
                .param("orderBy", "price")
                .param("sortRule", "asc"))
                .andReturn();
        
        MockHttpServletResponse mockHttpResponse = result.getResponse();
        String responseJSONStr = mockHttpResponse.getContentAsString();
        JSONArray productJSONArray = new JSONArray(responseJSONStr);
        
        List<String> productIds = new ArrayList<>();
        for (int i = 0; i < productJSONArray.length(); i++) {
            JSONObject productJSON = productJSONArray.getJSONObject(i);
            productIds.add(productJSON.getString("id"));
        }
            
            assertEquals(4, productIds.size());
            assertEquals(p2.getId(), productIds.get(0));
            assertEquals(p1.getId(), productIds.get(1));
            assertEquals(p4.getId(), productIds.get(2));
            assertEquals(p3.getId(), productIds.get(3));

            assertEquals(HttpStatus.OK.value(), mockHttpResponse.getStatus());
            assertEquals(MediaType.APPLICATION_JSON_VALUE,
                mockHttpResponse.getHeader(HttpHeaders.CONTENT_TYPE));

    }
    
    private Product createProduct(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
