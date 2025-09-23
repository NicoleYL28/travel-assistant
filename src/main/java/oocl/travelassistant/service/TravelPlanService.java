package oocl.travelassistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oocl.travelassistant.dto.*;
import oocl.travelassistant.entity.DailyPlan;
import oocl.travelassistant.entity.TravelPlan;
import oocl.travelassistant.entity.TravelTip;
import oocl.travelassistant.exception.DataSerializationException;
import oocl.travelassistant.repository.TravelPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final ObjectMapper objectMapper;

    public TravelPlanService(TravelPlanRepository travelPlanRepository, ObjectMapper objectMapper) {
        this.travelPlanRepository = travelPlanRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TravelPlanDTO createTravelPlan(Long userId, TravelPlanDTO travelPlanDTO) {
        TravelPlan travelPlan = buildTravelPlanForCreate(userId, travelPlanDTO);
        TravelPlan savedTravelPlan = travelPlanRepository.save(travelPlan);

        if (travelPlanDTO.getDailyPlan() != null) {
            List<DailyPlan> dailyPlans = mapDailyPlansFromDTO(travelPlanDTO.getDailyPlan(), savedTravelPlan);
            savedTravelPlan.setDailyPlans(dailyPlans);
        }

        if (travelPlanDTO.getTips() != null) {
            savedTravelPlan.setTips(mapTipsFromDTO(travelPlanDTO.getTips(), savedTravelPlan));
        }

        TravelPlan result = travelPlanRepository.save(savedTravelPlan);
        return convertToDTO(result);
    }

    private TravelPlan buildTravelPlanForCreate(Long userId, TravelPlanDTO travelPlanDTO) {
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setUserId(userId);
        travelPlan.setTitle(travelPlanDTO.getTitle());
        travelPlan.setOverview(travelPlanDTO.getOverview());
        travelPlan.setDuration(travelPlanDTO.getDuration());
        travelPlan.setTotalBudget(travelPlanDTO.getTotalBudget());

        if (travelPlanDTO.getBudgetBreakdown() != null) {
            travelPlan.setBudgetBreakdown(serializeBudgetBreakdown(travelPlanDTO.getBudgetBreakdown()));
        }

        return travelPlan;
    }

    private String serializeBudgetBreakdown(BudgetBreakdownDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new DataSerializationException("预算信息序列化失败", e);
        }
    }

    private List<DailyPlan> mapDailyPlansFromDTO(List<DailyPlanDTO> dtos, TravelPlan parent) {
        return dtos.stream().map(d -> mapSingleDailyPlan(d, parent)).collect(Collectors.toCollection(ArrayList::new));
    }

    private DailyPlan mapSingleDailyPlan(DailyPlanDTO dto, TravelPlan parent) {
        DailyPlan dailyPlan = new DailyPlan();
        dailyPlan.setTravelPlan(parent);
        dailyPlan.setDayNumber(dto.getDay());
        dailyPlan.setTheme(dto.getTheme());
        dailyPlan.setMorning(dto.getMorning());
        dailyPlan.setAfternoon(dto.getAfternoon());
        dailyPlan.setEvening(dto.getEvening());
        dailyPlan.setDate(dto.getDate());
        dailyPlan.setDailyCost(dto.getDailyCost());

        if (dto.getMeals() != null) {
            dailyPlan.setBreakfast(dto.getMeals().getBreakfast());
            dailyPlan.setLunch(dto.getMeals().getLunch());
            dailyPlan.setDinner(dto.getMeals().getDinner());
        }

        try {
            if (dto.getAccommodation() != null) {
                dailyPlan.setAccommodation(objectMapper.writeValueAsString(dto.getAccommodation()));
            }
            if (dto.getTransportation() != null) {
                dailyPlan.setTransportation(objectMapper.writeValueAsString(dto.getTransportation()));
            }
        } catch (JsonProcessingException e) {
            throw new DataSerializationException("住宿或交通信息序列化失败", e);
        }

        return dailyPlan;
    }

    private List<TravelTip> mapTipsFromDTO(List<String> tips, TravelPlan parent) {
        return tips.stream().map(t -> {
            TravelTip tip = new TravelTip();
            tip.setTravelPlan(parent);
            tip.setTipContent(t);
            return tip;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<TravelPlanDTO> getTravelPlanDTOsByUserId(Long userId) {
        List<TravelPlan> plans = travelPlanRepository.findByUserId(userId);
        return plans.stream().map(this::convertToDTO).collect(Collectors.toCollection(ArrayList::new));
    }

    public TravelPlanDTO getTravelPlanDTOById(Long id) {
        TravelPlan plan = travelPlanRepository.findById(id).orElse(null);
        return plan != null ? convertToDTO(plan) : null;
    }

    @Transactional
    public boolean deleteTravelPlanByIdAndUserId(Long id, Long userId) {
        TravelPlan travelPlan = travelPlanRepository.findById(id).orElse(null);
        if (travelPlan == null) return false;
        if (!travelPlan.getUserId().equals(userId)) return false;
        travelPlanRepository.deleteById(id);
        return true;
    }

    @Transactional
    public TravelPlanDTO updateTravelPlan(Long id, Long userId, TravelPlanDTO travelPlanDTO) {
        TravelPlan existingPlan = travelPlanRepository.findById(id).orElse(null);
        if (existingPlan == null || !existingPlan.getUserId().equals(userId)) return null;

        // 完整更新基本信息（覆盖）
        existingPlan.setTitle(travelPlanDTO.getTitle());
        existingPlan.setOverview(travelPlanDTO.getOverview());
        existingPlan.setDuration(travelPlanDTO.getDuration());
        existingPlan.setTotalBudget(travelPlanDTO.getTotalBudget());

        existingPlan.setBudgetBreakdown(travelPlanDTO.getBudgetBreakdown() != null
                ? serializeBudgetBreakdown(travelPlanDTO.getBudgetBreakdown())
                : null);

        // 替换日程与提示
        existingPlan.setDailyPlans(travelPlanDTO.getDailyPlan() != null
                ? mapDailyPlansFromDTO(travelPlanDTO.getDailyPlan(), existingPlan)
                : new ArrayList<>());

        existingPlan.setTips(travelPlanDTO.getTips() != null
                ? mapTipsFromDTO(travelPlanDTO.getTips(), existingPlan)
                : new ArrayList<>());

        TravelPlan result = travelPlanRepository.save(existingPlan);
        return convertToDTO(result);
    }

    @Transactional
    public TravelPlanDTO partialUpdateTravelPlan(Long id, Long userId, TravelPlanDTO travelPlanDTO) {
        TravelPlan existingPlan = travelPlanRepository.findById(id).orElse(null);
        if (existingPlan == null || !existingPlan.getUserId().equals(userId)) return null;

        // 部分更新：仅更新非空字段
        if (travelPlanDTO.getTitle() != null) existingPlan.setTitle(travelPlanDTO.getTitle());
        if (travelPlanDTO.getOverview() != null) existingPlan.setOverview(travelPlanDTO.getOverview());
        if (travelPlanDTO.getDuration() != null) existingPlan.setDuration(travelPlanDTO.getDuration());
        if (travelPlanDTO.getTotalBudget() != null) existingPlan.setTotalBudget(travelPlanDTO.getTotalBudget());

        if (travelPlanDTO.getBudgetBreakdown() != null) {
            existingPlan.setBudgetBreakdown(serializeBudgetBreakdown(travelPlanDTO.getBudgetBreakdown()));
        }

        if (travelPlanDTO.getDailyPlan() != null) {
            updateDailyPlansIntelligently(existingPlan, travelPlanDTO.getDailyPlan());
        }

        if (travelPlanDTO.getTips() != null) {
            updateTipsIntelligently(existingPlan, travelPlanDTO.getTips());
        }

        TravelPlan result = travelPlanRepository.save(existingPlan);
        return convertToDTO(result);
    }

    private void updateDailyPlansIntelligently(TravelPlan existingPlan, List<DailyPlanDTO> newDailyPlanDTOs) {
        List<DailyPlan> existingDailyPlans = existingPlan.getDailyPlans();
        if (existingDailyPlans == null) {
            existingDailyPlans = new ArrayList<>();
            existingPlan.setDailyPlans(existingDailyPlans);
        }

        // 创建一个Map来快速查找现有的DailyPlan（以dayNumber为key）
        Map<Integer, DailyPlan> existingPlansByDay = existingDailyPlans.stream()
                .collect(Collectors.toMap(DailyPlan::getDayNumber, Function.identity()));

        // 收集需要保留的DayNumber
        Set<Integer> updatedDayNumbers = newDailyPlanDTOs.stream()
                .map(DailyPlanDTO::getDay)
                .collect(Collectors.toSet());

        // 移除不在更新列表中的DailyPlan
        existingDailyPlans.removeIf(plan -> !updatedDayNumbers.contains(plan.getDayNumber()));

        // 处理每个新的DailyPlanDTO
        for (DailyPlanDTO dto : newDailyPlanDTOs) {
            DailyPlan existingDailyPlan = existingPlansByDay.get(dto.getDay());

            if (existingDailyPlan != null) {
                // 更新现有记录
                updateExistingDailyPlan(existingDailyPlan, dto);
            } else {
                // 创建新记录
                DailyPlan newDailyPlan = mapSingleDailyPlan(dto, existingPlan);
                existingDailyPlans.add(newDailyPlan);
            }
        }
    }

    private void updateExistingDailyPlan(DailyPlan existingDailyPlan, DailyPlanDTO dto) {
        existingDailyPlan.setDayNumber(dto.getDay());
        existingDailyPlan.setTheme(dto.getTheme());
        existingDailyPlan.setMorning(dto.getMorning());
        existingDailyPlan.setAfternoon(dto.getAfternoon());
        existingDailyPlan.setEvening(dto.getEvening());
        existingDailyPlan.setDate(dto.getDate());
        existingDailyPlan.setDailyCost(dto.getDailyCost());

        // 更新餐食信息
        if (dto.getMeals() != null) {
            existingDailyPlan.setBreakfast(dto.getMeals().getBreakfast());
            existingDailyPlan.setLunch(dto.getMeals().getLunch());
            existingDailyPlan.setDinner(dto.getMeals().getDinner());
        }

        // 更新住宿和交通信息
        try {
            if (dto.getAccommodation() != null) {
                existingDailyPlan.setAccommodation(objectMapper.writeValueAsString(dto.getAccommodation()));
            }
            if (dto.getTransportation() != null) {
                existingDailyPlan.setTransportation(objectMapper.writeValueAsString(dto.getTransportation()));
            }
        } catch (JsonProcessingException e) {
            throw new DataSerializationException("住宿或交通信息序列化失败", e);
        }
    }

    private void updateTipsIntelligently(TravelPlan existingPlan, List<String> newTips) {
        List<TravelTip> existingTips = existingPlan.getTips();
        if (existingTips == null) {
            existingTips = new ArrayList<>();
            existingPlan.setTips(existingTips);
        }

        // 清除现有提示，重新设置（提示相对简单，直接替换）
        existingTips.clear();

        // 添加新提示
        for (String tipContent : newTips) {
            TravelTip tip = new TravelTip();
            tip.setTravelPlan(existingPlan);
            tip.setTipContent(tipContent);
            existingTips.add(tip);
        }
    }

    private TravelPlanDTO convertToDTO(TravelPlan plan) {
        TravelPlanDTO dto = new TravelPlanDTO();
        dto.setId(plan.getId());
        dto.setTitle(plan.getTitle());
        dto.setOverview(plan.getOverview());
        dto.setDuration(plan.getDuration());
        dto.setTotalBudget(plan.getTotalBudget());

        try {
            if (plan.getBudgetBreakdown() != null) {
                dto.setBudgetBreakdown(objectMapper.readValue(plan.getBudgetBreakdown(), BudgetBreakdownDTO.class));
            }
        } catch (Exception e) {
            dto.setBudgetBreakdown(null);
        }

        if (plan.getDailyPlans() != null) {
            dto.setDailyPlan(plan.getDailyPlans().stream().map(d -> {
                DailyPlanDTO dailyDTO = new DailyPlanDTO();
                dailyDTO.setDay(d.getDayNumber());
                dailyDTO.setTheme(d.getTheme());
                dailyDTO.setMorning(d.getMorning());
                dailyDTO.setAfternoon(d.getAfternoon());
                dailyDTO.setEvening(d.getEvening());
                dailyDTO.setDailyCost(d.getDailyCost());
                dailyDTO.setDate(d.getDate());

                try {
                    if (d.getAccommodation() != null) {
                        dailyDTO.setAccommodation(objectMapper.readValue(d.getAccommodation(), AccommodationDTO.class));
                    }
                    if (d.getTransportation() != null) {
                        dailyDTO.setTransportation(objectMapper.readValue(d.getTransportation(), TransportationDTO.class));
                    }
                } catch (Exception e) {
                    dailyDTO.setAccommodation(null);
                    dailyDTO.setTransportation(null);
                }

                MealsDTO meals = new MealsDTO();
                meals.setBreakfast(d.getBreakfast());
                meals.setLunch(d.getLunch());
                meals.setDinner(d.getDinner());
                dailyDTO.setMeals(meals);

                return dailyDTO;
            }).collect(Collectors.toCollection(ArrayList::new)));
        }

        if (plan.getTips() != null) {
            dto.setTips(plan.getTips().stream().map(TravelTip::getTipContent).collect(Collectors.toCollection(ArrayList::new)));
        }

        return dto;
    }
}
