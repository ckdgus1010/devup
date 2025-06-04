CREATE TABLE answers
(
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    question_id BIGINT        NOT NULL,
    answer_text VARCHAR(1000) NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP     NULL    ,
    PRIMARY KEY (id)
); -- '정답'

CREATE TABLE bookmarks
(
    user_id     BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, question_id)
); -- '북마크'

CREATE TABLE categories
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    category VARCHAR(100) NOT NULL,
    color    VARCHAR(10)  NOT NULL COMMENT '카테고리 바에 표시할 색상',
    PRIMARY KEY (id)
); -- '기술 면접 주제'

ALTER TABLE categories
    ADD CONSTRAINT UQ_category UNIQUE (category);

CREATE TABLE levels
(
    id    BIGINT      NOT NULL AUTO_INCREMENT,
    level VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
); -- '난이도'

ALTER TABLE levels
    ADD CONSTRAINT UQ_level UNIQUE (level);

CREATE TABLE question_stats
(
    id             BIGINT NOT NULL AUTO_INCREMENT,
    question_id    BIGINT NOT NULL,
    total_attempts BIGINT NOT NULL COMMENT '전체 풀이 시도 횟수',
    correct_count  BIGINT NOT NULL COMMENT '정답 횟수',
    PRIMARY KEY (id)
); -- '문제 풀이 현황'

CREATE TABLE questions
(
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    title         VARCHAR(200)  NOT NULL,
    question_text VARCHAR(2000) NOT NULL,
    category_id   BIGINT        NOT NULL,
    level_id      BIGINT        NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at   TIMESTAMP     NULL    ,
    PRIMARY KEY (id)
); -- '기술 면접 질문'

CREATE TABLE user_answer_stats
(
    id              BIGINT    NOT NULL AUTO_INCREMENT,
    user_id         BIGINT    NOT NULL,
    question_id     BIGINT    NOT NULL,
    correct_count   INT       NOT NULL DEFAULT 0 COMMENT '맞춘 횟수',
    wrong_count     INT       NOT NULL DEFAULT 0 COMMENT '틀릿 횟수',
    first_solved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_solved_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
); -- '유저가 푼 문제 이력'

CREATE TABLE user_answers
(
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    user_id     BIGINT        NOT NULL,
    question_id BIGINT        NOT NULL,
    answer_text VARCHAR(2000) NOT NULL,
    is_correct  INT           NOT NULL COMMENT '정답 여부(0: 오답, 1: 정답)',
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '제출 시간',
    PRIMARY KEY (id)
); -- '유저가 작성한 정답'

ALTER TABLE user_answers
    ADD CONSTRAINT CHECK_is_correct CHECK ( is_correct BETWEEN 0 AND 1);

CREATE TABLE users
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    login_id    VARCHAR(100) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(100) NOT NULL,
    email       VARCHAR(200) NOT NULL COMMENT '이메일 인증 필요',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP    NULL    ,
    PRIMARY KEY (id)
); -- '사용자'

ALTER TABLE users
    ADD CONSTRAINT UQ_login_id UNIQUE (login_id);

ALTER TABLE users
    ADD CONSTRAINT UQ_nickname UNIQUE (nickname);

ALTER TABLE users
    ADD CONSTRAINT UQ_email UNIQUE (email);

CREATE TABLE wrong_answers
(
    id          BIGINT    NOT NULL AUTO_INCREMENT,
    user_id     BIGINT    NOT NULL,
    question_id BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
); -- '유저가 틀린 문제'

ALTER TABLE questions
    ADD CONSTRAINT FK_categories_TO_questions
        FOREIGN KEY (category_id)
            REFERENCES categories (id);

ALTER TABLE questions
    ADD CONSTRAINT FK_levels_TO_questions
        FOREIGN KEY (level_id)
            REFERENCES levels (id);

ALTER TABLE answers
    ADD CONSTRAINT FK_questions_TO_answers
        FOREIGN KEY (question_id)
            REFERENCES questions (id);

ALTER TABLE user_answers
    ADD CONSTRAINT FK_users_TO_user_answers
        FOREIGN KEY (user_id)
            REFERENCES users (id);

ALTER TABLE user_answers
    ADD CONSTRAINT FK_questions_TO_user_answers
        FOREIGN KEY (question_id)
            REFERENCES questions (id);

ALTER TABLE wrong_answers
    ADD CONSTRAINT FK_users_TO_wrong_answers
        FOREIGN KEY (user_id)
            REFERENCES users (id);

ALTER TABLE wrong_answers
    ADD CONSTRAINT FK_questions_TO_wrong_answers
        FOREIGN KEY (question_id)
            REFERENCES questions (id);

ALTER TABLE bookmarks
    ADD CONSTRAINT FK_users_TO_bookmarks
        FOREIGN KEY (user_id)
            REFERENCES users (id);

ALTER TABLE bookmarks
    ADD CONSTRAINT FK_questions_TO_bookmarks
        FOREIGN KEY (question_id)
            REFERENCES questions (id);

ALTER TABLE question_stats
    ADD CONSTRAINT FK_questions_TO_question_stats
        FOREIGN KEY (question_id)
            REFERENCES questions (id);

ALTER TABLE user_answer_stats
    ADD CONSTRAINT FK_users_TO_user_answer_stats
        FOREIGN KEY (user_id)
            REFERENCES users (id);

ALTER TABLE user_answer_stats
    ADD CONSTRAINT FK_questions_TO_user_answer_stats
        FOREIGN KEY (question_id)
            REFERENCES questions (id);