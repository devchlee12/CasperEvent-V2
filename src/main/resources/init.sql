INSERT INTO `member` (`phone_number`, `car`, `role`, `tool_box_cnt`) VALUES
                                                                         ('01012345678', 1, 'user1', 3),
                                                                         ('01011111111', 1, 'user2', 3),
                                                                         ('01022222222', 1, 'user3', 3),
                                                                         ('01033333333', 1, 'user4', 3),
                                                                         ('01044444444', 1, 'user5', 3);

insert into event_day_info (event_day, event_date)
values (1, CURDATE());

### 퀴즈

insert into quiz_content (id, quiz_date, quiz_description, quiz_question_1, quiz_question_2,
                          quiz_question_3, quiz_question_4)
values (1, CURDATE(), '캐스퍼 EV는 OOO를 통해 고급스러운 분위기를 연출할 수 있어요!', '앰비언트 무드램프', '와인', '촛불', '향수');

insert into quiz_content (id, quiz_date, quiz_description, quiz_question_1, quiz_question_2,
                          quiz_question_3, quiz_question_4)
values (2, CURDATE() - INTERVAL 1 DAY, '캐스퍼 EV는 OOO를 통해 고급스러운 분위기를 연출할 수 있어요!!', '앰비언트 무드램프', '와인', '촛불', '향수');

-- 오늘의 퀴즈 컨텐츠의 id와 맞춰야함
insert into quiz_info (id, answer_num, quiz_image)
values (1, 1, 'quiz_image');

##### 선착순 경품 만들기 프로시저
DELIMITER //

CREATE PROCEDURE insert_quiz_rewards()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 500 DO
            INSERT INTO quiz_reward (
                id,
                quiz_date,
                reward_image,
                success_order,
                valid
            ) VALUES (
                         i,
                         CURDATE(),
                         CONCAT('reward_image_', i),
                         i,
                         TRUE
                     );
            SET i = i + 1;
        END WHILE;
END //

DELIMITER ;

### 응모

INSERT INTO draw_daily_message_info
(common_scenario, draw_date, lose_image, lose_message, lose_scenario, win_image, win_message)
VALUES
    ('캐스퍼 EV와 떠날 시간! 까먹고 차키를 안가져왔네요.. 어쩌죠..', CURDATE(), '/images/lose1.png', '망치로는 차 문을 열 수 없어요.. 아마도요..', '스마트폰, 스마트워치를 활용해 쉽게 열 수 있어요!', '/images/win1.png', '스마트폰, 스마트워치를 활용해 쉽게 열 수 있어요!');


INSERT INTO `draw_probability` (`ranking`, `probability`) VALUES
                                                              (1, 1),
                                                              (2, 5),
                                                              (3, 10),
                                                              (4, 100),
                                                              (0, 1000);

INSERT INTO `draw_reward_info` (`ranking`, `image`, `name`, `stock`) VALUES
                                                                         (1, 'https://example.com/images/reward1.png', '1등 상품 - 아이패드', 1),
                                                                         (2, 'https://example.com/images/reward2.png', '2등 상품 - 에어팟', 5),
                                                                         (3, 'https://example.com/images/reward3.png', '3등 상품 - 스타벅스 기프티콘', 10),
                                                                         (4, 'https://example.com/images/reward4.png', '4등 상품 - 편의점 1천원 쿠폰', 100),
                                                                         (0, 'https://example.com/images/reward5.png', '꽝', 0);

### 상품 정보
-- 1등 (1개)
INSERT INTO `draw_prize` (`image`, `owner`, `valid`, `draw_reward_info_ranking`) VALUES
    ('https://example.com/images/reward1.png', NULL, b'1', 1);

-- 2등 (5개)
INSERT INTO `draw_prize` (`image`, `owner`, `valid`, `draw_reward_info_ranking`) VALUES
                                                                                     ('https://example.com/images/reward2.png', NULL, b'1', 2),
                                                                                     ('https://example.com/images/reward2.png', NULL, b'1', 2),
                                                                                     ('https://example.com/images/reward2.png', NULL, b'1', 2),
                                                                                     ('https://example.com/images/reward2.png', NULL, b'1', 2),
                                                                                     ('https://example.com/images/reward2.png', NULL, b'1', 2);

-- 3등 (10개)
INSERT INTO `draw_prize` (`image`, `owner`, `valid`, `draw_reward_info_ranking`) VALUES
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3),
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3),
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3),
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3),
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3),
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3),
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3),
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3),
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3),
                                                                                     ('https://example.com/images/reward3.png', NULL, b'1', 3);

INSERT INTO `draw_prize` (`image`, `owner`, `valid`, `draw_reward_info_ranking`) VALUES
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4),
('https://example.com/images/reward4.png', NULL, b'1', 4);