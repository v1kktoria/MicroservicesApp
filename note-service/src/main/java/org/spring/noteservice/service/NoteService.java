package org.spring.noteservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.spring.noteservice.annotations.RequiresAuthorization;
import org.spring.noteservice.client.UserClient;
import org.spring.noteservice.exception.InvalidTokenException;
import org.spring.noteservice.exception.ResourceNotFoundException;
import org.spring.noteservice.exception.UnauthorizedAccessException;
import org.spring.noteservice.model.Note;
import org.spring.noteservice.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;

    @RequiresAuthorization
    public List<Note> getAllNotes(String token) {
        Long userId = getUserIdFromToken(token);
        return noteRepository.findByUserId(userId);
    }

    @RequiresAuthorization
    public Note createNote(Note note, String token) {
        Long userId = getUserIdFromToken(token);
        note.setUserId(userId);
        return noteRepository.save(note);
    }

    @RequiresAuthorization
    public Note updateNote(Long id, Note note, String token) {
        Long userId = getUserIdFromToken(token);
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        if (!existingNote.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to update this note");
        }
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        return noteRepository.save(existingNote);
    }

    @RequiresAuthorization
    public void deleteNote(Long id, String token) {
        Long userId = getUserIdFromToken(token);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        if (!note.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this note");
        }
        noteRepository.delete(note);
    }

    private Long getUserIdFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            DecodedJWT decodedJWT = JWT.decode(token);
            if (decodedJWT.getClaim("userId").isNull()) {
                throw new RuntimeException("userId claim is missing in token");
            }
            return decodedJWT.getClaim("userId").asLong();
        } catch (Exception e) {
            throw new InvalidTokenException("Failed to decode token");
        }
    }
}
