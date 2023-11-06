package by.levitsky.telegrambot.service;

import by.levitsky.telegrambot.dto.NoteDto;

public interface NoteService {
    void createNote(NoteDto noteDto, long chatId);
    Iterable<NoteDto> getAllNotes();
    Iterable<NoteDto> getAllNotesByUser(long chatId);
    NoteDto getNoteById(long chatId, long noteId);
    boolean updateNote(long noteId, long chatId, NoteDto newNoteDto);
    void deleteNote(long chatId, long noteId);
}
