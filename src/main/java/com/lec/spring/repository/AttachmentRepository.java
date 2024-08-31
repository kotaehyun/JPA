package com.lec.spring.repository;

import com.lec.spring.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    // 특정 글(postId) 의 첨부파일들
    List<Attachment> findByPost(Long postId);
}
