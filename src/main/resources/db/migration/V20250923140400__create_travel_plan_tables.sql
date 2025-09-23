CREATE TABLE travel_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    overview TEXT,
    duration INT NOT NULL,
    total_budget DECIMAL(10,2),
    accommodation_budget DECIMAL(10,2),
    food_budget DECIMAL(10,2),
    transportation_budget DECIMAL(10,2),
    activities_budget DECIMAL(10,2),
    shopping_budget DECIMAL(10,2),
    other_budget DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE daily_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    travel_plan_id BIGINT NOT NULL,
    day_number INT NOT NULL,
    theme VARCHAR(255),
    morning TEXT,
    afternoon TEXT,
    evening TEXT,
    breakfast TEXT,
    lunch TEXT,
    dinner TEXT,
    accommodation TEXT,
    transportation_details TEXT,
    transportation_cost DECIMAL(10,2),
    daily_cost DECIMAL(10,2),
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plans(id) ON DELETE CASCADE
);

CREATE TABLE travel_tips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    travel_plan_id BIGINT NOT NULL,
    tip_content TEXT NOT NULL,
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plans(id) ON DELETE CASCADE
);