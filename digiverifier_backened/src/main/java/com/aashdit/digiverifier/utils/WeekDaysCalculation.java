/**
 * 
 */
package com.aashdit.digiverifier.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * Nambi
 */
@Service
public class WeekDaysCalculation {
	
	
	public long calculateWeekdays(LocalDate startDate, LocalDate endDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long weekdays = 0;

        for (int i = 0; i < totalDays; i++) {
            LocalDate currentDay = startDate.plusDays(i);
            DayOfWeek dayOfWeek = currentDay.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                weekdays++;
            }
        }

        return weekdays;
    }

	 
}
