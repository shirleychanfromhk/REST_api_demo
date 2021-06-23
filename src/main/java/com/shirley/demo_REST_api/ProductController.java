package com.shirley.demo_REST_api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {
	@Autowired
	private ProductService productService;
		 
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") String id) throws NotFoundException{
		Product product = productService.getProduct(id);
		return ResponseEntity.ok(product);
    }
    
	@GetMapping
	public ResponseEntity<List<Product>> getProducts(@ModelAttribute ProductQueryParameter param) {
		List<Product> products = productService.getProducts(param);
		System.out.println(products);
		return ResponseEntity.ok(products);
	}
	 
	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody Product request) {
		Product product = productService.createProduct(request);
		
		URI location = ServletUriComponentsBuilder
		        .fromCurrentRequest()
		        .path("/{id}")
		        .buildAndExpand(product.getId())
		        .toUri();
		
		return ResponseEntity.created(location).body(product);
	}
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<Product> replaceProduct(@PathVariable("id") String id, @RequestBody Product request) throws NotFoundException{
        Product product = productService.replaceProduct(id, request);
        return ResponseEntity.ok(product);
	 }
	 
	 @DeleteMapping("/{id}")
	 public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
	 }
	 
}