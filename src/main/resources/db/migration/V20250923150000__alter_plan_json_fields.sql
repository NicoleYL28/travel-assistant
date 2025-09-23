-- 将 accommodation、transportation、budgetBreakdown 字段改为 JSON 类型
ALTER TABLE daily_plans DROP COLUMN accommodation;
ALTER TABLE daily_plans DROP COLUMN transportation_details;
ALTER TABLE daily_plans DROP COLUMN transportation_cost;

ALTER TABLE daily_plans ADD COLUMN accommodation JSON;
ALTER TABLE daily_plans ADD COLUMN transportation JSON;
ALTER TABLE daily_plans ADD COLUMN date VARCHAR(255);


ALTER TABLE travel_plans DROP COLUMN accommodation_budget;
ALTER TABLE travel_plans DROP COLUMN food_budget;
ALTER TABLE travel_plans DROP COLUMN transportation_budget;
ALTER TABLE travel_plans DROP COLUMN activities_budget;
ALTER TABLE travel_plans DROP COLUMN shopping_budget;
ALTER TABLE travel_plans DROP COLUMN other_budget;
ALTER TABLE travel_plans ADD COLUMN budget_breakdown JSON;
