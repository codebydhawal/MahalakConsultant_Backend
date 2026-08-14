package com.mahalak.media.controller;

import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.IServices.IBlogService;
import com.mahalak.media.dto.request.BlogRequest;
import com.mahalak.media.dto.response.BlogResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import com.mahalak.media.dto.wrapper.DownloadResponse;
import com.mahalak.media.enums.BlogStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rest/blog")
@RequiredArgsConstructor
public class BlogController {

    private final IBlogService blogService;

    private final GoogleDriveService googleDriveService;

    /**
     * Add Blog
     */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BlogResponse>> addBlog(
            @Valid @RequestPart("request") BlogRequest request,
            @RequestPart("featuredImage") MultipartFile featuredImage,
            @RequestPart("blogDocument") MultipartFile blogDocument,
            @RequestPart(value = "authorImage", required = false) MultipartFile authorImage) {

        BlogResponse response = blogService.addBlog(
                request,
                featuredImage,
                blogDocument,
                authorImage);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Blog added successfully.",
                        response));
    }

    /**
     * Update Blog
     */
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlog(
            @RequestParam String blogId,
            @Valid @RequestPart(value = "request", required = false) BlogRequest request,
            @RequestPart(value = "featuredImage", required = false) MultipartFile featuredImage,
            @RequestPart(value = "blogDocument", required = false) MultipartFile blogDocument,
            @RequestPart(value = "authorImage", required = false) MultipartFile authorImage) {

        BlogResponse response = blogService.updateBlog(
                blogId,
                request,
                featuredImage,
                blogDocument,
                authorImage);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Blog updated successfully.",
                        response));
    }

    /**
     * Get Blog By Id
     */
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<BlogResponse>> getBlogById(
            @RequestParam String blogId) {

        BlogResponse response = blogService.getBlogById(blogId);

        System.out.println("this is  the content file  url" + response.getContentFileUrl());
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Blog fetched successfully.",
                        response));
    }

    /**
     * Get All Blogs
     */
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getAllBlogs() {

        List<BlogResponse> response = blogService.getAllBlogs();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Blogs fetched successfully.",
                        response));
    }

    /**
     * Search Blogs
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> searchBlogs(
            @RequestParam String keyword) {

        List<BlogResponse> response = blogService.searchBlogs(keyword);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Blogs fetched successfully.",
                        response));
    }

    /**
     * Get Blogs By Category
     */
    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogsByCategory(
            @RequestParam String category) {

        List<BlogResponse> response = blogService.getBlogsByCategory(category);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Blogs fetched successfully.",
                        response));
    }

    /**
     * Get Blogs By Status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogsByStatus(
            @RequestParam BlogStatus status) {

        List<BlogResponse> response = blogService.getBlogsByStatus(status);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Blogs fetched successfully.",
                        response));
    }

    /**
     * Update Blog Status
     */
    @PatchMapping("/update-status")
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlogStatus(
            @RequestParam String blogId,
            @RequestParam BlogStatus status) {

        BlogResponse response = blogService.updateBlogStatus(blogId, status);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Blog status updated successfully.",
                        response));
    }

    /**
     * Delete Blog (Soft Delete)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(
            @RequestParam String blogId) {

        blogService.deleteBlog(blogId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Blog deleted successfully.",
                        null));
    }

    /**
     * Increment Blog View Count
     */
    @PostMapping("/view")
    public ResponseEntity<ApiResponse<Integer>> incrementView(
            @RequestParam String blogId) {

        Integer count = blogService.incrementView(blogId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Blog view updated successfully.",
                        count));
    }

    @GetMapping("/pdf/{fileId}")
    public ResponseEntity<byte[]> getPdf(
            @PathVariable String fileId) throws Exception {

        DownloadResponse response =
                googleDriveService.download(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getMimeType()))
                .body(response.getData());
    }
}
