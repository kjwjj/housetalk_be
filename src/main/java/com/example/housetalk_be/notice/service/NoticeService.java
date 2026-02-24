package com.example.housetalk_be.notice.service;

import com.example.housetalk_be.notice.entity.Notice;
import com.example.housetalk_be.notice.repository.NoticeRepository;
import com.example.housetalk_be.user.service.UserService;
import com.example.housetalk_be.common.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserService userService;

    public NoticeService(NoticeRepository noticeRepository, UserService userService) {
        this.noticeRepository = noticeRepository;
        this.userService = userService;
    }

    // 전체 공지 (페이징)
    public Page<Notice> getAllNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    // 단일 공지
    public Notice getNotice(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "id", id));
    }

    // 생성
    @Transactional
    public Notice createNotice(Notice notice, String email) {
        notice.setAuthor(email);
        notice.setCreatedAt(java.time.LocalDateTime.now());
        return noticeRepository.save(notice);
    }

    // 수정
    @Transactional
    public Notice updateNotice(Long id, Notice updatedNotice, String email) {
        Notice notice = getNotice(id);

        // 작성자만 수정 가능
        if (!notice.getAuthor().equals(email)) {
            throw new IllegalArgumentException("수정 권한 없음");
        }

        notice.setTitle(updatedNotice.getTitle());
        notice.setContent(updatedNotice.getContent());
        notice.setStatus(updatedNotice.getStatus());
        notice.setUpdatedAt(java.time.LocalDateTime.now());
        return noticeRepository.save(notice);
    }

    // 삭제
    @Transactional
    public void deleteNotice(Long id, String email, boolean isAdmin) {
        Notice notice = getNotice(id);

        boolean isAuthor = notice.getAuthor().equals(email);
        if (!(isAuthor || isAdmin)) {
            throw new IllegalArgumentException("삭제 권한 없음");
        }

        noticeRepository.delete(notice);
    }
}