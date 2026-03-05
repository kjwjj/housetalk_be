package com.example.housetalk_be.board.repository;

import com.example.housetalk_be.board.entity.Board;
import com.example.housetalk_be.board.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 일반 댓글 + 페이징
    Page<Comment> findByBoardAndParentIsNull(Board board, Pageable pageable);

    // 일반 댓글 전체 조회
    List<Comment> findByBoardAndParentIsNull(Board board);

    // 대댓글 조회
    List<Comment> findByParent(Comment parent);

    // 🔥 부모 댓글 + 자식 댓글을 한 번에 fetch join
    @Query("SELECT DISTINCT c FROM Comment c " +
            "JOIN FETCH c.user u " +         // 부모 댓글 작성자 fetch
            "LEFT JOIN FETCH c.replies r " +
            "LEFT JOIN FETCH r.user ru " +   // 대댓글 작성자 fetch
            "WHERE c.board = :board AND c.parent IS NULL")
    List<Comment> findParentCommentsWithReplies(@Param("board") Board board);
}