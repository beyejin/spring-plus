package org.example.expert.config;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) return;

        String pw = passwordEncoder.encode("Test1234!");

        User user1 = userRepository.save(new User("user1@test.com", pw, UserRole.USER, "홍길동"));
        User user2 = userRepository.save(new User("user2@test.com", pw, UserRole.USER, "김철수"));
        User admin = userRepository.save(new User("admin@test.com", pw, UserRole.ADMIN, "관리자"));

        todoRepository.save(new Todo("맑은 날 할 일", "공원 산책하기", "맑음", user1));
        todoRepository.save(new Todo("흐린 날 할 일", "실내 운동하기", "흐림", user1));
        todoRepository.save(new Todo("비 오는 날 할 일", "독서하기", "비", user2));
        todoRepository.save(new Todo("맑음 두 번째", "자전거 타기", "맑음", user2));
        todoRepository.save(new Todo("관리자 할 일", "시스템 점검", "맑음", admin));
    }
}
