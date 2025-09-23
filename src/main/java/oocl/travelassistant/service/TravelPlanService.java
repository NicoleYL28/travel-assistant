package oocl.travelassistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oocl.travelassistant.dto.*;
import oocl.travelassistant.entity.DailyPlan;
import oocl.travelassistant.entity.TravelPlan;
import oocl.travelassistant.entity.TravelTip;
import oocl.travelassistant.repository.TravelPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelPlanService {

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public TravelPlanDTO createTravelPlan(Long userId, TravelPlanDTO travelPlanDTO) {
        TravelPlan travelPlan = getTravelPlan(userId, travelPlanDTO);
        TravelPlan savedTravelPlan = travelPlanRepository.save(travelPlan);

        if (travelPlanDTO.getDailyPlan() != null) {
            List<DailyPlan> dailyPlans = travelPlanDTO.getDailyPlan().stream().map(dailyPlanDTO -> {
                DailyPlan dailyPlan = new DailyPlan();
                dailyPlan.setTravelPlan(savedTravelPlan);
                dailyPlan.setDayNumber(dailyPlanDTO.getDay());
                dailyPlan.setTheme(dailyPlanDTO.getTheme());
                dailyPlan.setMorning(dailyPlanDTO.getMorning());
                dailyPlan.setAfternoon(dailyPlanDTO.getAfternoon());
                dailyPlan.setEvening(dailyPlanDTO.getEvening());
                dailyPlan.setDate(dailyPlanDTO.getDate());

                try {
                    if (dailyPlanDTO.getAccommodation() != null) {
                        dailyPlan.setAccommodation(objectMapper.writeValueAsString(dailyPlanDTO.getAccommodation()));
                    }
                    if (dailyPlanDTO.getTransportation() != null) {
                        dailyPlan.setTransportation(objectMapper.writeValueAsString(dailyPlanDTO.getTransportation()));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("住宿或交通信息序列化失败");
                }

                dailyPlan.setDailyCost(dailyPlanDTO.getDailyCost());

                if (dailyPlanDTO.getMeals() != null) {
                    dailyPlan.setBreakfast(dailyPlanDTO.getMeals().getBreakfast());
                    dailyPlan.setLunch(dailyPlanDTO.getMeals().getLunch());
                    dailyPlan.setDinner(dailyPlanDTO.getMeals().getDinner());
                }

                return dailyPlan;
            }).collect(Collectors.toList());

            savedTravelPlan.setDailyPlans(dailyPlans);
        }

        if (travelPlanDTO.getTips() != null) {
            List<TravelTip> tips = travelPlanDTO.getTips().stream().map(tipContent -> {
                TravelTip tip = new TravelTip();
                tip.setTravelPlan(savedTravelPlan);
                tip.setTipContent(tipContent);
                return tip;
            }).collect(Collectors.toList());

            savedTravelPlan.setTips(tips);
        }

        TravelPlan result = travelPlanRepository.save(savedTravelPlan);
        return convertToDTO(result);
    }

    private TravelPlan getTravelPlan(Long userId, TravelPlanDTO travelPlanDTO) {
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setUserId(userId);
        travelPlan.setTitle(travelPlanDTO.getTitle());
        travelPlan.setOverview(travelPlanDTO.getOverview());
        travelPlan.setDuration(travelPlanDTO.getDuration());
        travelPlan.setTotalBudget(travelPlanDTO.getTotalBudget());

        try {
            if (travelPlanDTO.getBudgetBreakdown() != null) {
                travelPlan.setBudgetBreakdown(objectMapper.writeValueAsString(travelPlanDTO.getBudgetBreakdown()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("预算信息序列化失败");
        }

        return travelPlan;
    }

    public List<TravelPlan> getTravelPlansByUserId(Long userId) {
        return travelPlanRepository.findByUserId(userId);
    }

    public TravelPlan getTravelPlanById(Long id) {
        return travelPlanRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean deleteTravelPlanByIdAndUserId(Long id, Long userId) {
        TravelPlan travelPlan = travelPlanRepository.findById(id).orElse(null);
        if (travelPlan == null) {
            return false;
        }
        if (!travelPlan.getUserId().equals(userId)) {
            return false;
        }
        travelPlanRepository.deleteById(id);
        return true;
    }

    public List<TravelPlanDTO> getTravelPlanDTOsByUserId(Long userId) {
        List<TravelPlan> plans = travelPlanRepository.findByUserId(userId);
        return plans.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public TravelPlanDTO getTravelPlanDTOById(Long id) {
        TravelPlan plan = travelPlanRepository.findById(id).orElse(null);
        if (plan == null) return null;
        return convertToDTO(plan);
    }

    private TravelPlanDTO convertToDTO(TravelPlan plan) {
        TravelPlanDTO dto = new TravelPlanDTO();
        dto.setId(plan.getId());  // 添加 id 字段设置
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
            dto.setDailyPlan(plan.getDailyPlans().stream().map(dailyPlan -> {
                DailyPlanDTO dailyDTO = new DailyPlanDTO();
                dailyDTO.setDay(dailyPlan.getDayNumber());
                dailyDTO.setTheme(dailyPlan.getTheme());
                dailyDTO.setMorning(dailyPlan.getMorning());
                dailyDTO.setAfternoon(dailyPlan.getAfternoon());
                dailyDTO.setEvening(dailyPlan.getEvening());
                dailyDTO.setDailyCost(dailyPlan.getDailyCost());
                dailyDTO.setDate(dailyPlan.getDate());

                try {
                    if (dailyPlan.getAccommodation() != null) {
                        dailyDTO.setAccommodation(objectMapper.readValue(dailyPlan.getAccommodation(), AccommodationDTO.class));
                    }
                    if (dailyPlan.getTransportation() != null) {
                        dailyDTO.setTransportation(objectMapper.readValue(dailyPlan.getTransportation(), TransportationDTO.class));
                    }
                } catch (Exception e) {
                    dailyDTO.setAccommodation(null);
                    dailyDTO.setTransportation(null);
                }

                MealsDTO meals = new MealsDTO();
                meals.setBreakfast(dailyPlan.getBreakfast());
                meals.setLunch(dailyPlan.getLunch());
                meals.setDinner(dailyPlan.getDinner());
                dailyDTO.setMeals(meals);

                return dailyDTO;
            }).collect(Collectors.toList()));
        }

        if (plan.getTips() != null) {
            dto.setTips(plan.getTips().stream().map(TravelTip::getTipContent).collect(Collectors.toList()));
        }

        return dto;
    }
}
