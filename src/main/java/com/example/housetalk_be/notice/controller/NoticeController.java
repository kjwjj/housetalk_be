package com.example.housetalk_be.notice.controller;

import com.example.housetalk_be.notice.entity.Notice;
import com.example.housetalk_be.notice.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    public Notice createNotice(@RequestBody Notice notice, Authentication authentication) {
        return noticeService.createNotice(notice, authentication.getName());
    }

    @GetMapping
    public Page<Notice> getAllNotices(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "3") int size) {
        return noticeService.getAllNotices(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Notice getNotice(@PathVariable Long id) {
        return noticeService.getNotice(id);
    }

    @PutMapping("/{id}")
    public Notice updateNotice(@PathVariable Long id,
                               @RequestBody Notice updatedNotice,
                               Authentication authentication) {
        return noticeService.updateNotice(id, updatedNotice, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public void deleteNotice(@PathVariable Long id,
                             Authentication authentication,
                             @RequestParam(defaultValue = "false") boolean isAdmin) {
        noticeService.deleteNotice(id, authentication.getName(), isAdmin);
    }
}