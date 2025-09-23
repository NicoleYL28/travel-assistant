package oocl.travelassistant.service;

import oocl.travelassistant.dto.TravelPlanDTO;
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

    @Transactional
    public TravelPlan createTravelPlan(Long userId, TravelPlanDTO travelPlanDTO) {
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
                dailyPlan.setAccommodation(dailyPlanDTO.getAccommodation());
                dailyPlan.setDailyCost(dailyPlanDTO.getDailyCost());

                if (dailyPlanDTO.getMeals() != null) {
                    dailyPlan.setBreakfast(dailyPlanDTO.getMeals().getBreakfast());
                    dailyPlan.setLunch(dailyPlanDTO.getMeals().getLunch());
                    dailyPlan.setDinner(dailyPlanDTO.getMeals().getDinner());
                }

                if (dailyPlanDTO.getTransportation() != null) {
                    dailyPlan.setTransportationDetails(dailyPlanDTO.getTransportation().getDetails());
                    dailyPlan.setTransportationCost(dailyPlanDTO.getTransportation().getCost());
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

        return travelPlanRepository.save(savedTravelPlan);
    }

    private static TravelPlan getTravelPlan(Long userId, TravelPlanDTO travelPlanDTO) {
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setUserId(userId);
        travelPlan.setTitle(travelPlanDTO.getTitle());
        travelPlan.setOverview(travelPlanDTO.getOverview());
        travelPlan.setDuration(travelPlanDTO.getDuration());
        travelPlan.setTotalBudget(travelPlanDTO.getTotalBudget());

        if (travelPlanDTO.getBudgetBreakdown() != null) {
            travelPlan.setAccommodationBudget(travelPlanDTO.getBudgetBreakdown().getAccommodation());
            travelPlan.setFoodBudget(travelPlanDTO.getBudgetBreakdown().getFood());
            travelPlan.setTransportationBudget(travelPlanDTO.getBudgetBreakdown().getTransportation());
            travelPlan.setActivitiesBudget(travelPlanDTO.getBudgetBreakdown().getActivities());
            travelPlan.setShoppingBudget(travelPlanDTO.getBudgetBreakdown().getShopping());
            travelPlan.setOtherBudget(travelPlanDTO.getBudgetBreakdown().getOther());
        }
        return travelPlan;
    }

    public List<TravelPlan> getTravelPlansByUserId(Long userId) {
        return travelPlanRepository.findByUserId(userId);
    }

    public TravelPlan getTravelPlanById(Long id) {
        return travelPlanRepository.findById(id).orElse(null);
    }

    public TravelPlan getTravelPlanByIdAndUserId(Long id, Long userId) {
        TravelPlan travelPlan = travelPlanRepository.findById(id).orElse(null);
        if (travelPlan != null && !travelPlan.getUserId().equals(userId)) {
            return null; // 用户无权限访问此旅行计划
        }
        return travelPlan;
    }

    @Transactional
    public void deleteTravelPlan(Long id) {
        travelPlanRepository.deleteById(id);
    }

    @Transactional
    public boolean deleteTravelPlanByIdAndUserId(Long id, Long userId) {
        TravelPlan travelPlan = travelPlanRepository.findById(id).orElse(null);
        if (travelPlan == null) {
            return false; // 旅行计划不存在
        }
        if (!travelPlan.getUserId().equals(userId)) {
            return false; // 用户无权限删除此旅行计划
        }
        travelPlanRepository.deleteById(id);
        return true;
    }
}
