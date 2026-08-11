package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.IShippingRuleService;
import com.mahalak.media.dto.request.ShippingRuleRequest;
import com.mahalak.media.dto.response.ShippingRuleResponse;
import com.mahalak.media.entity.ShippingRule;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.ShippingRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShippingRuleServiceImpl implements IShippingRuleService {

    private final GoogleEntityManager entityManager;
    private final ShippingRuleMapper shippingRuleMapper;

    @Override
    public ShippingRuleResponse addShippingRule(ShippingRuleRequest request) {
        validateEffectiveDates(request);
        ensureNameIsAvailable(request.getName(), null);

        ShippingRule shippingRule = shippingRuleMapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        shippingRule.setCreatedAt(now);
        shippingRule.setUpdatedAt(now);
        entityManager.save(shippingRule);

        return shippingRuleMapper.toResponse(shippingRule);
    }

    @Override
    public ShippingRuleResponse getShippingRuleById(String shippingId) {
        return shippingRuleMapper.toResponse(findShippingRule(shippingId));
    }

    @Override
    public List<ShippingRuleResponse> getAllShippingRules() {
        return entityManager.findAll(ShippingRule.class).stream()
                .map(shippingRuleMapper::toResponse)
                .toList();
    }

    @Override
    public ShippingRuleResponse updateShippingRule(String shippingId, ShippingRuleRequest request) {
        validateEffectiveDates(request);
        ShippingRule shippingRule = findShippingRule(shippingId);
        ensureNameIsAvailable(request.getName(), shippingId);

        shippingRuleMapper.updateEntity(request, shippingRule);
        shippingRule.setUpdatedAt(LocalDateTime.now());
        entityManager.update(shippingRule);

        return shippingRuleMapper.toResponse(shippingRule);
    }

    @Override
    public ShippingRuleResponse deleteShippingRule(String shippingId) {
        ShippingRule shippingRule = findShippingRule(shippingId);
        entityManager.delete(ShippingRule.class, shippingId);
        return shippingRuleMapper.toResponse(shippingRule);
    }

    private ShippingRule findShippingRule(String shippingId) {
        return entityManager.findById(ShippingRule.class, shippingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipping rule not found with id: " + shippingId));
    }

    private void ensureNameIsAvailable(String name, String excludedShippingId) {
        boolean exists = entityManager.findAll(ShippingRule.class).stream()
                .anyMatch(shippingRule -> shippingRule.getName() != null
                        && shippingRule.getName().equalsIgnoreCase(name.trim())
                        && !Objects.equals(shippingRule.getShippingId(), excludedShippingId));
        if (exists) {
            throw new BadRequestException("Shipping rule with name '" + name + "' already exists.");
        }
    }

    private void validateEffectiveDates(ShippingRuleRequest request) {
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date.");
        }
    }
}
