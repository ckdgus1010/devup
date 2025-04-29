CREATE TABLE answers
(
    question_id BIGINT        NOT NULL,
    answer_text VARCHAR(1000) NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP     NULL    ,
    PRIMARY KEY (question_id)
);

CREATE TABLE bookmarks
(
    user_id     BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, question_id)
);

CREATE TABLE categories
(
    id       BIGINT       NOT NULL,
    category VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE categories
    ADD CONSTRAINT UQ_category UNIQUE (category);

CREATE TABLE levels
(
    id    BIGINT      NOT NULL,
    level VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE levels
    ADD CONSTRAINT UQ_level UNIQUE (level);

CREATE TABLE question_stats
(
    question_id    BIGINT NOT NULL,
    total_attempts BIGINT NOT NULL COMMENT '전체 풀이 시도 횟수',
    correct_count  BIGINT NOT NULL COMMENT '정답 횟수',
    PRIMARY KEY (question_id)
);

CREATE TABLE questions
(
    id            BIGINT        NOT NULL,
    title         VARCHAR(200)  NOT NULL,
    question_text VARCHAR(2000) NOT NULL,
    category_id   BIGINT        NOT NULL,
    level_id      BIGINT        NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at   TIMESTAMP     NULL    ,
    PRIMARY KEY (id)
);

CREATE TABLE user_answers
(
    id          BIGINT        NOT NULL,
    user_id     BIGINT        NOT NULL,
    question_id BIGINT        NOT NULL,
    answer_text VARCHAR(2000) NOT NULL,
    is_correct  BOOLEAN       NOT NULL COMMENT '정답 여부(0: 오답, 1: 정답)',
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '제출 시간',
    PRIMARY KEY (id)
);

CREATE TABLE user_correct_answers
(
    user_id     BIGINT    NOT NULL,
    question_id BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, question_id)
);

CREATE TABLE users
(
    id          BIGINT       NOT NULL,
    login_id    VARCHAR(100) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(100) NOT NULL,
    email       VARCHAR(200) NOT NULL COMMENT '이메일 인증 필요',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP    NULL    ,
    PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT UQ_login_id UNIQUE (login_id);

ALTER TABLE users
    ADD CONSTRAINT UQ_nickname UNIQUE (nickname);

ALTER TABLE users
    ADD CONSTRAINT UQ_email UNIQUE (email);

CREATE TABLE wrong_answers
(
    user_id     BIGINT    NOT NULL,
    question_id BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, question_id)
);

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

ALTER TABLE user_correct_answers
    ADD CONSTRAINT FK_users_TO_user_correct_answers
        FOREIGN KEY (user_id)
            REFERENCES users (id);

ALTER TABLE user_correct_answers
    ADD CONSTRAINT FK_questions_TO_user_correct_answers
        FOREIGN KEY (question_id)
            REFERENCES questions (id);