package edu.wctc.stockpurchase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import edu.wctc.stockpurchase.entity.StockPurchase;
import edu.wctc.stockpurchase.exception.ResourceNotFoundException;
import edu.wctc.stockpurchase.repo.StockPurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockPurchaseService {
    private StockPurchaseRepository repo;
    private ObjectMapper objectMapper;

    @Autowired
    public StockPurchaseService(StockPurchaseRepository spr,
                                ObjectMapper om) {
        this.repo = spr;
        this.objectMapper = om;
    };

    public StockPurchase patch(int id, JsonPatch patch) throws ResourceNotFoundException,
            JsonPatchException, JsonProcessingException {
        StockPurchase existingStockPurchase = getStockPurchase(id);
        JsonNode patched = patch.apply(objectMapper
                .convertValue(existingStockPurchase, JsonNode.class));
        StockPurchase patchedStockPurchase = objectMapper.treeToValue(patched, StockPurchase.class);
        repo.save(patchedStockPurchase);
        return patchedStockPurchase;
    }

    public void delete(int id) throws ResourceNotFoundException {
        if (repo.existsById(id)) {
            repo.deleteById(id);
        } else {
            throw new ResourceNotFoundException("StockPurchase", "id", id);
        }
    }

    public StockPurchase update(StockPurchase stockPurchase)
            throws ResourceNotFoundException {
        if (repo.existsById(stockPurchase.getId())) {
            return repo.save(stockPurchase);
        } else {
            throw new ResourceNotFoundException("StockPurchase", "id", stockPurchase.getId());
        }
    }

    public StockPurchase save(StockPurchase stockPurchase) {
        return repo.save(stockPurchase);
    }

    public List<StockPurchase> getAllStockPurchases() {
        List<StockPurchase> list = new ArrayList<>();
        repo.findAll().forEach(list::add);
        return list;
    }

    public StockPurchase getStockPurchase(int id) throws ResourceNotFoundException {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockPurchase", "id", id));
    }
}
