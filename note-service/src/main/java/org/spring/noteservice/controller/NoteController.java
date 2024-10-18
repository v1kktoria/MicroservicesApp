package org.spring.noteservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.noteservice.model.Note;
import org.spring.noteservice.service.NoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public List<Note> getAllNotes(@RequestHeader("Authorization") String token) {
        return noteService.getAllNotes(token);
    }

    @PostMapping
    public Note createNote(@RequestHeader("Authorization") String token, @Valid @RequestBody Note note) {
        return noteService.createNote(note, token);
    }

    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id, @Valid @RequestBody Note note, @RequestHeader("Authorization") String token) {
        return noteService.updateNote(id, note, token);
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        noteService.deleteNote(id, token);
    }
}

