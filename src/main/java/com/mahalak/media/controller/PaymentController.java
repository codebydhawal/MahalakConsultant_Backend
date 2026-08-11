package com.mahalak.media.controller;

import com.mahalak.media.IServices.IPaymentService;
import com.mahalak.media.dto.request.PaymentRejectionRequest;
import com.mahalak.media.dto.request.PaymentSubmissionRequest;
import com.mahalak.media.dto.response.PaymentResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rest/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<PaymentResponse>> getMyPayment(@RequestParam String paymentId) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment fetched successfully.",
                paymentService.getMyPayment(paymentId)));
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PaymentResponse>> submitOnlinePayment(
            @RequestParam String paymentId,
            @Valid @RequestPart("request") PaymentSubmissionRequest request,
            @RequestPart("screenshot") MultipartFile screenshot) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment submitted for verification.",
                paymentService.submitOnlinePayment(paymentId, request, screenshot)));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPendingPayments() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Pending payments fetched successfully.",
                paymentService.getPaymentsPendingVerification()));
    }

    @PatchMapping("/admin/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(@RequestParam String paymentId) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment verified successfully.",
                paymentService.verifyPayment(paymentId)));
    }

    @PatchMapping("/admin/reject")
    public ResponseEntity<ApiResponse<PaymentResponse>> rejectPayment(
            @RequestParam String paymentId,
            @Valid @RequestBody PaymentRejectionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment rejected successfully.",
                paymentService.rejectPayment(paymentId, request)));
    }
}
