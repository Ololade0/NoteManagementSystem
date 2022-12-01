package com.bookmanagement.bookmanagementsystem.service;

import com.bookmanagement.bookmanagementsystem.dao.request.FindAllNoteRequest;
import com.bookmanagement.bookmanagementsystem.dto.model.Note;
import com.bookmanagement.bookmanagementsystem.exception.NoteCannotBeFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class NoteServiceImplTest {
    Note savedNote;
    @Autowired
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        Note note = Note.builder()
                .body("My Notebook Body")
                .title("My NoteBook title")
                .content("My Notes")
                .build();
        savedNote = noteService.createNoteBook(note);
    }

    @AfterEach
    void tearDown() {
        noteService.deleteAllNotes();
    }

    @Test
    void testThatNNoteCanBeRegistered() {
        Note note = Note.builder()
                .body("My Note book Body")
                .title("My NoteBook title")
                .content("My Notes")
                .build();
        noteService.createNoteBook(note);
        assertEquals(2, noteService.totalNoOfNotes());
    }

    @Test
    void testThatNNoteCanBeFindById() throws NoteCannotBeFoundException {
        Note foundNote = noteService.findNoteById(savedNote.getId());
        assertThat(foundNote.getId().equals(savedNote.getId()));
        assertThat(foundNote.getId()).isNotNull();
    }


    @Test
    void testThatNNoteCanBeDeleteById() throws NoteCannotBeFoundException {
        noteService.deleteNoteById(savedNote.getId());
        assertEquals(0, noteService.totalNoOfNotes());

    }

    @Test
    void testThatAllNoteCanBeDeleted() throws NoteCannotBeFoundException {
        noteService.deleteAllNotes();
        assertEquals(0, noteService.totalNoOfNotes());
    }

    @Test
    void testThatAllNoteCanBeFound() throws NoteCannotBeFoundException {
        FindAllNoteRequest findAllNoteRequest = FindAllNoteRequest.builder()
                .page(1)
                .limit(1)
                .build();
        noteService.findAllNotes(findAllNoteRequest);
        List<Note> noteList = noteService.findAllNote(findAllNoteRequest.getPage(), findAllNoteRequest.getLimit());
        assertEquals("My Notebook Body", noteList.get(0).getBody());
        assertEquals("My NoteBook title", noteList.get(0).getTitle());
    }

    @Test
    void testThatNoteCanBeUpdated() throws NoteCannotBeFoundException {
        Note note = new Note();
    }

}