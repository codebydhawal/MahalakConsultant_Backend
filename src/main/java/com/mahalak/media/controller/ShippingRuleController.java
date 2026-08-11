package com.mahalak.media.controller;

import com.mahalak.media.IServices.IShippingRuleService;
import com.mahalak.media.dto.request.ShippingRuleRequest;
import com.mahalak.media.dto.response.ShippingRuleResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/shipping-rule")
@RequiredArgsConstructor
public class ShippingRuleController {

    private final IShippingRuleService shippingRuleService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ShippingRuleResponse>> addShippingRule(
            @Valid @RequestBody ShippingRuleRequest request) {
        ShippingRuleResponse response = shippingRuleService.addShippingRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), "Shipping rule added successfully.", response));
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<ShippingRuleResponse>> getShippingRuleById(
            @RequestParam String shippingId) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Shipping rule fetched successfully.", shippingRuleService.getShippingRuleById(shippingId)));
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<ShippingRuleResponse>>> getAllShippingRules() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Shipping rules fetched successfully.", shippingRuleService.getAllShippingRules()));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<ShippingRuleResponse>> updateShippingRule(
            @RequestParam String shippingId, @Valid @RequestBody ShippingRuleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Shipping rule updated successfully.",
                shippingRuleService.updateShippingRule(shippingId, request)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<ShippingRuleResponse>> deleteShippingRule(
            @RequestParam String shippingId) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Shipping rule deleted successfully.", shippingRuleService.deleteShippingRule(shippingId)));
    }
}
