package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.IServices.IPaymentService;
import com.mahalak.media.auth.SecurityUtil;
import com.mahalak.media.dto.request.PaymentRejectionRequest;
import com.mahalak.media.dto.request.PaymentSubmissionRequest;
import com.mahalak.media.dto.response.PaymentResponse;
import com.mahalak.media.dto.wrapper.FileUploadResponseDto;
import com.mahalak.media.entity.Order;
import com.mahalak.media.entity.Payment;
import com.mahalak.media.enums.OrderStatus;
import com.mahalak.media.enums.PaymentMethod;
import com.mahalak.media.enums.PaymentStatus;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final GoogleEntityManager entityManager;
    private final GoogleDriveService googleDriveService;

    @Override
    public PaymentResponse createInitialPayment(String orderId, PaymentMethod paymentMethod, Double amount) {
        if (orderId == null || orderId.isBlank()) {
            throw new BadRequestException("Order ID is required.");
        }
        if (paymentMethod == null) {
            throw new BadRequestException("Payment method is required.");
        }
        if (amount == null || amount < 0) {
            throw new BadRequestException("Payment amount must be zero or greater.");
        }
        findOrder(orderId);
        if (entityManager.findAll(Payment.class).stream().anyMatch(p -> orderId.equals(p.getOrderId()))) {
            throw new BadRequestException("A payment already exists for this order.");
        }

        LocalDateTime now = LocalDateTime.now();
        Payment payment = Payment.builder()
                .orderId(orderId)
                .paymentMethod(paymentMethod.name())
                .paymentStatus(PaymentStatus.PENDING.name())
                .amount(amount)
                .createdAt(now)
                .updatedAt(now)
                .build();
        entityManager.save(payment);
        return toResponse(payment);
    }

    @Override
    public PaymentResponse getMyPayment(String paymentId) {
        Payment payment = findPayment(paymentId);
        assertCurrentUserOwnsOrder(payment.getOrderId());
        return toResponse(payment);
    }

    @Override
    public PaymentResponse submitOnlinePayment(String paymentId, PaymentSubmissionRequest request, MultipartFile screenshot) {
        Payment payment = findPayment(paymentId);
        assertCurrentUserOwnsOrder(payment.getOrderId());

        if (!PaymentMethod.ONLINE.name().equals(payment.getPaymentMethod())) {
            throw new BadRequestException("Payment proof can only be submitted for online payments.");
        }
        if (PaymentStatus.VERIFIED.name().equals(payment.getPaymentStatus())) {
            throw new BadRequestException("This payment has already been verified.");
        }
        if (screenshot == null || screenshot.isEmpty()) {
            throw new BadRequestException("Payment screenshot is required.");
        }
        if (screenshot.getContentType() == null || !screenshot.getContentType().startsWith("image/")) {
            throw new BadRequestException("Payment screenshot must be an image.");
        }

        String originalName = screenshot.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
        FileUploadResponseDto uploaded = googleDriveService.upload(
                screenshot, "PAY_" + payment.getPaymentId() + "_" + UUID.randomUUID() + extension);

        payment.setTransactionId(request.getTransactionId().trim());
        payment.setScreenshotName(uploaded.getFileName());
        payment.setScreenshotFileId(uploaded.getFileId());
        payment.setScreenshotUrl(uploaded.getDownloadUrl());
        payment.setPaymentStatus(PaymentStatus.VERIFICATION_PENDING.name());
        payment.setRejectionReason(null);
        payment.setUpdatedAt(LocalDateTime.now());
        Order order = findOrder(payment.getOrderId());
        order.setOrderStatus(OrderStatus.PAYMENT_VERIFICATION_PENDING.name());
        order.setUpdatedAt(LocalDateTime.now());
        entityManager.update(payment);
        entityManager.update(order);

        return toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsPendingVerification() {
        return entityManager.findAll(Payment.class).stream()
                .filter(payment -> PaymentStatus.VERIFICATION_PENDING.name().equals(payment.getPaymentStatus()))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse verifyPayment(String paymentId) {
        Payment payment = findPayment(paymentId);
        requireVerificationPending(payment);

        Order order = findOrder(payment.getOrderId());
        payment.setPaymentStatus(PaymentStatus.VERIFIED.name());
        payment.setVerifiedBy(SecurityUtil.getCurrentUserId().toString());
        payment.setVerifiedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CONFIRMED.name());
        order.setUpdatedAt(LocalDateTime.now());

        entityManager.update(payment);
        entityManager.update(order);
        return toResponse(payment);
    }

    @Override
    public PaymentResponse rejectPayment(String paymentId, PaymentRejectionRequest request) {
        Payment payment = findPayment(paymentId);
        requireVerificationPending(payment);

        Order order = findOrder(payment.getOrderId());
        payment.setPaymentStatus(PaymentStatus.REJECTED.name());
        payment.setVerifiedBy(SecurityUtil.getCurrentUserId().toString());
        payment.setVerifiedAt(LocalDateTime.now());
        payment.setRejectionReason(request.getRejectionReason().trim());
        payment.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PAYMENT_FAILED.name());
        order.setUpdatedAt(LocalDateTime.now());

        entityManager.update(payment);
        entityManager.update(order);
        return toResponse(payment);
    }

    private Payment findPayment(String paymentId) {
        return entityManager.findById(Payment.class, paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));
    }

    private Order findOrder(String orderId) {
        return entityManager.findById(Order.class, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));
    }

    private void assertCurrentUserOwnsOrder(String orderId) {
        Order order = findOrder(orderId);
        if (!SecurityUtil.getCurrentUserId().toString().equals(order.getUserId())) {
            throw new BadRequestException("You cannot access another user's payment.");
        }
    }

    private void requireVerificationPending(Payment payment) {
        if (!PaymentStatus.VERIFICATION_PENDING.name().equals(payment.getPaymentStatus())) {
            throw new BadRequestException("Payment is not awaiting verification.");
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .paymentMethod(PaymentMethod.valueOf(payment.getPaymentMethod()))
                .paymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus()))
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .screenshotName(payment.getScreenshotName())
                .screenshotUrl(payment.getScreenshotUrl())
                .verifiedBy(payment.getVerifiedBy())
                .verifiedAt(payment.getVerifiedAt())
                .rejectionReason(payment.getRejectionReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
