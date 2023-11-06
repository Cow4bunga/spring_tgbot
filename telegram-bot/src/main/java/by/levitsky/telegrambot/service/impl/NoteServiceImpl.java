package by.levitsky.telegrambot.service.impl;

import by.levitsky.telegrambot.dto.NoteDto;
import by.levitsky.telegrambot.exception.ResourceNotFoundException;
import by.levitsky.telegrambot.model.Note;
import by.levitsky.telegrambot.model.User;
import by.levitsky.telegrambot.repository.NoteRepository;
import by.levitsky.telegrambot.repository.UserRepository;
import by.levitsky.telegrambot.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public void createNote(NoteDto noteDto, long chatId) {
        Note note = mapToEntity(noteDto);
        User user = userRepository
                .findById(chatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "ID", chatId));
        note.setUser(user);
        noteRepository.save(note);
//        Note newNote = note;
//        User user = userRepository.findById(chatId).get();
//        newNote.setUser(user);
//        noteRepository.save(newNote);
    }

    @Override
    public Iterable<NoteDto> getAllNotes() {
        Iterable<Note> notes1= noteRepository.findAll();
        List<Note> notes= (List<Note>) notes1;
        return notes.stream().map(note->mapToDto(note))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<NoteDto> getAllNotesByUser(long chatId) {
        Iterable<Note> notes1= noteRepository.findAllByUserId(chatId);
        List<Note> notes= (List<Note>) notes1;
        return notes.stream().map(note->mapToDto(note))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NoteDto getNoteById(long chatId, long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note", "ID", noteId));

        User user = userRepository.findById(chatId).orElseThrow(() ->
                new ResourceNotFoundException("User", "ID", chatId));

        if (!Objects.equals(note.getUser().getId(), user.getId())) {
            throw new ResourceNotFoundException("Note", "ID", noteId);
        }

        return mapToDto(note);
    }

    @Override
    public boolean updateNote(long noteId, long chatId, NoteDto newNoteDto) {
        if (!noteRepository.existsById(noteId) || !userRepository.existsById(chatId)) {
            return false;
        }
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note", "ID", noteId));

        User user = userRepository.findById(chatId).orElseThrow(() ->
                new ResourceNotFoundException("User", "ID", chatId));

        if (!Objects.equals(note.getUser().getId(), user.getId())) {
            throw new ResourceNotFoundException("Note", "ID", noteId);
        }
        note.setTitle(newNoteDto.getTitle());
        note.setNote(newNoteDto.getNote());

        noteRepository.save(note);
        return true;
    }

    @Override
    public void deleteNote(long chatId, long noteId) {
        Note note = mapToEntity(getNoteById(chatId, noteId));
        noteRepository.delete(note);
    }

    private NoteDto mapToDto(Note note) {
        NoteDto noteDto = new NoteDto();
        noteDto.setId(note.getId());
        noteDto.setNote(note.getNote());
        noteDto.setTitle(note.getTitle());
        return noteDto;
    }

    private Note mapToEntity(NoteDto noteDto) {
        Note note = new Note();
        note.setId(noteDto.getId());
        note.setTitle(noteDto.getTitle());
        note.setNote(noteDto.getNote());
        return note;
    }
}
