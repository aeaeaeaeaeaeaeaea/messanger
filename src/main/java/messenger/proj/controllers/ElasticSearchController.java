package messenger.proj.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import messenger.proj.models.ElasticUser;
import messenger.proj.repositories.ElasticSearchQuery;

import java.io.IOException;
import java.util.List;

@RestController
public class ElasticSearchController {

    private ElasticSearchQuery elasticSearchQuery;
    
    @Autowired
    public ElasticSearchController(ElasticSearchQuery elasticSearchQuery) {
		super();
		this.elasticSearchQuery = elasticSearchQuery;
	}
    
    
    @PostMapping("/createOrUpdateDocument")
    public ResponseEntity<Object> createOrUpdateDocument(@RequestBody ElasticUser elasticUser) throws IOException {
          String response = elasticSearchQuery.createOrUpdateDocument(elasticUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    

	@GetMapping("/getDocument")
    public ResponseEntity<Object> getDocumentById(@RequestParam String userId) throws IOException {
		ElasticUser product =  elasticSearchQuery.getDocumentById(userId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/deleteDocument")
    public ResponseEntity<Object> deleteDocumentById(@RequestParam String userId) throws IOException {
        String response =  elasticSearchQuery.deleteDocumentById(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/searchDocument")
    public ResponseEntity<Object> searchAllDocument() throws IOException {
        List<ElasticUser> products = elasticSearchQuery.searchAllDocuments();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
