package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.BlogRequest;
import com.mahalak.media.dto.response.BlogResponse;
import com.mahalak.media.enums.BlogStatus;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBlogService {
    BlogResponse addBlog(@Valid BlogRequest request, MultipartFile featuredImage, MultipartFile blogDocument, MultipartFile authorImage);

    BlogResponse updateBlog(String blogId, @Valid BlogRequest request, MultipartFile featuredImage, MultipartFile authorImage, MultipartFile image);

    BlogResponse getBlogById(String blogId);

    List<BlogResponse> getAllBlogs();

    List<BlogResponse> searchBlogs(String keyword);

    List<BlogResponse> getBlogsByCategory(String category);

    List<BlogResponse> getBlogsByStatus(BlogStatus status);

    BlogResponse updateBlogStatus(String blogId, BlogStatus status);

    void deleteBlog(String blogId);

    Integer incrementView(String blogId);
}
