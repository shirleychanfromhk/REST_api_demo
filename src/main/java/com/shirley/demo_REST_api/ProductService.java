package com.shirley.demo_REST_api;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
	
	@Autowired
    private ProductRepository repository;
	
	public Product createProduct(Product request) throws ConflictException{
		Product product = new Product();
	    product.setName(request.getName());
	    product.setPrice(request.getPrice());

	    if(repository.existsByName(request.getName())){
	    	throw new ConflictException("The product already exists.");
	    }
	    return repository.insert(product);
	}

	public Product getProduct(String id) throws NotFoundException{
		return repository.findById(id)
	            .orElseThrow(() -> new NotFoundException("Can't find product."));
	}

	public Product replaceProduct(String id, Product request) throws NotFoundException{
		Product oldProduct = getProduct(id);

	    Product product = new Product();
	    product.setId(oldProduct.getId());
	    product.setName(request.getName());
	    product.setPrice(request.getPrice());

	    return repository.save(product);
	}

	public void deleteProduct(String id) throws NotFoundException{
		if(!repository.existsById(id)) {
			throw new NotFoundException("Can't find product.");
		}else {
			repository.deleteById(id);
		}
	}

	public List<Product> getProducts(ProductQueryParameter param) {
	    String nameKeyword = Optional.ofNullable(param.getKeyword()).orElse("");
	    String orderBy = param.getOrderBy();
	    String sortRule = param.getSortRule();

	    Sort sort = Sort.unsorted();
	    if (Objects.nonNull(orderBy) && Objects.nonNull(sortRule)) {
	        Sort.Direction direction = Sort.Direction.fromString(sortRule);
	        sort = Sort.by(direction, orderBy);
	    }

	    return repository.findByNameLikeIgnoreCase(nameKeyword, sort);
	}
	
}
