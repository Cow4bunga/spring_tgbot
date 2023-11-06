package by.levitsky.telegrambot.repository;

import by.levitsky.telegrambot.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note,Long> {
    Iterable<Note> findAllByUserId(long chatId);
}
