package by.levitsky.telegrambot.repository;

import by.levitsky.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
