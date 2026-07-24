package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.IServices.IBlogService;
import com.mahalak.media.dto.request.BlogRequest;
import com.mahalak.media.dto.response.BlogResponse;
import com.mahalak.media.dto.wrapper.FileUploadResponseDto;
import com.mahalak.media.entity.Blog;
import com.mahalak.media.enums.BlogStatus;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.BlogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements IBlogService {

    private final GoogleEntityManager entityManager;

    private final BlogMapper blogMapper;

    private final GoogleDriveService googleDriveService;

    @Override
    public BlogResponse addBlog(BlogRequest request,
                                MultipartFile featuredImage,
                                MultipartFile document, MultipartFile authorImage) {

        Blog blog = blogMapper.toEntity(request);

        handleAuthorImage(blog, authorImage);
        handleFeaturedImage(blog, featuredImage);
        handleContentFile(blog, document);

        blog.setViews(0);
        blog.setIsBlogDeleted(false);

        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());

        System.out.println("Entity URL Before Save = " + blog.getContentFileUrl());
        entityManager.save(blog);

        return blogMapper.toResponse(blog);
    }

    @Override
    public BlogResponse updateBlog(String blogId,
                                   BlogRequest request,
                                   MultipartFile featuredImage,
                                   MultipartFile authorImage, MultipartFile document) {

        Blog blog = entityManager.findById(Blog.class, blogId)
                .orElseThrow(() ->
                        new RuntimeException("Blog not found with id : " + blogId));

        blogMapper.updateEntity(request, blog);

        handleFeaturedImage(blog, featuredImage);
        handleAuthorImage(blog, authorImage);
        handleContentFile(blog, document);

        blog.setUpdatedAt(LocalDateTime.now());

        entityManager.update(blog);

        return blogMapper.toResponse(blog);
    }

    @Override
    public BlogResponse getBlogById(String blogId) {

        Blog blog = entityManager.findById(Blog.class, blogId)
                .orElseThrow(() ->
                        new RuntimeException("Blog not found with id : " + blogId));


        System.out.println("========= AFTER READ =========");
        System.out.println("ContentFileName : " + blog.getContentFileName());
        System.out.println("ContentFileId   : " + blog.getContentFileId());
        System.out.println("ContentFileUrl  : " + blog.getContentFileUrl());
        System.out.println("==============================");

        BlogResponse response = blogMapper.toResponse(blog);

        System.out.println("========= AFTER MAPPING =========");
        System.out.println("Response URL : " + response.getContentFileUrl());
        System.out.println("Response URL : " + response.getContentFileId());
        System.out.println("================================");

        return response;
    }

    @Override
    public List<BlogResponse> getAllBlogs() {

        return entityManager.findAll(Blog.class)
                .stream()
                .filter(blog -> !Boolean.TRUE.equals(blog.getIsBlogDeleted()))
                .map(blogMapper::toResponse)
                .toList();
    }

    @Override
    public List<BlogResponse> searchBlogs(String keyword) {

        String searchKeyword = keyword.toLowerCase().trim();

        return entityManager.findAll(Blog.class)
                .stream()
                .filter(blog -> !Boolean.TRUE.equals(blog.getIsBlogDeleted()))
                .filter(blog ->
                        (blog.getTitle() != null &&
                                blog.getTitle().toLowerCase().contains(searchKeyword))
                                ||
                                (blog.getAuthorName() != null &&
                                        blog.getAuthorName().toLowerCase().contains(searchKeyword))
                )
                .map(blogMapper::toResponse)
                .toList();
    }

    @Override
    public List<BlogResponse> getBlogsByCategory(String category) {

        return entityManager.findAll(Blog.class)
                .stream()
                .filter(blog -> !Boolean.TRUE.equals(blog.getIsBlogDeleted()))
                .filter(blog -> blog.getCategory().equalsIgnoreCase(category))
                .map(blogMapper::toResponse)
                .toList();
    }

    @Override
    public List<BlogResponse> getBlogsByStatus(BlogStatus status) {

        return entityManager.findAll(Blog.class)
                .stream()
                .filter(blog -> !Boolean.TRUE.equals(blog.getIsBlogDeleted()))
                .filter(blog -> blog.getStatus() == status)
                .map(blogMapper::toResponse)
                .toList();
    }

    @Override
    public BlogResponse updateBlogStatus(String blogId,
                                         BlogStatus status) {

        Blog blog = entityManager.findById(Blog.class, blogId)
                .orElseThrow(() ->
                        new RuntimeException("Blog not found."));

        blog.setStatus(status);
        blog.setUpdatedAt(LocalDateTime.now());

        entityManager.update(blog);

        return blogMapper.toResponse(blog);
    }

    @Override
    public void deleteBlog(String blogId) {

        Blog blog = entityManager.findById(Blog.class, blogId)
                .orElseThrow(() ->
                        new RuntimeException("Blog not found."));

        blog.setIsBlogDeleted(true);
        blog.setUpdatedAt(LocalDateTime.now());

        entityManager.update(blog);
    }

    @Override
    public Integer incrementView(String blogId) {

        Blog blog = entityManager.findById(Blog.class, blogId)
                .orElseThrow(() ->
                        new RuntimeException("Blog not found."));

        blog.setViews(blog.getViews() + 1);

        entityManager.update(blog);

        return blog.getViews();
    }

    private void handleFeaturedImage(Blog blog, MultipartFile featuredImage) {

        if (featuredImage != null && !featuredImage.isEmpty()) {

            String originalName = featuredImage.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String generatedFileName = "FTAR_" + UUID.randomUUID() + extension;

            FileUploadResponseDto driveFile =
                    googleDriveService.upload(featuredImage, generatedFileName);

            blog.setFeaturedImageName(generatedFileName);
            blog.setAuthorImageFileId(driveFile.getFileId());
            blog.setFeaturedImageUrl(driveFile.getDownloadUrl());
        }
    }

    private void handleAuthorImage(Blog blog, MultipartFile authorImage) {
        if (authorImage != null && !authorImage.isEmpty()) {

            String originalName = authorImage.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String generatedFileName = "AUTH_" + UUID.randomUUID() + extension;

            FileUploadResponseDto driveFile =
                    googleDriveService.upload(authorImage, generatedFileName);

            blog.setAuthorImageName(generatedFileName);
            blog.setAuthorImageFileId(driveFile.getFileId());
            blog.setAuthorImageUrl(driveFile.getDownloadUrl());

        }
    }

    private void handleContentFile(Blog blog, MultipartFile document) {
        if (document != null && !document.isEmpty()) {

            String originalName = document.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String generatedFileName = "BLOG_" + originalName + extension;

            FileUploadResponseDto driveFile =
                    googleDriveService.upload(document, generatedFileName);

            System.out.println("============== PDF Upload ==============");
            System.out.println("File Id      : " + driveFile.getFileId());
            System.out.println("Download Url : " + driveFile.getDownloadUrl());
            System.out.println("========================================");

            blog.setContentFileName(generatedFileName);
            blog.setContentFileId(driveFile.getFileId());
            blog.setContentFileUrl(driveFile.getDownloadUrl());

        }
    }
}
