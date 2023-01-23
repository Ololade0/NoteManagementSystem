package com.bookmanagement.bookmanagementsystem.dto.repository;

import com.bookmanagement.bookmanagementsystem.dto.model.Note;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends PagingAndSortingRepository<Note, Long> {
    Optional<Note> findNoteById(Long id);
    List<Note> findNoteByContent(String content);
    List<Note> findNoteByContentContaining(String content);
}
