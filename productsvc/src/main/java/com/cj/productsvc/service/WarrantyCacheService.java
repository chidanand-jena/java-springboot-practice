package com.cj.productsvc.service;

import com.cj.productsvc.model.WarrantyInfo;
import com.cj.productsvc.repo.WarrantyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarrantyCacheService {
    private static final String WARRANTY_SET_KEY = "warranty:ids";

    private final RedisTemplate<String, Object> redisTemplate;
    private final WarrantyRepository warrantyRepository;

    public boolean isWarrantyExists(Long warrantyId) {
        Boolean cached = redisTemplate.opsForSet().isMember(WARRANTY_SET_KEY,warrantyId );
        if(Boolean.TRUE.equals(cached)){
            return true;
        }
        // Not in cache, check DB
        boolean existsInDb = warrantyRepository.existsById(warrantyId);
        if (existsInDb) {
            // Add to cache for future requests
            redisTemplate.opsForSet().add(WARRANTY_SET_KEY, warrantyId);
        }
        return existsInDb;
    }

    /**
     * Evict warrantyId from cache when warranty is deleted/invalidated.
     */
    public void evictWarranty(Long warrantyId) {
        redisTemplate.opsForSet().remove(WARRANTY_SET_KEY,warrantyId);
    }


    public void clearWarrantyCache() {
        redisTemplate.delete(WARRANTY_SET_KEY);
    }

    @PostConstruct
    public void preloadWarrantyIdsToCache() {
        redisTemplate.delete(WARRANTY_SET_KEY);
        List<Long> ids = warrantyRepository.findAllIds();
        List<String> stringIds = ids.stream()
                .map(String::valueOf)
                .toList();
        redisTemplate.opsForSet().add(WARRANTY_SET_KEY, stringIds.toArray());
        log.info("preloadWarrantyIdsToCache completed: WARRANTY_SET_KEY :{} ", redisTemplate.opsForSet().members(WARRANTY_SET_KEY));
    }

}
